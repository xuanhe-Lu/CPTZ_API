package com.ypiao.verify;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.code.Suncoder;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import org.framework.context.support.ResourceBundleMessageSource;
import org.framework.web.context.WebApplicationContext;
import org.framework.web.context.support.WebApplicationContextUtils;
import com.sunsw.struts.ServletActionContext;
import com.sunsw.xwork2.ActionContext;
import com.sunsw.xwork2.ActionInvocation;
import com.sunsw.xwork2.interceptor.AbstractInterceptor;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.service.SysBeaner;
import com.ypiao.servlet.UserCookie;
import com.ypiao.util.AState;
import com.ypiao.util.AUtils;
import com.ypiao.util.Constant;
import com.ypiao.util.VeRule;

public class UserLoginInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 808181462119590997L;

	private static final Logger logger = LoggerFactory.getLogger(UserLoginInterceptor.class);

	public String intercept(ActionInvocation a) throws Exception {
		ActionContext ac = a.getInvocationContext();
		ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
		WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(sc);
		if (wc == null) {
			logger.error("ApplicationContext could not be found.");
			return a.invoke();
		} // Session 验证
		Map<String, Object> map = ac.getSession();
		UserSession us = (UserSession) map.get(Constant.USE_SESSION_KEY);
		if (us == null) {
			// Ignored
		} else if (us.getUid() >= AState.USER_UID_BEG) {
			return a.invoke();
		} // access_token
		HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
		String token = request.getHeader("access_token");
		if (token != null) {
			long uid = AUtils.toLong(request.getHeader("uid"));
			long key = AUtils.toLong(request.getHeader("nonce_str"));
			us = SysBeaner.getUserLogerService().getAccessToken(uid, key, token);
			if (us != null) {
				if (us.isLogin()) {
					// Ignroed
				} else {
					System.out.println(token + "=====" + us.getUid());
				}
				ac.getSession().put(Constant.USE_SESSION_KEY, us);
				return a.invoke();
			}
		} // Cookie 验证
		HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
		UserCookie uc = new UserCookie(request, response);
		if (uc.isAuthPass()) {
			try {
				String str = Suncoder.decode(uc.getInfo(), Constant.COOKIE_KEYS);
				long uid = Long.parseLong(str.split(",")[0]);
				us.setUid(uid);
				return a.invoke();
			} catch (Exception e) {
				e.printStackTrace();
				uc.removeAll();
			}
		} // 检测过滤JSON请求
		int beg = request.getContextPath().length();
		String uri = request.getRequestURI().substring(beg);
		if (VeRule.isYes("(js)/", uri)) {
			return this.invoke(ac, wc);
		} // 缓存当前请求地址
		Matcher m = Pattern.compile("(^|/)(index|login|logout).(do|html|jsp)$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			return a.invoke();
		} else {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return null;
		}
	}

	private String invoke(ActionContext ac, WebApplicationContext wc) {
		AjaxInfo ajax = AjaxInfo.getBean(); // 返回数据
		ResourceBundleMessageSource message = (ResourceBundleMessageSource) wc.getBean("messageSource");
		ajax.addError(message.getMessage("system.error.login", null, ac.getLocale()));
		ac.getValueStack().set("ajaxInfo", ajax);
		return "json";
	}
}
