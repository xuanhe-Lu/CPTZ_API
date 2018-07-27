package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.BankInfo;
import com.ypiao.bean.Manager;
import com.ypiao.service.BankInfoService;

public class APIAt991 extends Abstract {

	private BankInfoService bankInfoService;

	public BankInfoService getBankInfoService() {
		return bankInfoService;
	}

	public void setBankInfoService(BankInfoService bankInfoService) {
		this.bankInfoService = bankInfoService;
	}

	public void save(Manager mgr) {
		try {
			BankInfo b = mgr.getObject(BankInfo.class);
			this.getBankInfoService().save(b);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void order(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			long time = mgr.getLong("time");
			this.getBankInfoService().order(ids, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			ids = null;
		}
	}

	public void state(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			int state = mgr.getInt("state");
			long time = mgr.getLong("time");
			this.getBankInfoService().state(ids, state, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		} finally {
			ids = null;
		}
	}
}
