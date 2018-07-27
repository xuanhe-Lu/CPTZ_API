package com.ypiao.verify;

import com.ypiao.servlet.UserCookie;

public interface UserCookieAware {

	public void setRemoteAddr(String remoteAddr);

	public void setUserCookie(UserCookie cookie);

}
