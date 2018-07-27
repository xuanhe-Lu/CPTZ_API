package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.ProdModel;

public interface ProdModelService {

	// ==================== APS 接口层 ====================
	public void save(ProdModel m) throws SQLException;

	public void update(String ids, int state, long time) throws SQLException;

	// ==================== API 接口层 ====================
	public ProdModel getProdModelByTid(int tid) throws SQLException;
}
