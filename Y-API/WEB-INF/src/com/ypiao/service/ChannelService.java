package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.Channel;

/**
 * 渠道统计APS接口定义.
 * 
 * Created by xk on 2018-05-18.
 */
public interface ChannelService {

	/**
	 * @param channel is Channel
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Channel channel) throws SQLException;

	/**
	 * @param sid int
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(int sid) throws SQLException;
}
