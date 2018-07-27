package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.Version;

/**
 * app版本更新业务层接口.
 * 
 * Created by xk on 2018-06-06.
 */
public interface VersionService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, String sid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo list(StringBuilder sql, List<Object> fs, String order, int offset, int max);
	
	// ==================== API 接口层 ====================
	public AjaxInfo list(StringBuilder sql);

	public Version findVersionBySId(String sid) throws SQLException;

	public void saveVersion(Version version, FileInfo fileInfo) throws IOException, SQLException;
	
	public void saveVersion(Version version) throws IOException, SQLException;

	public boolean remove(String sid) throws SQLException;

}
