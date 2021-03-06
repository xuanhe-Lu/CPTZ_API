package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.ActInfo;
import com.ypm.bean.Manager;
import com.ypm.service.ActivityService;

public class APiAt853 extends Abstract {

	private ActivityService activityService;

	public ActivityService getActivityService() {
		return activityService;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public void saveInfo(Manager mgr) {
		try {
			ActInfo info = mgr.getObject(ActInfo.class);
			if (info.getAdj() >= 1) {
				this.getActivityService().save(info);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void saveState(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			int state = mgr.getInt("state");
			long time = mgr.getLong("time");
			this.getActivityService().update(ids, state, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			ids = null;
		}
	}
}
