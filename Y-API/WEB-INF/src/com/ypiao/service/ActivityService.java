package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.ActInfo;
import com.ypiao.bean.LogOrder;
import com.ypiao.bean.ProdInfo;

public interface ActivityService {

	public void save(ActInfo info) throws SQLException;

	public int update(String ids, int state, long time) throws SQLException;

	/** 根据订单参与活动 */
	public void take(int adj, String mobile, LogOrder log) throws SQLException;

	/** 活动->邀请->返现 */
	public void take(ProdInfo info, LogOrder log) throws SQLException;
}
