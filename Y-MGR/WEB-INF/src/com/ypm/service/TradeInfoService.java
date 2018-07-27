package com.ypm.service;

import java.sql.SQLException;
import com.ypm.bean.LogOrder;
import com.ypm.bean.SysOrder;

public interface TradeInfoService {

	// ==================== APS 接口层 ====================
	public void save(LogOrder order) throws SQLException;

	public void save(SysOrder order) throws SQLException;
	// ==================== API 接口层 ====================
}
