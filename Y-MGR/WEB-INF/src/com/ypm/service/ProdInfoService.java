package com.ypm.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.AutoRaws;
import com.ypm.bean.ProdInfo;

public interface ProdInfoService {

	// ==================== APS 接口层 ====================
	public void save(ProdInfo info) throws SQLException;

	public void save(List<AutoRaws> ls) throws SQLException;

	public int updateAuto(long time) throws SQLException;

	public int update(long Pid, long time) throws SQLException;

	public int update(Connection conn, long Pid, BigDecimal amt) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findProdByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public ProdInfo findProdByPid(long Pid) throws SQLException;

	public boolean isProdByName(String name);

	public void saveAds(String ids) throws SQLException;

	public void saveProd(ProdInfo info) throws SQLException;
	/** 结票 -> 打款 */
	public void saveOver() throws SQLException;

	public void saveOver(String ids) throws SQLException;

	public void saveState(String ids, int state) throws SQLException;

	public void removeProd(ProdInfo info) throws SQLException;

}
