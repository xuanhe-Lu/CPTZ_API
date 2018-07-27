package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.UserInfo;
import com.ypiao.bean.UserReger;
import com.ypiao.bean.UserSession;

public interface UserLogerService {

	// ==================== APS 接口层 ====================
	public int update(UserSession us) throws SQLException;

	// ==================== API 接口层 ====================
	public void register(UserReger reg) throws SQLException;

	public UserInfo getUserInfoByAcc(String fix, String acc) throws SQLException;

	public UserSession getAccessByUid(long uid) throws SQLException;

	public UserSession getAccessToken(long uid, long stime, String token);

	public void login(UserSession us, String dev, String model) throws SQLException;

	public void logout(UserSession us) throws SQLException;
}
