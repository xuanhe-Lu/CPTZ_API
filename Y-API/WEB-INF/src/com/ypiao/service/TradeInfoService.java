package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.LogOrder;
import com.ypiao.bean.ProdInfo;
import com.ypiao.bean.SysOrder;
import com.ypiao.bean.UserCoupon;

public interface TradeInfoService {

	// ==================== APS 接口层 ====================
	public void save(LogOrder order) throws SQLException;

	public void save(SysOrder order) throws SQLException;
	// ==================== API 接口层 ====================
	public int commit(ProdInfo info, LogOrder log, UserCoupon uc) throws SQLException;

	public void saveOrder(SysOrder order) throws SQLException;

	public boolean isProdByAll(long uid, int tid, int total) throws SQLException;

	public boolean isProdByPid(long uid, long Pid, int total) throws SQLException;

	public LogOrder findLogOrderBySid(long sid) throws SQLException;

	public SysOrder findSysOrderBySid(long sid) throws SQLException;

	public AjaxInfo sendTreadByUid(AjaxInfo json, long uid, int state) throws SQLException;
}
