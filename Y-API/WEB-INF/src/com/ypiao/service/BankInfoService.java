package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.BankInfo;

public interface BankInfoService {

	// ==================== APS 接口层 ====================
	public void save(BankInfo info) throws SQLException;

	public void order(String ids, long time) throws SQLException;

	public void state(String ids, int state, long time) throws SQLException;

	// ==================== API 接口层 ====================
	public int findBankByCNo(String cardNo);

	public BankInfo findBankByBid(int bid) throws SQLException;

	public BankInfo getBankByBid(int bid);

	public void sendByAll(AjaxInfo json) throws SQLException;
}
