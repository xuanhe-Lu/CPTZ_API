package com.ypm.verify;

import javax.servlet.http.HttpServletRequest;

public interface UserRequestAware {

	public void setHttpRequest(HttpServletRequest request);

}
