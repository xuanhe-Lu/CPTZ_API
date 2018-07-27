package com.ypm.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import com.ypm.service.SysBeaner;

public class Rewritten {

	private static final Logger logger = LoggerFactory.getLogger(UrlRewrite.class);

	private int beg = -1;

	public Rewritten() {
	}

	public boolean doRewrite(final HttpServletRequest request, final HttpServletResponse response, NormalRule rule) throws IOException, ServletException {
		String toUrl = null, key = null, uri = request.getRequestURI();
		try {
			if (beg == 0) {
				key = uri;
			} else if (beg > 0) {
				key = uri.substring(beg);
			} else {
				this.beg = request.getContextPath().length();
				key = uri.substring(beg);
			} // 检测转换地址
			if (key == null || key.length() <= 0) {
				toUrl = rule.getUrl(request.getContextPath(), "index");
				return this.doRewrite(request, response, toUrl);
			} else if (key.indexOf(".do") != -1) {
				return false;
			} // next
			if (key.equals("/") || key.equalsIgnoreCase("/index.html")) {
				toUrl = rule.getUrl(request.getContextPath(), "index");
				return this.doRewrite(request, response, toUrl);
			} else if (uri.indexOf(".") == -1) {
				return false;
			} else if (key.indexOf(".html") != -1) {
				toUrl = rule.execute(uri);
			} else if (key.indexOf(".jsp") != -1) {
				toUrl = rule.executeJSP(uri);
			} else if (key.indexOf(".php") != -1) {
				return SysBeaner.getDownloadService().doGet(request, response);
			} else {
				return false;
			}
			if (toUrl == null) return false;
			return this.doRewrite(request, response, toUrl);
		} finally {
			toUrl = key = uri = null;
		}
	}

	private boolean doRewrite(final HttpServletRequest request, final HttpServletResponse response, String toUrl) throws IOException, ServletException {
		if (response.isCommitted()) {
			logger.error("response is comitted cannot forward to "+ toUrl +" (check you haven't done anything to the response (ie, written to it) before here)");
		} else {
			request.getRequestDispatcher(toUrl).forward(request, response);
		} // Exit
		return true;
	}

}
