package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.CouponInfo;
import com.ypm.bean.CouponRule;
import com.ypm.bean.CouponUser;

public interface CouponInfoService {

	// ==================== APS 接口层 ====================
	public void save(CouponInfo info) throws SQLException;

	public void save(CouponRule rule) throws SQLException;

	public void save(List<CouponUser> ls) throws SQLException;

	public void saveSok(String ids, long time) throws SQLException;

	public int saveQM(CouponRule rule) throws SQLException;

	public int update(long rid, int state, long time) throws SQLException;

	public void delete(long rid, String ids) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findCouponByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public CouponInfo findCouponByCid(int cid) throws SQLException;

	public void saveInfo(CouponInfo info) throws SQLException;

	public void saveState(String ids, int state) throws SQLException;

	public AjaxInfo findCouponByRid(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo findCouponByRid(long rid, String order, int offset, int max);

	public CouponRule findCouponByRid(long rid) throws SQLException;

	public void saveInfo(CouponRule rule) throws SQLException;

	public void saveSok(String ids) throws SQLException;

	public void addUser(long rid, String user) throws SQLException;

	public void delUser(long rid, String ids) throws SQLException;

	/** 拒绝发放 */
	public void refuse(CouponRule rule) throws SQLException;

	/** 发放券码 */
	public void sendQM(CouponRule rule) throws SQLException;
}
