package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.Manager;
import com.ypiao.bean.RegionInfo;
import com.ypiao.service.SiteAreaService;

public class APIAt990 extends Abstract {

	private SiteAreaService siteAreaService;

	public SiteAreaService getSiteAreaService() {
		return siteAreaService;
	}

	public void setSiteAreaService(SiteAreaService siteAreaService) {
		this.siteAreaService = siteAreaService;
	}

	public void remove(Manager mgr) {
		Logger.info(mgr.getString("code"));
	}

	public void sync(Manager mgr) {
		try {
			RegionInfo info = mgr.getObject(RegionInfo.class);
			this.getSiteAreaService().saveRegion(info);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}
}
