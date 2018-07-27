package com.ypiao.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.LogOrder;
import com.ypiao.bean.ProdOrder;

public interface UserOrderService {

	// ==================== APS 接口层 ====================
	public int adds(Connection conn, LogOrder log) throws SQLException;

	public void saveAuto(LogOrder log) throws SQLException;

	/** 更新倒计时间 */
	public void updateAny() throws SQLException;

	// ==================== API 接口层 ====================
	public List<ProdOrder> findOrderByPid(long Pid) throws SQLException;

	public boolean isNewByUid(long uid) throws SQLException;
}
