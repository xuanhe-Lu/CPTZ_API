package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.Manager;
import com.ypiao.bean.UserAuth;
import com.ypiao.service.UserAuthService;

public class APIAt124 extends Abstract {

	private UserAuthService userAuthService;

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	public void save(Manager mgr) {
		try {
			UserAuth a = mgr.getObject(UserAuth.class);
			if (a.getUid() >= USER_UID_BEG) {
				this.getUserAuthService().save(a);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
