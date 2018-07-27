package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Xues;

/**
 * 票友学堂数据接口定义. 
 */
public interface XuerInfoService {

	public void sendIndex(AjaxInfo json) throws SQLException;
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 票友学堂数据列表获取
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException;
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 票友学堂单条记录获取
	 */
	public AjaxInfo sendByOne(AjaxInfo json, String sid) throws SQLException;
	
	/**
	 * @author xk
	 * @param xues is Xues
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Xues xues) throws SQLException;
	
	/**
	 * @author xk
	 * @param sid String
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException;
	
	/**
	 * @author xk
	 * @param sid String
	 * @return Xues
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public Xues findXuesBySId(String sid) throws SQLException;

}
