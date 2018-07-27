package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.Manager;
import com.ypiao.bean.ProdModel;
import com.ypiao.service.ProdModelService;

public class APIAt211 extends Abstract {

	private ProdModelService prodModelService;

	public ProdModelService getProdModelService() {
		return prodModelService;
	}

	public void setProdModelService(ProdModelService prodModelService) {
		this.prodModelService = prodModelService;
	}

	public void save(Manager mgr) {
		try {
			ProdModel m = mgr.getObject(ProdModel.class);
			this.getProdModelService().save(m);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void state(Manager mgr) {
		String ids = mgr.getString("ids");
		try {
			int state = mgr.getInt("state");
			long  time = mgr.getLong("time");
			this.getProdModelService().update(ids, state, time);
		} catch (SQLException e) {
			mgr.addError(DATA_UPDATE_FAILED);
		}
	}
}
