package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.ComRaws;

public interface CfoCompanyService {

	// ==================== APS 接口层 ====================
	public int update(ComRaws r) throws SQLException;
	// ==================== API 接口层 ====================
}
