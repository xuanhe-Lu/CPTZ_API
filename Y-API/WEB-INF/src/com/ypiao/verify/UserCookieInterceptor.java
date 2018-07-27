package com.ypiao.verify;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import org.framework.web.context.WebApplicationContext;
import org.framework.web.context.support.WebApplicationContextUtils;
import com.sunsw.struts.ServletActionContext;
import com.sunsw.xwork2.ActionContext;
import com.sunsw.xwork2.ActionInvocation;
import com.sunsw.xwork2.interceptor.AbstractInterceptor;
import com.ypiao.servlet.UserCookie;
import com.ypiao.util.VeStr;

public class UserCookieInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -7544895058221612238L;

	private static final Logger logger = LoggerFactory.getLogger(UserCookieInterceptor.class);

	@Override
	public String intercept(ActionInvocation a) throws Exception {
		Object obj = a.getAction();
		if (obj instanceof UserCookieAware) {
			ActionContext ac = a.getInvocationContext();
			ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
			WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(sc);
			if (wc == null) {
				logger.error("ApplicationContext could not be found.");
			} else {
				UserCookieAware act = (UserCookieAware) obj;
				HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
				HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
				act.setRemoteAddr(VeStr.getRemoteAddr(request));
				act.setUserCookie(new UserCookie(request, response));
			}
		}
		return a.invoke();
	}

}
