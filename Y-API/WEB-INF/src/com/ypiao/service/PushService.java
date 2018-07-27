package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.Push;

/**
 * 推送管理APS接口定义.
 * 
 * Created by xk on 2018-06-06.
 */
public interface PushService {

	/**
	 * @param push is Push
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Push push) throws SQLException;

	/**
	 * @param sid long
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(long sid) throws SQLException;
}
