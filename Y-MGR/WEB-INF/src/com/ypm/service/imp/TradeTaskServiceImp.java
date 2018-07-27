package com.ypm.service.imp;

import java.sql.SQLException;
import com.ypm.service.*;
import com.ypm.util.GMTime;

public class TradeTaskServiceImp implements TradeTaskService {

	private AssetRawService assetRawService;

	private ProdInfoService prodInfoService;

	private UserInfoService userInfoService;

	private UserOrderService userOrderService;

	public TradeTaskServiceImp() {
	}

	public AssetRawService getAssetRawService() {
		return assetRawService;
	}

	public void setAssetRawService(AssetRawService assetRawService) {
		this.assetRawService = assetRawService;
	}

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	/** 自动上架、起息处理 */
	public void doProd() {
		try {
			long time = GMTime.currentTimeMillis();
			if (this.getProdInfoService().updateAuto(time) >= 1) {
				this.getProdInfoService().saveOver();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void schedule() {
		try {
			this.getUserInfoService().updateMonth();
			this.getUserOrderService().updateAny();
			this.getUserOrderService().doReceived();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
