package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.FeedInfo;

public interface FeedBackService {

	// ==================== APS 接口层 ====================
	public void save(FeedInfo feed) throws SQLException;

	// ==================== API 接口层 ====================
	public void saveFeed(FeedInfo feed) throws SQLException;
}
