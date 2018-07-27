package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.BankInfoService;

public class AtBank extends Action {

	private static final long serialVersionUID = 4968746647636918596L;

	private BankInfoService bankInfoService;

	public AtBank() {
		super(true);
	}

	public BankInfoService getBankInfoService() {
		return bankInfoService;
	}

	public void setBankInfoService(BankInfoService bankInfoService) {
		this.bankInfoService = bankInfoService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			this.getBankInfoService().sendByAll(json);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}
}
