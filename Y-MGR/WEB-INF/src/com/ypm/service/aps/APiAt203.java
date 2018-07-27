package com.ypm.service.aps;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AutoRaws;
import com.ypm.bean.Manager;
import com.ypm.service.AssetRawService;
import com.ypm.service.ProdInfoService;

public class APiAt203 extends Abstract {

	private AssetRawService assetRawService;

	private ProdInfoService prodInfoService;

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

	public void over(Manager mgr) {
		try {
			List<AutoRaws> ls = mgr.getList(AutoRaws.class);
			if (ls.size() >= 1) {
				this.getAssetRawService().save(ls);
				this.getProdInfoService().save(ls);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void saveAuto(Manager mgr) {
		try {
			long Pid = mgr.getLong("pid");
			long time = mgr.getLong("time");
			this.getProdInfoService().update(Pid, time);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
