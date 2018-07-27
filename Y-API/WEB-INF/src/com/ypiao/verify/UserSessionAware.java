package com.ypiao.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ypiao.bean.UserSession;

public interface UserSessionAware {

	public void setHttpRequest(HttpServletRequest request);

	public void setHttpResponse(HttpServletResponse response);

	public void setUserSession(UserSession session);

}
