package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserBank;
import com.ypiao.bean.UserBker;

public interface UserBankService {

	// ==================== APS 接口层 ====================
	public void save(UserBank b) throws SQLException;

	public void save(UserBker b) throws SQLException;

	public void used(UserBker b) throws SQLException;
	// ==================== API 接口层 ====================
	public UserBank findBankByCNo(String cardNo) throws SQLException;

	public UserBank findBankByUid(long uid) throws SQLException;

	public UserBker findBkerByUid(long uid) throws SQLException;

	public void saveBank(UserBank b) throws SQLException;

	public void saveBker(UserBker b) throws SQLException;

	/** 完成绑卡操作 */
	public void saveBind(UserBank b) throws SQLException;

	public void sendByNew(AjaxInfo json, long uid) throws SQLException;

	public void sendByUid(AjaxInfo json, long uid) throws SQLException;
}
