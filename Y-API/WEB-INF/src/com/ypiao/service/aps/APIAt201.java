package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.Company;
import com.ypiao.bean.Manager;
import com.ypiao.service.AssetComService;

public class APIAt201 extends Abstract {

	private AssetComService assetComService;

	public AssetComService getAssetComService() {
		return assetComService;
	}

	public void setAssetComService(AssetComService assetComService) {
		this.assetComService = assetComService;
	}

	public void save(Manager mgr) {
		try {
			Company c = mgr.getObject(Company.class);
			this.getAssetComService().save(c);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void remove(Manager mgr) {
		try {
			int cid = mgr.getInt("cid");
			this.getAssetComService().remove(cid);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

}
