package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.LogOrder;
import com.ypiao.bean.Manager;
import com.ypiao.service.UserOrderService;

public class APIAt851 extends Abstract {

	private UserOrderService userOrderService;

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	public void saveAuto(Manager mgr) {
		try {
			LogOrder log = mgr.getObject(LogOrder.class);
			this.getUserOrderService().saveAuto(log);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
