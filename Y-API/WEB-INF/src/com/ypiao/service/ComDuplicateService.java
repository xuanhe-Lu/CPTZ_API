package com.ypiao.service;

import java.sql.SQLException;
import java.util.List;

import com.ypiao.bean.ComDuplicate;

public interface ComDuplicateService {

	// ==================== API 接口层 ====================
	//查询是否已存在的数据
	public ComDuplicate findDuplicates(String idfa,String type) throws SQLException;
	//数据保存
	public boolean saveDuplicate(ComDuplicate cd) throws SQLException;
}