package com.ypm.servlet;

import java.io.Serializable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ypm.util.Constant;

public class UserCookie implements Serializable {

	private static final long serialVersionUID = 466105070071985487L;
	/** Cookie 过期时间一天 */
	private static final int MAXAGE = 24 * 60 * 60;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private String userSid;

	public UserCookie(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		// String key = Constant.COOKIE_KEYS;
		Cookie cs[] = request.getCookies();
		try {
			if (cs != null && cs.length > 0) {
				for (int i = 0; i < cs.length; i++) {
					Cookie c = cs[i];
					if (c.getName().equals(Constant.WEB_AUTHSID)) {
						this.setUserSid(c.getValue());
					}
				}
			} // 检测处理来路
		} catch (Exception e) {
			// ignored
		} finally {
			// key = null;
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public String getUserSid() {
		return userSid;
	}

	public void setUserSid(String userSid) {
		this.userSid = userSid;
	}

	public void addC(String name, String value, int maxage) {
		Cookie c = new Cookie(name, value);
		c.setPath(Constant.COOKIE_PATH);
		c.setMaxAge(maxage);
		if (Constant.COOKIE_DOMAIN == null) {
			// ignored
		} else {
			c.setDomain(Constant.COOKIE_DOMAIN);
		}
		this.response.addCookie(c);
	}

	/** 缓存标识信息 */
	public void addSid(String code) {
		this.addC(Constant.WEB_AUTHSID, code, MAXAGE);
	}

}
