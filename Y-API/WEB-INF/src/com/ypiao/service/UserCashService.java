package com.ypiao.service;

import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserCash;

public interface UserCashService {

	// ==================== APS 接口层 ====================
	public void save(UserCash c) throws SQLException;

	public void update(UserCash c) throws SQLException;

	public void update(List<UserCash> ls) throws SQLException;
	// ==================== API 接口层 ====================
	/** 提现操作 */
	public boolean commit(UserCash c) throws SQLException;

	public AjaxInfo sendByUid(AjaxInfo json, long uid) throws SQLException;
}
