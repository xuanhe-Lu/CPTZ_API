package com.ypm.view;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import com.sunsw.xwork2.ActionSupport;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserSession;
import com.ypm.util.VeRule;
import com.ypm.verify.UserSessionAware;

public abstract class Action extends ActionSupport implements UserSessionAware {

	private static final long serialVersionUID = -7338378462042160650L;

	private HttpServletRequest request;

	private UserSession session;

	private AjaxInfo ajaxInfo;

	private String action = "index";

	private String toUrl;

	public HttpServletRequest getRequest() {
		return request;
	}
	@Override
	public void setHttpRequest(HttpServletRequest request) {
		this.request = request;
	}

	public UserSession getUserSession() {
		return session;
	}
	@Override
	public void setUserSession(UserSession session) {
		this.session = session;
	}

	public AjaxInfo getAjaxInfo() {
		if (ajaxInfo == null) {
			this.setAjaxInfo(AjaxInfo.getBean());
		}
		return ajaxInfo;
	}

	public void setAjaxInfo(AjaxInfo ajaxInfo) {
		this.ajaxInfo = ajaxInfo;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getToUrl() {
		return toUrl;
	}

	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}

	protected String executeMethod(String method) throws Exception {
		Method m = this.getClass().getMethod(this.getAction());
		return String.valueOf(m.invoke(this));
	}

	public String execute() {
		try {
			Method m = this.getClass().getMethod(this.getAction());
			return String.valueOf(m.invoke(this));
		} catch(Exception e) {
			e.printStackTrace();
			AjaxInfo json = this.getAjaxInfo();
			String sn = this.getClass().getSimpleName();
			if (VeRule.isYes("(Login)", sn)) {
				json.setBody(e.toString());
				return ERROR;
			} else {
				json.addError(this.getText("system.error.info"));
				System.out.println("json:"+json.toString());
 return JSON;
			}
		}
	}

	public abstract String index();

}
