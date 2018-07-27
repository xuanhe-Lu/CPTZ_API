package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Company;
import com.ypm.bean.FileInfo;

public interface AssetComService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, int cid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findCompanyByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public Company findCompanyById(int cid) throws SQLException;

	public boolean isCompanyBySNo(String sno);

	public void saveCompany(Company c, FileInfo f) throws SQLException, IOException;

	public boolean sendYPiao(Company c) throws SQLException;

	public boolean remove(int cid) throws SQLException;

}
