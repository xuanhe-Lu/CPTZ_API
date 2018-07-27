package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.Manager;
import com.ypm.bean.UserRmbs;
import com.ypm.service.UserMoneyService;

public class APiAt128 extends Abstract {

	private UserMoneyService userMoneyService;

	public UserMoneyService getUserMoneyService() {
		return userMoneyService;
	}

	public void setUserMoneyService(UserMoneyService userMoneyService) {
		this.userMoneyService = userMoneyService;
	}

	public void save(Manager mgr) {
		try {
			UserRmbs rmb = mgr.getObject(UserRmbs.class);
			this.getUserMoneyService().save(rmb);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void share(Manager mgr) {
		try {
			UserRmbs rmb = mgr.getObject(UserRmbs.class);
			this.getUserMoneyService().share(rmb);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
