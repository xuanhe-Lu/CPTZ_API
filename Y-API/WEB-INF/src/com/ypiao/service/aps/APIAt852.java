package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.ComRaws;
import com.ypiao.bean.Manager;
import com.ypiao.service.CfoCompanyService;

public class APIAt852 extends Abstract {

	private CfoCompanyService cfoCompanyService;

	public CfoCompanyService getCfoCompanyService() {
		return cfoCompanyService;
	}

	public void setCfoCompanyService(CfoCompanyService cfoCompanyService) {
		this.cfoCompanyService = cfoCompanyService;
	}

	public void money(Manager mgr) {
		try {
			ComRaws r = mgr.getObject(ComRaws.class);
			this.getCfoCompanyService().update(r);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
