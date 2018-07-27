package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.Manager;
import com.ypm.bean.UserAuth;
import com.ypm.service.UserAuthService;

public class APiAt124 extends Abstract {

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
