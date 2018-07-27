package com.ypm.view;

import com.ypm.bean.UserSession;

public class Index extends Action {

	private static final long serialVersionUID = -6914177895174865802L;

	public String index() {
		UserSession us = this.getUserSession();
		try {
			us.getUserId();
		} finally {
			// Ignored
		}
		return SUCCESS;
	}

}
