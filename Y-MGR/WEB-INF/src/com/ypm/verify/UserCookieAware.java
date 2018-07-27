package com.ypm.verify;

import com.ypm.servlet.UserCookie;

public interface UserCookieAware {

	public void setRemoteAddr(String remoteAddr);

	public void setUserCookie(UserCookie cookie);

}
