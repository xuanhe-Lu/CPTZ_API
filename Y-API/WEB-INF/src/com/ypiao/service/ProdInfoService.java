package com.ypiao.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.AutoRaws;
import com.ypiao.bean.ProdInfo;

public interface ProdInfoService {

	// ==================== APS 接口层 ====================
	public void save(ProdInfo info) throws SQLException;

	public void save(List<AutoRaws> ls) throws SQLException;

	public int updateAuto(long time) throws SQLException;

	public int update(long Pid, long time) throws SQLException;

	public void update(String ids, long time) throws SQLException;

	public void update(String ids, int state, long time) throws SQLException;

	public void remove(long Pid, long Rid, long time) throws SQLException;

	public int update(Connection conn, long Pid, BigDecimal amt) throws SQLException;

	// ==================== API 接口层 ====================
	/** 根据id查询标的信息 */
	public ProdInfo findProdByPid(long Pid) throws SQLException;

	/** 产品自动结票 */
	public void saveOver(long Pid, long time) throws SQLException;

	/** 首页产品加载 */
	public void sendIndex(AjaxInfo json) throws SQLException;

	/** 产品列表加载 */
	public void sendList(AjaxInfo json) throws SQLException;

	/** 在售产品列表加载 */
	public void sendSale(AjaxInfo json) throws SQLException;

	/** 根据状态加载产品列表 */
	public void sendList(AjaxInfo json, int state) throws SQLException;

	public AjaxInfo sendByAdj(AjaxInfo json, int adj, int max) throws SQLException;

}