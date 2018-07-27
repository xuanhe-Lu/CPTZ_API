package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.ProdModel;

public interface ProdModelService {
	// ==================== APS 接口层 ====================

	// ==================== API 接口层 ====================
	public AjaxInfo findModelByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo findModelByAll(int state);

	public ProdModel findModelByTid(int tid) throws SQLException;

	public Map<Integer, String> getModelByAll();

	public boolean isModelByName(String name);

	public void saveModel(ProdModel m) throws SQLException;

	public void saveState(String ids, int state) throws SQLException;

}
