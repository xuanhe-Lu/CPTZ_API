package com.ypiao.service.imp;

import java.sql.SQLException;
import com.ypiao.service.*;
import com.ypiao.util.GMTime;

/**
 * 交易任务接口实现类. 
 */
public class TradeTaskServiceImp implements TradeTaskService {

	private AssetRawService assetRawService;

	private ProdInfoService prodInfoService;

	private UserCouponService userCouponService;

	private UserInfoService userInfoService;

	private UserOrderService userOrderService;

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

	public UserCouponService getUserCouponService() {
		return userCouponService;
	}

	public void setUserCouponService(UserCouponService userCouponService) {
		this.userCouponService = userCouponService;
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
			long time = System.currentTimeMillis();
			if (this.getProdInfoService().updateAuto(time) >= 1) {
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void schedule() {
		try {
			this.getUserInfoService().updateMonth();
			this.getUserOrderService().updateAny();
			this.getUserCouponService().expired();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
