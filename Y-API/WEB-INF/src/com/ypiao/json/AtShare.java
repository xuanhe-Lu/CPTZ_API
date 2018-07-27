package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.UserShareService;

public class AtShare extends Action {

	private static final long serialVersionUID = 1270519822817372651L;

	private UserShareService userShareService;

	public AtShare() {
		super(true);
	}

	public UserShareService getUserShareService() {
		return userShareService;
	}

	public void setUserShareService(UserShareService userShareService) {
		this.userShareService = userShareService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long uid = this.getLong("uid");
			int state = this.getInt("state");
			this.getUserShareService().sendByAll(json, uid, state);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
		}
		return JSON;
	}
}
