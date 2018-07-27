package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.LogCharge;
import com.ypm.bean.Manager;
import com.ypm.bean.UserProto;
import com.ypm.service.UserChargeService;

public class APiAt129 extends Abstract {

	private UserChargeService userChargeService;

	public UserChargeService getUserChargeService() {
		return userChargeService;
	}

	public void setUserChargeService(UserChargeService userChargeService) {
		this.userChargeService = userChargeService;
	}

	public void charge(Manager mgr) {
		try {
			LogCharge c = mgr.getObject(LogCharge.class);
			this.getUserChargeService().save(c);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void proto(Manager mgr) {
		try {
			UserProto p = mgr.getObject(UserProto.class);
			this.getUserChargeService().save(p);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void unBind(Manager mgr) {
		String CNo = mgr.getString("cno");
		try {
			long time = mgr.getLong("time");
			if (CNo != null && time > USER_TIME) {
				int state = mgr.getInt("state");
				this.getUserChargeService().update(CNo, state, time);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			CNo = null;
		}
	}
}
