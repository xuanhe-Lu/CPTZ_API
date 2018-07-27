package com.ypiao.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserCoupon;

public interface UserCouponService {

	// ==================== APS 接口层 ====================
	/** 更新过期优惠券 */
	public void expired() throws SQLException;

	public void save(List<UserCoupon> fs, long time) throws SQLException;

	public int update(Connection conn, long sid, long uid, long ordId, long time) throws SQLException;

	// ==================== API 接口层 ====================
	public UserCoupon findCouponBySid(long sid) throws SQLException;

	public int loadByUid(AjaxInfo json, long uid, int day) throws SQLException;

	public AjaxInfo sendByUid(AjaxInfo json, long uid, int state) throws SQLException;
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @param uid long
	 * @param state int
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 优惠券弹窗
	 */
	public AjaxInfo alertByUid(AjaxInfo json, long uid, int state) throws SQLException;
}
