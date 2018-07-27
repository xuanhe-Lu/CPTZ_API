package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.ActInfo;
import com.ypm.bean.AjaxInfo;

public interface ActivityService {
	// ==================== APS 接口层 ====================

	public void save(ActInfo info) throws SQLException;

	public int update(String ids, int state, long time) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findActByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo findActByAll();

	public ActInfo findActByAdj(int adj) throws SQLException;

	public ActInfo getActByAdj(int adj);

	public void saveInfo(ActInfo info) throws SQLException;

	public void saveState(String ids, int state) throws SQLException;
}
