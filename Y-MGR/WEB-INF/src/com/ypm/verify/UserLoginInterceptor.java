package com.ypm.verify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import org.framework.context.support.ResourceBundleMessageSource;
import org.framework.web.context.WebApplicationContext;
import org.framework.web.context.support.WebApplicationContextUtils;
import com.sunsw.struts.ServletActionContext;
import com.sunsw.xwork2.ActionContext;
import com.sunsw.xwork2.ActionInvocation;
import com.sunsw.xwork2.interceptor.AbstractInterceptor;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserSession;
import com.ypm.util.Constant;

public class UserLoginInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -4121839656831292199L;

	private static final Logger logger = LoggerFactory.getLogger(UserLoginInterceptor.class);

	@Override
	public String intercept(ActionInvocation a) throws Exception {
		ActionContext ac = a.getInvocationContext();
		ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
		WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(sc);
		if (wc == null) {
			logger.error("ApplicationContext could not be found.");
			return a.invoke();
		} // Session 验证
		UserSession us = (UserSession) ac.getSession().get(Constant.USE_SESSION_KEY);
		if (us == null || us.getUserId() <= 0) {
			// Ignored
		} else {
			return a.invoke();
		} // 检测是否首页
		HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
		int beg = request.getContextPath().length();
		String uri = request.getRequestURI().substring(beg); // 请求地址
		Matcher m = Pattern.compile("(index|login|logout|trialindex).(do|html|jsp)$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return null;
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return this.invoke(ac, wc);
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
