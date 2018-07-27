package com.ypiao.ajax;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.lang.StringUtils;
import com.sunsw.xwork2.ActionSupport;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.util.AUtils;
import com.ypiao.verify.UserRequestAware;

public class Action extends ActionSupport implements UserRequestAware {

	private static final long serialVersionUID = 3214628608312978821L;

	protected HashMap<String, String> cache = new HashMap<String, String>();

	protected static final String JXML = "jxml";

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	private boolean _json = true;

	private String action = "index";

	private AjaxInfo ajaxInfo;

	public Action(boolean json) {
		this._json = json;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setHttpRequest(HttpServletRequest request) {
		this.request = request;
		System.out.println("!!!!!!!!!!,request:"+request.toString());
		Enumeration<String> es = request.getParameterNames();
		while (es.hasMoreElements()) {
			String name = es.nextElement();
			this.cache.put(name.toLowerCase(), request.getParameter(name));
		}
	}

	public void setHttpResponse(HttpServletResponse response) {
		this.response = response;
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

	/** String 参数 */
	protected String getString(String name) {
		String str = this.cache.get(name.toLowerCase());
		if (StringUtils.isBlank(str)) {
			return null;
		} else {
			return str.trim();
		}
	}

	public AjaxInfo getAjaxInfo() {
		if (ajaxInfo == null) {
			this.setAjaxInfo(AjaxInfo.getBean(_json));
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

	/** 添加参数集 */
	protected void addParameter(Map<String, String> map) {
		String name = null; // 参数名称
		Enumeration<String> es = request.getParameterNames();
		while (es.hasMoreElements()) {
			name = es.nextElement(); // 相关参数
			map.put(name, request.getParameter(name));
		}
	}

	public String execute() {
		try {
			return this.executeMethod(getAction());
		} catch (Exception e) {
			if (this._json) {
				this.getAjaxInfo().addError(getText("system.error.info"));
			} else {
				this.getAjaxInfo().addFailure();
			}
			return JSON;
		}
	}

	protected String executeMethod(String method) throws Exception {
		Method m = this.getClass().getMethod(method);
		return String.valueOf(m.invoke(this));
	}
}
