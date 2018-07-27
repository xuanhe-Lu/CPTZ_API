package com.ypm.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.AutoRaws;
import com.ypm.bean.LogOrder;

public interface UserOrderService {

	// ==================== APS 接口层 ====================
	public int adds(Connection conn, LogOrder log) throws SQLException;

	public int addsYpiao(Connection conn, LogOrder log) throws SQLException;

	public void save(LogOrder log) throws SQLException;

	public void saveAuto(LogOrder log) throws SQLException;

	public void saveYpiao(LogOrder log) throws SQLException;
	/** 更新倒计时间 */
	public void updateAny() throws SQLException;

	public void doReceived() throws SQLException;

	// ==================== API 接口层 ====================
	public AutoRaws compute(AutoRaws r) throws SQLException;

	public AjaxInfo findBookByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo findOrderByAll(StringBuilder sql, List<Object> fs, boolean join, String order, int offset, int max);

}
