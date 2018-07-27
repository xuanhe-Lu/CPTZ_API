package com.ypm.view;

import java.util.Map;
import com.sunsw.struts.interceptor.SessionAware;
import com.ypm.bean.UserSession;
import com.ypm.util.Constant;

public class Logout extends Action implements SessionAware {

	private static final long serialVersionUID = 4470897811591309055L;

	private Map<String, Object> session;

	public Logout() {
		if (Constant.USE_REWRITE) {
			this.setToUrl("login.html");
		} else {
			this.setToUrl("logout.jsp");
		}
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public String index() {
		UserSession us = this.getUserSession();
		try {
			this.getSession().remove(Constant.USE_SESSION_KEY);
		} catch (Exception e) {
			// Ignored
		} finally {
			us.setUserId(0);
			us = null;
		}
		return NONE;
	}
}
