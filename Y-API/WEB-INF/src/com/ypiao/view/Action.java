package com.ypiao.view;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sunsw.xwork2.ActionSupport;
import com.ypiao.bean.SystemInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.verify.UserSessionAware;

public abstract class Action extends ActionSupport implements UserSessionAware {

	private static final long serialVersionUID = 4493460744364463819L;
	/** 页面不存在 */
	protected static final String NOTHTML = "nothtml";
	/** 数据执行失败 */
	protected static final String FAILED = "failed";

	protected UserSession userSession;

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	private String action = "index";

	private String toUrl;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setHttpRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setHttpResponse(HttpServletResponse response) {
		this.response = response;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession session) {
		this.userSession = session;
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

	protected SystemInfo getSystem() {
		// return Constant.CONFSYS.get(Constant.SYS_ROOT);
		return null;
	}

	protected String executeMethod(String method) throws Exception {
		Method m = this.getClass().getMethod(method);
		return String.valueOf(m.invoke(this));
	}

	public String execute() {
		try {
			return executeMethod(this.getAction());
		} catch (Exception e) {
			this.addActionError(this.getText("system.error.info"));
			return ERROR;
		}
	}

	public abstract String index();

	protected boolean isOK(String text, int len) {
		return (text != null && text.length() >= len);
	}

}
