package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.Xues;

/**
 * 票友学堂业务层接口.
 * 
 * Created by xk on 2018-05-02.
 */
public interface XuesService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, int sid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findXuesByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);
	
	// ==================== API 接口层 ====================
	public AjaxInfo findXuesByAll(StringBuilder sql);

	public Xues findXuesBySId(String sid) throws SQLException;

	public void saveXues(Xues xues, FileInfo fileInfo) throws IOException, SQLException;

	public boolean remove(String sid) throws SQLException;

}
