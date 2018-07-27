package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.AdsInfo;
import com.ypiao.bean.Manager;
import com.ypiao.service.AderInfoService;

public class APIAt501 extends Abstract {

	private AderInfoService aderInfoService;

	public AderInfoService getAderInfoService() {
		return aderInfoService;
	}

	public void setAderInfoService(AderInfoService aderInfoService) {
		this.aderInfoService = aderInfoService;
	}

	public void save(Manager mgr) {
		try {
			AdsInfo ads = mgr.getObject(AdsInfo.class);
			this.getAderInfoService().save(ads);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void order(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			long time = mgr.getLong("time");
			this.getAderInfoService().order(ids, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void state(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			int state = mgr.getInt("state");
			long time = mgr.getLong("time");
			this.getAderInfoService().state(ids, state, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void remove(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			this.getAderInfoService().remove(ids);
		} catch (SQLException e) {
			mgr.addError(DATA_DELETE_FAILED);
		} finally {
			ids = null;
		}
	}
}
