package com.ypiao.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlRewrite implements Filter {

	private Rewritten rewritten;

	private NormalRule normalRule;

	public void init(final FilterConfig filterConfig) throws ServletException {
		rewritten = new Rewritten();
		normalRule = new NormalRule();
	}

	public void destroy() {
		rewritten = null;
		normalRule = null;
	}

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8"); // 强制转换编码
		final HttpServletRequest hsRequest = (HttpServletRequest) request;
		final HttpServletResponse hsResponse = (HttpServletResponse) response;
		if (!rewritten.doRewrite(hsRequest, hsResponse, normalRule)) {
			chain.doFilter(hsRequest, hsResponse);
		}
	}

}
