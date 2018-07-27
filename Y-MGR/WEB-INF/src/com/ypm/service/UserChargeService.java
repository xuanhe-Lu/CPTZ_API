package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.LogCharge;
import com.ypm.bean.UserProto;

public interface UserChargeService {

	// ==================== APS 接口层 ====================
	public void save(LogCharge c) throws SQLException;

	public void save(UserProto p) throws SQLException;

	public int update(String CNo, int state, long time) throws SQLException;
	// ==================== API 接口层 ====================
	public AjaxInfo findOrderByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo loadOrderBySum(AjaxInfo json, StringBuilder sql, List<Object> fs) throws SQLException;
}
