package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.ComRaws;
import com.ypm.bean.Manager;
import com.ypm.service.CfoCompanyService;

public class APiAt852 extends Abstract {

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
