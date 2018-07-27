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

public class UserRequestInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -1514321520415338403L;

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
				HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
				((UserRequestAware) obj).setHttpRequest(request);
			}
		}
		return a.invoke();
	}

}
