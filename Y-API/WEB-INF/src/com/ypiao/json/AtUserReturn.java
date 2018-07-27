package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.service.UserReturnService;

public class AtUserReturn extends Action {

	private static final long serialVersionUID = 5840983262088828575L;

	private UserReturnService userReturnService;

	public AtUserReturn() {
		super(true);
	}

	public UserReturnService getUserReturnService() {
		return userReturnService;
	}

	public void setUserReturnService(UserReturnService userReturnService) {
		this.userReturnService = userReturnService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long uid = this.getLong("uid");
			if (USER_UID_BEG >= uid) {
				UserSession us = this.getUserSession();
				uid = us.getUid();
			}
			this.getUserReturnService().sendByAll(json, uid);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		return JSON;
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long uid = this.getLong("uid");
			if (USER_UID_BEG >= uid) {
				UserSession us = this.getUserSession();
				uid = us.getUid();
			}
			this.getUserReturnService().sendByUid(json, uid);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		return JSON;
	}
}
