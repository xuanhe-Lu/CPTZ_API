package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.Manager;
import com.ypm.bean.UserReger;
import com.ypm.service.UserInfoService;

public class APiAt120 extends Abstract {

	private UserInfoService userInfoService;

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public void reg(Manager mgr) {
		try {
			UserReger reg = mgr.getObject(UserReger.class);
			if (reg.getUid() >= USER_UID_BEG) {
				this.getUserInfoService().reg(reg); // 不同步
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
