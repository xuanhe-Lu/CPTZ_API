package com.ypm.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ypm.bean.AderNote;
import com.ypm.bean.AjaxInfo;

/**
 * 通知管理业务层接口
 * Create by xk on 2018-04-25
 */
public interface AderNoteService {

	// ==================== APS 接口层 ====================
	public int update(Connection conn, int cid, int row) throws SQLException;

	// ==================== API 接口层 ====================
	public AjaxInfo findAderNoteByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);
	
	// ==================== API 接口层 ====================
	public AjaxInfo findAderNoteByAll(StringBuilder sql);

	public AderNote findAderNoteBySId(String sid) throws SQLException;

	public boolean isAderNoteBySNo(String sno);

	public void saveAderNote(AderNote aderNote) throws SQLException, IOException;

	public boolean remove(String sid) throws SQLException;

}
