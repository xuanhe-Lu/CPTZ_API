package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.LogCharge;
import com.ypiao.bean.UserProto;
import com.ypiao.bean.UserRmbs;

public interface UserChargeService {

	// ==================== APS 接口层 ====================
	public void save(LogCharge c) throws SQLException;

	public void save(UserProto p) throws SQLException;

	public int update(String CNo, int state, long time) throws SQLException;
	// ==================== API 接口层 ====================
	public LogCharge findChargeBySid(long sid) throws SQLException;

	public UserProto findProtoByUid(long uid) throws SQLException;

	public void saveLog(LogCharge c) throws SQLException;

	public void bindProto(LogCharge c, String SNo) throws SQLException;

	public void unBindProto(UserProto proto) throws SQLException;
	/** 充值确认操作 */
	public UserRmbs recharge(LogCharge c) throws SQLException;
}
