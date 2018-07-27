package com.ypm.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ypm.bean.UserAuth;

public interface UserAuthService {

	// ==================== APS 接口层 ====================
	public void save(Connection conn, UserAuth a) throws SQLException;

	public void save(UserAuth a) throws SQLException;
	// ==================== API 接口层 ====================

	public UserAuth findAuthByUid(long uid) throws SQLException;
}
