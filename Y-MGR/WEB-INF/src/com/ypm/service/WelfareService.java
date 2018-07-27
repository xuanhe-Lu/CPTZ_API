package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.Welfare;

/**
 * 福利专区业务层接口.
 * 
 * Created by xk on 2018-06-12.
 */
public interface WelfareService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, int sid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo list(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public Welfare bySid(String sid) throws SQLException;

	public void saveWelfare(Welfare welfare, FileInfo fileInfo) throws IOException, SQLException;

	public boolean remove(String sid) throws SQLException;

}
