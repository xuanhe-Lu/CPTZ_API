package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.UserInfo;

public interface TriggerService {

	// ==================== API 接口层 ====================
	/** 注册触发 */
	public void register(UserInfo info) throws SQLException;

	/** 首次绑卡 */
	public void bindCard(long uid, long time) throws SQLException;

	/** 首次邀请 */
	public void invite(long uid, long time) throws SQLException;
}
