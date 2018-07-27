package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Help;

/**
 * Created by xk on 2018-05-10.
 * 
 * 常见问题信息同步APS接口定义.
 */
public interface HelpInfoService {

	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException;
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 常见问题数据列表获取
	 */
	public AjaxInfo sendByList(AjaxInfo json) throws SQLException;
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 根据常见问题分类id获取数据
	 */
	public AjaxInfo listByTid(AjaxInfo json, int tid) throws SQLException;
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 根据常见问题分类id获取数据
	 */
	public AjaxInfo listByType(AjaxInfo json, int type) throws SQLException;

	/**
	 * @author xk
	 * @param help is Help
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Help help) throws SQLException;
	
	/**
	 * @author xk
	 * @param sid string
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException;
}
