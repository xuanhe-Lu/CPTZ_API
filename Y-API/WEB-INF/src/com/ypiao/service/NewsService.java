package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.News;

/**
 * 新闻管理数据接口定义. 
 */
public interface NewsService {
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 新闻数据列表获取
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException;
	
	/**
	 * @author xk
	 * @param news is News
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(News news) throws SQLException;
	
	/**
	 * @author xk
	 * @param sid String
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException;

}
