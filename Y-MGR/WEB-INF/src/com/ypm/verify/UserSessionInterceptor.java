package com.ypm.verify;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import org.framework.web.context.WebApplicationContext;
import org.framework.web.context.support.WebApplicationContextUtils;
import com.sunsw.struts.ServletActionContext;
import com.sunsw.xwork2.ActionContext;
import com.sunsw.xwork2.ActionInvocation;
import com.sunsw.xwork2.interceptor.AbstractInterceptor;
import com.ypm.bean.UserSession;
import com.ypm.util.Constant;

public class UserSessionInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 8470526706261453169L;

	private static final Logger logger = LoggerFactory.getLogger(UserSessionInterceptor.class);

	@Override
	public String intercept(ActionInvocation a) throws Exception {
		Object obj = a.getAction();
		if (obj instanceof UserSessionAware) {
			ActionContext ac = a.getInvocationContext();
			ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
			WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(sc);
			if (wc == null) {
				logger.error("ApplicationContext could not be found.");
			} else {
				UserSessionAware act = (UserSessionAware) obj;
				UserSession as = (UserSession) ac.getSession().get(Constant.USE_SESSION_KEY);
				HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
				act.setUserSession(as);
				act.setHttpRequest(request);
			}
		}
		return a.invoke();
	}

}
