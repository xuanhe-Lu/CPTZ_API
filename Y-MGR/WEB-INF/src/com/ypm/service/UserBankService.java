package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserBank;
import com.ypm.bean.UserBker;

public interface UserBankService {

	// ==================== APS 接口层 ====================
	public void save(UserBank b) throws SQLException;

	public void save(UserBker b) throws SQLException;

	public void used(UserBker b) throws SQLException;
	// ==================== API 接口层 ====================
	public AjaxInfo findBankByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public UserBker findBkerBySid(long sid) throws SQLException;

	public void update(UserBker b) throws SQLException;
}
