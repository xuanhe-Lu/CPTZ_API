package com.ypm.service.aps;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.Manager;
import com.ypm.bean.UserCoupon;
import com.ypm.service.UserCouponService;

public class APiAt512 extends Abstract {

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
