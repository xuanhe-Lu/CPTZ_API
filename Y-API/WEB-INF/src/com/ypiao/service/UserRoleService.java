package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.InfoVIPS;

public interface UserRoleService {

	// ==================== APS 接口层 ====================
	public void save(InfoVIPS v) throws SQLException;
	// ==================== API 接口层 ====================
}
