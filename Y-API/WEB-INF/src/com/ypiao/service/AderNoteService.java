package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AderNote;
import com.ypiao.bean.AdsNote;
import com.ypiao.bean.AjaxInfo;

/**
 * 通知管理数据接口定义. 
 */
public interface AderNoteService {

	// ==================== APS 接口层 ====================
	public void save(AdsNote note) throws SQLException;
	
	/**
	 * @author xk
	 * @param note is AderNote
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(AderNote aderNote) throws SQLException;
	
	/**
	 * @author xk
	 * @param ids String
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException;

	// ==================== API 接口层 ====================
	public AdsNote findNoteBySid(String sid) throws SQLException;

	/** 首页公告加载 */
	public void sendIndex(AjaxInfo json) throws SQLException;
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 通知管理列表
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException;
}
