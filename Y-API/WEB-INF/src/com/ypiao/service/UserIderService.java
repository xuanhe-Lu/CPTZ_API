package com.ypiao.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ypiao.bean.UserIder;

public interface UserIderService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, long uid, String sm, long time) throws SQLException;

	// ==================== API 接口层 ====================
	public UserIder getUserIder(String mobile) throws SQLException;
	/** 手机号对应UId号 */
	public UserIder findUserIder(String mobile) throws SQLException;
}
