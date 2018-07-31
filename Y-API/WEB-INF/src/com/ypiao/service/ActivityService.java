package com.ypiao.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ypiao.bean.*;

public interface ActivityService {

	public void save(ActInfo info) throws SQLException;

	public int update(String ids, int state, long time) throws SQLException;

	/** 根据订单参与活动 */
	public void take(int adj, String mobile, LogOrder log) throws SQLException;

	/** 活动->邀请->返现 */
	public void take(ProdInfo info, LogOrder log) throws SQLException;

	/**
	 *
	 * @param info
	 * @param log
	 * @throws SQLException
	 */
	public void activityForVipBuy(ProdInfo info, LogOrder log) throws SQLException;


	/**
	 *  根据邀请人查找受邀人的数量
	 * @param ups
	 * @return
	 * @throws SQLException
	 */
	public int qryUserByUps(long ups ) throws SQLException;

	/**
	 * 根据uid 查找符合累计投资满1W的受邀人
	 * @param ups
	 * @return
	 * @throws SQLException
	 */
	public List<String> qryUserByUid(long ups ) throws SQLException;

	/**
	 * 根据event查找受邀人中的白银会员和黄金会员
	 * @param ups
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,Object>> qryUserVipByUid(long ups ) throws SQLException;
	public List<UserRmbs> qryInvestByUidAndEvent(long ups ) throws SQLException;
}
