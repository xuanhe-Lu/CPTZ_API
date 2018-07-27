package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.Config;
import com.ypiao.bean.Manager;
import com.ypiao.bean.SetClient;

public interface ConfigService {

	// ==================== APS 接口层 ====================
	public void saveClient(SetClient sc) throws SQLException;

	public void updateClient(String ids, int state, long time) throws SQLException;

	public void removeClient(String ids) throws SQLException;

	public void saveConfig(Config cfg) throws SQLException;

	public void orderConfig(String ids) throws SQLException;

	public void removeConfig(String ids) throws SQLException;
	// ==================== API 接口层 ====================
	public SetClient findClientByAndroid(int ver) throws SQLException;

	public SetClient findClientByIOS(int ver) throws SQLException;
	/** 版本检测更新 */
	public void sendVer(Manager mgr, int tid) throws Exception;

}
