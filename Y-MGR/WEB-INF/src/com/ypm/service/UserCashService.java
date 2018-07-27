package com.ypm.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.ExportInfo;
import com.ypm.bean.UserCash;
import com.ypm.bean.UserSession;

public interface UserCashService {

	// ==================== APS 接口层 ====================
	public void save(UserCash c) throws SQLException;

	public void update(UserCash c) throws SQLException;

	public void update(List<UserCash> ls) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findCashByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo loadCashBySum(AjaxInfo json, StringBuilder sql, List<Object> fs) throws SQLException;

	public UserCash findCashBySid(long sid) throws SQLException;

	/** 同意提现处理 */
	public void agree(UserSession us, String ids) throws SQLException;

	/** 拒绝提现处理 */
	public void refuse(UserSession us, UserCash c) throws SQLException;

	/** 提现信息导出 */
	public boolean exportByOut(ExportInfo info) throws SQLException, IOException;
}
