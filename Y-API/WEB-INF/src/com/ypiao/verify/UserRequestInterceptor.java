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

public class UserRequestInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 5173451551635546819L;

	private static final Logger logger = LoggerFactory.getLogger(UserRequestInterceptor.class);

	@Override
	public String intercept(ActionInvocation a) throws Exception {
		Object obj = a.getAction();
		if (obj instanceof UserRequestAware) {
			ActionContext ac = a.getInvocationContext();
			ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
			WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(sc);
			if (wc == null) {
				logger.error("ApplicationContext could not be found.");
			} else {
				UserRequestAware act = (UserRequestAware) obj;
				HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
				HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
				act.setHttpRequest(request);
				act.setHttpResponse(response);
			}
		}
		return a.invoke();
	}

}
