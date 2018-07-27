package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AdsInfo;
import com.ypiao.bean.AjaxInfo;

public interface AderInfoService {

	// ==================== APS 接口层 ====================
	public void save(AdsInfo ads) throws SQLException;

	public void order(String ids, long time) throws SQLException;

	public void state(String ids, int state, long time) throws SQLException;

	public void remove(String ids) throws SQLException;

	// ==================== API 接口层 ====================
	public void sendByTid(AjaxInfo json, int tid) throws SQLException;
	
	public void sendByTid(AjaxInfo json, int tid, int num) throws SQLException;
	
	/** 首页广告加载 */
	public void sendIndex(AjaxInfo json, long uid) throws SQLException;
}
