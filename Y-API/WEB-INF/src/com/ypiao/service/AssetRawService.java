package com.ypiao.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.AutoRaws;
import com.ypiao.bean.FileInfo;
import com.ypiao.bean.RawInfo;

public interface AssetRawService {

	// ==================== APS 接口层 ====================
	public void save(long rid, String ids, long time) throws SQLException;

	public void save(FileInfo f) throws SQLException;

	public void save(RawInfo r) throws SQLException;

	public void save(List<AutoRaws> ls) throws SQLException;

	public int update(Connection conn, long rid, int total, long time) throws SQLException;

	public void remove(long rid) throws SQLException;

	public void remove(long rid, String ids, long time) throws SQLException;
	// ==================== API 接口层 ====================

	public RawInfo findRawByRid(long rid) throws SQLException;

	/** 加载相关合同 */
	public void loadRawByRid(AjaxInfo json, long rid) throws SQLException;

}
