package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Welfare;

/**
 * Created by xk on 2018-06-12.
 * 
 * 福利专区数据接口定义.
 */
public interface WelfareService {
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 福利专区数据列表获取
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException;
	
	/**
	 * @author xk
	 * @param welfare is Welfare
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Welfare welfare) throws SQLException;
	
	/**
	 * @author xk
	 * @param sid String
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException;

}
