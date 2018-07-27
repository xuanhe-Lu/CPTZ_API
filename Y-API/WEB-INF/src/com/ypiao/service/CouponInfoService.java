package com.ypiao.service;

import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.CouponInfo;
import com.ypiao.bean.CouponRule;
import com.ypiao.bean.CouponUser;

public interface CouponInfoService {

	// ==================== APS 接口层 ====================
	public void save(CouponInfo info) throws SQLException;

	public void save(CouponRule rule) throws SQLException;

	public void save(List<CouponUser> ls) throws SQLException;

	public void saveSok(String ids, long time) throws SQLException;

	public int saveQM(CouponRule rule) throws SQLException;

	public int update(long rid, int state, long time) throws SQLException;

	public void delete(long rid, String ids) throws SQLException;

	public void state(String ids, int state, long time) throws SQLException;

	// ==================== API 接口层 ====================
	public List<CouponInfo> findCouponByTid(int tid, long time) throws SQLException;

	public void sendByUid(AjaxInfo json, long uid);
}