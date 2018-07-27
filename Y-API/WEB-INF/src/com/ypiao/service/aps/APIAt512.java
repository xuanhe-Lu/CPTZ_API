package com.ypiao.service.aps;

import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.Manager;
import com.ypiao.bean.UserCoupon;
import com.ypiao.service.UserCouponService;

public class APIAt512 extends Abstract {

	private UserCouponService userCouponService;

	public UserCouponService getUserCouponService() {
		return userCouponService;
	}

	public void setUserCouponService(UserCouponService userCouponService) {
		this.userCouponService = userCouponService;
	}

	public void save(Manager mgr) {
		try {
			List<UserCoupon> fs = mgr.getList(UserCoupon.class);
			if (fs.size() >= 1) {
				long time = mgr.getLong("time");
				this.getUserCouponService().save(fs, time);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
