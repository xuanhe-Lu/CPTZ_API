package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.InfoVIPS;
import com.ypiao.bean.Manager;
import com.ypiao.service.UserRoleService;

public class APIAt901 extends Abstract {

	private UserRoleService userRoleService;

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	public void saveVIP(Manager mgr) {
		try {
			InfoVIPS v = mgr.getObject(InfoVIPS.class);
			this.getUserRoleService().save(v);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
