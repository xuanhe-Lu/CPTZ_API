package com.ypm.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.BankInfo;

public interface BankInfoService {
	// ==================== APS 接口层 ====================

	// ==================== API 接口层 ====================
	public AjaxInfo findBankByAll();

	public AjaxInfo findBankByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max);

	public BankInfo findBankByBid(int bid) throws SQLException;

	public Map<Integer, String> getBankByAll();

	public void saveInfo(BankInfo bank) throws SQLException;

	public void saveOrder(String ids) throws SQLException;

	public void saveState(String ids, int state) throws SQLException;

}
