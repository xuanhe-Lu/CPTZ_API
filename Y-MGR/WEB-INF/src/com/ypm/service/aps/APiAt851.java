package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.LogOrder;
import com.ypm.bean.Manager;
import com.ypm.service.UserOrderService;

public class APiAt851 extends Abstract {

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
