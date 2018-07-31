package com.ypm;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.commons.lang.StringUtils;
import com.sunsw.xwork2.ActionSupport;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserSession;
import com.ypm.service.SysBeaner;
import com.ypm.util.AState;
import com.ypm.util.AUtils;
import com.ypm.verify.UserSessionAware;

public abstract class Action extends ActionSupport implements AState, UserSessionAware {

	private static final long serialVersionUID = 7558096462968020425L;

	protected static final DecimalFormat DF2 = new DecimalFormat("0.00");

	protected static final DecimalFormat DF3 = new DecimalFormat("#,##0.00");

	protected static final int FILE_MAX_SIZE = 10 * 1024 * 1024;
	
	protected static final int APK_FILE_MAX_SIZE = 20 * 1024 * 1024;

	protected HashMap<String, String> cache = new HashMap<String, String>();

	private HttpServletRequest request;

	private UserSession session;

	private AjaxInfo ajaxInfo;

	private String action = "index";

	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public void setHttpRequest(HttpServletRequest request) {
		this.request = request; // 请求信息
		Enumeration<String> es = request.getParameterNames();
		while (es.hasMoreElements()) {
			String name = es.nextElement();
			this.cache.put(name.toUpperCase(), request.getParameter(name));
		}
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
		return AUtils.toFloat(cache.get(name.toUpperCase()), 0);
	}

	/** Double 参数 */
	protected double getDouble(String name) {
		return AUtils.toDouble(cache.get(name.toUpperCase()), 0);
	}

	/** INT 参数 */
	protected int getInt(String name) {
		return getInt(name, 0);
	}

	protected int getInt(String name, int def) {
		return AUtils.toInt(cache.get(name.toUpperCase()), def);
	}

	/** LONG 参数 */
	protected long getLong(String name) {
		return AUtils.toLong(cache.get(name.toUpperCase()), 0);
	}

	/** 节点参数 */
	protected int getNode(String name) {
		int code = this.getInt(name, -1);
		if (code <= -1) {
			code = this.getInt("Node", 0);
		}
		return code;
	}

	/** String 参数 */
	protected String getString(String name) {
		String str = this.cache.get(name.toUpperCase());
		if (StringUtils.isBlank(str)) {
			return null;
		} else {
			return str.trim();
		}
	}

	/** 排序信息 */
	protected String getSort() {
		return this.getString("SORT");
	}

	/** 排序依据 */
	protected String getDir() {
		return this.getString("DIR");
	}

	protected int getStart() {
		int start = this.getInt("start");
		if (start < 0) {
			start = 0;
		}
		return start;
	}

	protected int getLimit() {
		int max = this.getInt("limit");
		if (max < 10) {
			max = 10;
		}
		return max;
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

	protected boolean checkValid(String sid, int len) {
		return (sid != null && sid.length() >= len);
	}
	
	/**
	 * @author xk
	 * @param sid int
	 * @param chkLen int
	 * @return boolean
	 * 
	 * 重写checkValid方法
	 */
	protected boolean checkValid(int sid, int chkLen) {
		return (sid > 0 && String.valueOf(sid).length() >= chkLen);
	}

	protected boolean checkValid(String sid, String ids) {
		if (sid == null || ids == null || sid.length() < 5 || ids.length() < 5) {
			return false;
		} else {
			return true;
		}
	}

	protected String executeMethod(String method) throws Exception {
		Method m = this.getClass().getMethod(method);
		return String.valueOf(m.invoke(this));
	}

	public String execute() {
		try {
			Method m = this.getClass().getMethod(this.getAction());
			return String.valueOf(m.invoke(this));
		} catch (Exception e) {
			this.getAjaxInfo().addError(this.getText("system.error.info"));
 return JSON;
		} finally {
			this.cache.clear();
		}
	}

	protected Map<String, String> getDictBySid(String sid) {
		return SysBeaner.getDictInfoService().getDictBySSid(sid);
	}

	public String index() {
 return JSON;
	}
}
