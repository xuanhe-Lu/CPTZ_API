package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.Manager;
import com.ypiao.bean.UserReger;
import com.ypiao.service.UserInfoService;

public class APIAt120 extends Abstract {

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
