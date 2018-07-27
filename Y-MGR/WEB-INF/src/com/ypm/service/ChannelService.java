package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Channel;

/**
 * 渠道统计业务层接口.
 * 
 * Created by xk on 2018-05-18.
 */
public interface ChannelService {

	/**
	 * @param channel is Channel
	 * @return int
	 * @throws SQLException
	 * 
	 * 更新动态数据 
	 */
	public int update(Channel channel) throws SQLException;

	/**
	 * @param sql StringBuilder
	 * @param fs List<Object>
	 * @param order String
	 * @param offset int
	 * @param max int
	 * @return AjaxInfo
	 * 
	 * 数据列表
	 */
	public AjaxInfo findChannelByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);
	
	/**
	 * @param sql StringBuilder
	 * @return AjaxInfo
	 * 
	 * 列表，通过传递纯sql参数方式获取
	 */
	public AjaxInfo findChannelByAll(StringBuilder sql);

	/**
	 * @param sid int
	 * @return Channel
	 * @throws SQLException
	 * 
	 * 根据sid获取记录
	 */
	public Channel findChannelBySId(int sid) throws SQLException;

	/**
	 * @param channel is Channel
	 * @throws IOException, SQLException
	 * 
	 * 新增/保存记录
	 */
	public void saveChannel(Channel channel) throws IOException, SQLException;

	/**
	 * @param sid int
	 * @return boolean
	 * @throws SQLException
	 * 
	 * 根据sid删除记录
	 */
	public boolean remove(int sid) throws SQLException;
	
	/**
	 * @param cid int 渠道编号
	 * @param ps is PrepareStatement
	 * @param conn is Connection
	 * @return Map<String, Object> <字段名key, 字段值val>
	 * @throws SQLException 
	 * 
	 * 根据渠道编号获取渠道的动态数据字段
	 */
	public Map<String, Object> getDynamicField(int cid, PreparedStatement ps, Connection conn) throws SQLException;

	/**
	 * @return Map<String, Object> <总统计字段名key, 总统计字段值val>
	 * @throws SQLException 
	 * 
	 * 汇总渠道总统计数据
	 */
	public Map<String, Object> sum() throws SQLException;

	/**
	 * @return String 
	 * @throws SQLException
	 * 
	 * 新增渠道，获取渠道编号
	 */
	public int getNextSid() throws SQLException;
	
	/**
	 * @return AjaxInfo
	 * 
	 * 渠道名称下拉数据
	 */
	public AjaxInfo getRinfos();

}
