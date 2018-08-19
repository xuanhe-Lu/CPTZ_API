package com.ypiao.verify;

import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.code.Suncoder;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import org.framework.web.context.WebApplicationContext;
import org.framework.web.context.support.WebApplicationContextUtils;
import com.sunsw.struts.ServletActionContext;
import com.sunsw.xwork2.ActionContext;
import com.sunsw.xwork2.ActionInvocation;
import com.sunsw.xwork2.interceptor.AbstractInterceptor;
import com.ypiao.bean.UserSession;
import com.ypiao.service.SysBeaner;
import com.ypiao.servlet.UserCookie;
import com.ypiao.util.AUtils;
import com.ypiao.util.Constant;

public class UserSessionInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -6143928681401480093L;

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
				HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
				HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
				UserSession us = (UserSession) ac.getSession().get(Constant.USE_SESSION_KEY);
				if (us == null) {
					us = this.get(ac, request, response);
				}
				act.setHttpRequest(request);
				logger.info("request:"+request.toString());
				logger.info("request,"+request.getRequestURI());
				act.setHttpResponse(response);
				act.setUserSession(us);
			}
		}
		return a.invoke();
	}

	private UserSession get(ActionContext ac, HttpServletRequest request, HttpServletResponse response) {
		String token = request.getHeader("access_token");
		long uid = AUtils.toLong(request.getHeader("uid"));
		long stime = AUtils.toLong(request.getHeader("nonce_str"));
		if (token != null) {
			UserSession us = SysBeaner.getUserLogerService().getAccessToken(uid, stime, token);
			if (us != null) {
				return us;
			}
		} // get user session for Cookie
		UserCookie uc = new UserCookie(request, response);
		if (uc.isAuthPass()) {
			try {
				String str = Suncoder.decode(uc.getInfo(), Constant.COOKIE_KEYS);
				uid = Long.parseLong(str.split(",")[0]);
				return SysBeaner.getUserLogerService().getAccessByUid(uid);
			} catch (SQLException e) {
				uc.removeAll();
			}
		} // def UserSession
		return new UserSession(0);
	}
}
