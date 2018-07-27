package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.Manager;
import com.ypm.service.UserInfoService;

public class APiAt123 extends Abstract {

	private UserInfoService userInfoService;

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public void modPwd(Manager mgr) {
		String Pwd = mgr.getString("pwd");
		try {
			long uid = mgr.getLong("uid");
			long time = mgr.getLong("time");
			if (uid >= USER_UID_BEG) {
				this.getUserInfoService().updatePwd(uid, Pwd, time);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			Pwd = null;
		}
	}
}
