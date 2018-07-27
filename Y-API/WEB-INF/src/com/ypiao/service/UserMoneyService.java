package com.ypiao.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserRmbs;

public interface UserMoneyService {

	// ==================== APS 接口层 ====================
	public int insert(Connection conn, UserRmbs r) throws SQLException;

	public int update(Connection conn, UserRmbs r) throws SQLException;

	public void save(Connection conn, UserRmbs r) throws SQLException;

	public void save(UserRmbs rmb) throws SQLException;

	public void share(Connection conn, UserRmbs r) throws SQLException;

	public void share(UserRmbs rmb) throws SQLException;

	// ==================== API 接口层 ====================
	public UserRmbs findMoneyByUid(Connection conn, long uid) throws SQLException;

	public UserRmbs findMoneyBySid(long sid, long uid) throws SQLException;

	public UserRmbs findMoneyByUid(long uid) throws SQLException;

	public AjaxInfo sendMoneyByUid(AjaxInfo json, long uid) throws SQLException;
}