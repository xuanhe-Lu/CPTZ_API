package com.ypiao.json;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.lang.StringUtils;
import com.sunsw.xwork2.ActionSupport;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.util.*;
import com.ypiao.verify.UserSessionAware;

public abstract class Action extends ActionSupport implements AState, UserSessionAware {

	private static final long serialVersionUID = 6326927229463210763L;

	protected static final DecimalFormat DF2 = new DecimalFormat("0.00");

	protected HashMap<String, String> cache = new HashMap<String, String>();

	protected HttpServletRequest request;

	private HttpServletResponse response;

	private UserSession userSession;

	private String action = "index";

	private AjaxInfo ajaxInfo;

	/** 是否JSON请求 */
	private boolean json = true;

	public Action(boolean json) {
		this.json = true;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setHttpRequest(HttpServletRequest request) {
		this.request = request;
		if (this.json) {
			try {
				BUtils.parse(cache, request);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	public AjaxInfo getAjaxInfo() {
		if (ajaxInfo == null) {
			this.setAjaxInfo(AjaxInfo.getBean());
		}
		return ajaxInfo;
	}

	public void setAjaxInfo(AjaxInfo json) {
		this.ajaxInfo = json;
	}

	protected BigDecimal getBigDecimal(String key) {
		try {
			return new BigDecimal(getString(key));
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	/** Float 参数 */
	protected float getFloat(String name) {
		return AUtils.toFloat(cache.get(name), 0);
	}

	/** Double 参数 */
	protected double getDouble(String name) {
		return AUtils.toDouble(cache.get(name), 0);
	}

	/** INT 参数 */
	protected int getInt(String name) {
		return getInt(name, 0);
	}

	protected int getInt(String name, int def) {
		return AUtils.toInt(cache.get(name), def);
	}

	/** LONG 参数 */
	protected long getLong(String name) {
		return AUtils.toLong(cache.get(name), 0);
	}

	protected String getParameter(String name) {
		return this.cache.get(name);
	}

	/** String 参数 */
	protected String getString(String name) {
		String str = this.cache.get(name);
		if (StringUtils.isBlank(str)) {
			return null;
		} else {
			return str.trim();
		}
	}

	protected String executeMethod(String method) throws Exception {
		Method m = this.getClass().getMethod(this.getAction());
		return String.valueOf(m.invoke(this));
	}

	public String execute() {
		try {
			return executeMethod(this.getAction());
		} catch (Exception e) {
			e.printStackTrace();
			this.getAjaxInfo().addError(this.getText("system.error.info"));
			return JSON;
		}
	}

	/** 用户对象锁 */
	protected Object doLock(long uid) {
		String key = String.valueOf(uid);
		Long obj = Constant.SYS_CACHE_KEYS.get(key);
		if (obj == null) {
			obj = new Long(uid);
			Constant.SYS_CACHE_KEYS.put(key, obj);
		}
		return obj;
	}

	public abstract String index();
}
