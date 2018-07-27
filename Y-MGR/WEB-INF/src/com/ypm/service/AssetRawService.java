package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.AutoRaws;
import com.ypm.bean.FileInfo;
import com.ypm.bean.RawInfo;
import com.ypm.bean.RawProd;

public interface AssetRawService {

	// ==================== APS 接口层 ====================
	public void save(List<AutoRaws> ls) throws SQLException;

	public int update(Connection conn, long rid, int total, long time) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findRawByAll();

	public AjaxInfo findRawByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public AjaxInfo findImgsByRid(AjaxInfo json, long rid) throws SQLException;

	public AjaxInfo findTreeByRid(AjaxInfo json, long rid) throws SQLException;

	public RawInfo findRawByRid(long rid) throws SQLException;

	public RawProd findProdByRid(long rid) throws SQLException;

	public int findImgByRid(long rid) throws SQLException;

	public boolean loadImg(FileInfo f) throws SQLException;

	public void saveAuto(AutoRaws r) throws SQLException;

	public void saveOrder(long rid, String ids) throws SQLException;

	public void saveRawImgs(FileInfo f, long rid) throws SQLException, IOException;

	public void saveRawInfo(RawInfo r, FileInfo f) throws SQLException, IOException;

	public void removeInfo(long rid) throws SQLException;

	public boolean removePics(long rid, String ids) throws SQLException;

}
