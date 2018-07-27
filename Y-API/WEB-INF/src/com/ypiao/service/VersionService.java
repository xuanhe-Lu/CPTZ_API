package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.Version;

/**
 * app版本更新APS接口定义.
 * 
 * Created by xk on 2018-06-06.
 */
public interface VersionService {

	/**
	 * @param version is Version
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Version version) throws SQLException;

	/**
	 * @param sid String
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException;
}
