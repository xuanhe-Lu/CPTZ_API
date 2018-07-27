package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Help;

/**
 * 常见问题业务层接口定义.
 * 
 * Created by xk on 2018-05-09.
 */
public interface HelpService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, int sid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findHelpByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);
	
	// ==================== API 接口层 ====================
	public AjaxInfo findHelpByAll(StringBuilder sql);

	public Help findHelpBySId(String sid) throws SQLException;

	public void saveHelp(Help help) throws IOException, SQLException;

	public boolean remove(String sid) throws SQLException;

}
