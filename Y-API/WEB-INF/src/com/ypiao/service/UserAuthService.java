package com.ypiao.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ypiao.bean.UserAuth;

public interface UserAuthService {

	// ==================== APS 接口层 ====================
	public void save(Connection conn, UserAuth a) throws SQLException;

	public void save(UserAuth a) throws SQLException;

	// ==================== API 接口层 ====================
	public UserAuth findAuthByUid(Connection conn, long uid) throws SQLException;

	public UserAuth findAuthByUid(long uid) throws SQLException;

	public boolean isAuthByIdCard(String idCard);

	public void saveAuth(UserAuth a) throws SQLException;
}
