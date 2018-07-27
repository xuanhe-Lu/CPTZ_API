package com.ypm.service.aps;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.Manager;
import com.ypm.bean.UserCash;
import com.ypm.service.UserCashService;

public class APiAt880 extends Abstract {

	private UserCashService userCashService;

	public UserCashService getUserCashService() {
		return userCashService;
	}

	public void setUserCashService(UserCashService userCashService) {
		this.userCashService = userCashService;
	}

	public void agree(Manager mgr) {
		try {
			List<UserCash> ls = mgr.getList(UserCash.class);
			if (ls.size() >= 1) {
				this.getUserCashService().update(ls);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

	public void refuse(Manager mgr) {
		try {
			UserCash c = mgr.getObject(UserCash.class);
			this.getUserCashService().update(c);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}

	public void save(Manager mgr) {
		try {
			UserCash c = mgr.getObject(UserCash.class);
			this.getUserCashService().save(c);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
