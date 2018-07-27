package com.ypm.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ypm.bean.LogCharge;
import com.ypm.bean.UserRmbs;

public interface UserMoneyService {

	// ==================== APS 接口层 ====================
	public int insert(Connection conn, UserRmbs r) throws SQLException;

	public int update(Connection conn, UserRmbs r) throws SQLException;
	public int updateYpiao(Connection conn, UserRmbs r) throws SQLException;
	public int insertYpiao(Connection conn, UserRmbs r) throws SQLException;
	public void save(Connection conn, UserRmbs r) throws SQLException;

	public void save(LogCharge c) throws SQLException;

	public void save(UserRmbs rmb) throws SQLException;

	public void share(Connection conn, UserRmbs r) throws SQLException;

	public void share(UserRmbs rmb) throws SQLException;

	// ==================== API 接口层 ====================
	public UserRmbs findMoneyByUid(Connection conn, long uid) throws SQLException;
	public UserRmbs findYpiaoMoneyByUid(Connection conn, long uid) throws SQLException;
}
