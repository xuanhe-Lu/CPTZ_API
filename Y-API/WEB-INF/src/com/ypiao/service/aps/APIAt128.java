package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.Manager;
import com.ypiao.bean.UserRmbs;
import com.ypiao.service.UserMoneyService;

public class APIAt128 extends Abstract {

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
