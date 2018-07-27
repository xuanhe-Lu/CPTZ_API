package com.ypiao.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import com.ypiao.service.SysBeaner;

public class Rewritten {

	private static final Logger logger = LoggerFactory.getLogger(UrlRewrite.class);

	private int beg = -1;

	public Rewritten() {
	}

	public boolean doRewrite(final HttpServletRequest request, final HttpServletResponse response, NormalRule rule) throws IOException, ServletException {
		String toUrl = null, key = null, uri = request.getRequestURI();
		try {
			if ("OPTIONS".equals(request.getMethod())) {
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
				response.setHeader("Access-Control-Allow-Method", "GET, POST, PUT, DELETE");
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				return true;
			} // 过滤禁用域名
			if (rule.isFailed(request.getServerName())) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return this.doRewrite(request, response, "/404.html");
			} // 检测地址信息
			if (beg == 0) {
				key = uri;
			} else if (beg > 0) {
				key = uri.substring(beg);
			} else {
				this.beg = request.getContextPath().length();
				key = uri.substring(beg);
			} // 检测转换地址
			if (key == null || key.length() <= 1) {
				toUrl = rule.getUrl(request.getContextPath(), "index");
				return this.doRewrite(request, response, toUrl);
			} else if (key.indexOf(".do") != -1) {
				return false;
			} // 判断是否为下载数据
			if (SysBeaner.getDownloadService().doGet(request, response, uri)) {
				return true;
			}
			if (key.equalsIgnoreCase("/index.html")) {
				toUrl = rule.getUrl(request.getContextPath(), "index");
				return this.doRewrite(request, response, toUrl);
			} else if (uri.indexOf(".") == -1) {
				toUrl = rule.execute(uri);
			} else if (key.indexOf(".html") != -1) {
				toUrl = rule.executeHTM(uri);
			} else if (key.indexOf(".jsp") != -1) {
				toUrl = rule.executeJSP(uri);
			} // return URL
			if (toUrl == null) {
				return false;
			} else {
				return this.doRewrite(request, response, toUrl);
			}
		} finally {
			toUrl = key = uri = null;
		}
	}

	private boolean doRewrite(final HttpServletRequest request, final HttpServletResponse response, String toUrl) throws IOException, ServletException {
		if (response.isCommitted()) {
			logger.error("response is comitted cannot forward to " + toUrl + " (check you haven't done anything to the response (ie, written to it) before here)");
		} else {
			request.getRequestDispatcher(toUrl).forward(request, response);
		} // Exit
		return true;
	}
}
