package com.ypiao.servlet;

import java.io.Serializable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.code.Suncoder;
import org.commons.lang.StringUtils;
import com.ypiao.util.Constant;

public class UserCookie implements Serializable {

	private static final long serialVersionUID = 2436763811984973905L;
	/** Cookie 过期时间一天 */
	private static final int MAXAGE = 24 * 60 * 60;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private String info;

	public UserCookie(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		Cookie cs[] = request.getCookies();
		try {
			if (cs != null && cs.length > 0) {
				for (int i = 0; i < cs.length; i++) {
					Cookie c = cs[i];
					if (c.getName().equals(Constant.WEB_AUTHINFO)) {
						this.info = c.getValue();
					} else if (c.getName().equals(Constant.WEB_AUTHSID)) {
						
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

	public String getInfo() {
		return info;
	}

	public void addC(String name, String value, int maxage) {
		Cookie c = new Cookie(name, value);
		c.setMaxAge(maxage);
		c.setPath(Constant.COOKIE_PATH);
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

	/** 缓存加密信息 */
	public void addInfo(String info) {
		String value = Suncoder.encode(info, Constant.COOKIE_KEYS);
		this.addC(Constant.WEB_AUTHINFO, value, MAXAGE);
	}

	/** 用户退出 */
	public void removeAll() {
		addC(Constant.WEB_AUTHCODE, "", 0);
		addC(Constant.WEB_AUTHINFO, "", 0);
	}

	/** 用户登录 */
	public boolean isAuthPass() {
		return StringUtils.isNotBlank(info);
	}

}
