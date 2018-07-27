package com.ypm.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.UserCoupon;

public interface UserCouponService {

	// ==================== APS 接口层 ====================
	public void save(List<UserCoupon> fs, long time) throws SQLException;

	public int update(Connection conn, long sid, long uid, long ordId, long time) throws SQLException;
	// ==================== API 接口层 ====================
}
