package com.ypm.verify;

import javax.servlet.http.HttpServletRequest;
import com.ypm.bean.UserSession;

public interface UserSessionAware {

	public void setHttpRequest(HttpServletRequest request);

	public void setUserSession(UserSession session);

}
