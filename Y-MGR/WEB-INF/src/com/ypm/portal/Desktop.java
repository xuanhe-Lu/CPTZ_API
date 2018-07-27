package com.ypm.portal;

import com.ypm.Action;

public class Desktop extends Action {

	private static final long serialVersionUID = -7243427695491085630L;

	public String index() {
		return JSON;
	}

	public String getUserPortalInfo() {
		this.getAjaxInfo().setBody("{}");
		return JSON;
	}

	public String updateUserPortal() {
		this.getAjaxInfo().setBody("");
		return JSON;
	}

}
