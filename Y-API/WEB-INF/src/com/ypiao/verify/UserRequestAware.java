package com.ypiao.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserRequestAware {

	public void setHttpRequest(HttpServletRequest request);

	public void setHttpResponse(HttpServletResponse response);

}
