package com.ypiao.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ypiao.bean.Company;

public interface AssetComService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, int cid, int row) throws SQLException;

	public void save(Company c) throws SQLException;

	public void remove(int cid) throws SQLException;
	// ==================== API 接口层 ====================

}
