package com.ypiao.servlet;

import java.io.File;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import com.ypiao.service.SysBeaner;
import com.ypiao.timer.TaskService;
import com.ypiao.util.Constant;

public class SysListener extends HttpServlet implements ServletContextListener {

	private static final long serialVersionUID = 3910607695952703445L;

	private static final Logger logger = LoggerFactory.getLogger(SysListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Web System Start ......");
		ServletContext sc = sce.getServletContext();
		String root = sc.getRealPath("/");
		String server = sc.getInitParameter("server");
		String servletfiler = sc.getInitParameter("servletfiler");
		String servletmapping = sc.getInitParameter("servletmapping");
		try {
			StringBuilder sb = new StringBuilder();
			if (root == null) {
				Constant.ROOTPATH = File.separator;
				sb.append(Constant.ROOTPATH);
			} else if (root.endsWith(File.separator)) {
				Constant.ROOTPATH = root;
				String str = root.substring(0, root.length() - 1);
				int end = str.lastIndexOf(File.separator);
				if (end > -1)
					root = str.substring(0, end);
				sb.append(root).append(File.separator);
			} else {
				Constant.ROOTPATH = root + File.separator;
				sb.append(root.substring(0, root.lastIndexOf(File.separator) + 1));
			} // Filer
			root = sb.toString();
			if (servletfiler == null) {
				sb.append("file").append(File.separator);
			} else {
				sb.setLength(0);
				String str = servletfiler.replace("\\", "/");
				sb.append(str.replaceAll("([//]+)", "\\" + File.separator));
				if (str.endsWith("/")) {
					// Ignored
				} else {
					sb.append(File.separator);
				}
			} // 文件路径信息
			Constant.FILEPATH = sb.toString();
			if (server == null) {
				// Ignored
			} else {
				server = server.trim();
				if (server.length() == 2) {
					Constant.SYS_SSIA = server.substring(0, 1);
					Constant.SYS_SSID = server;
				}
			} // Servlet Mapping
			if (servletmapping == null) {
				servletmapping = "*.do";
			}
			Constant.SERVLET_MAPPING = servletmapping.replaceAll("^\\*", "");
			logger.info("Application Run Path: " + Constant.ROOTPATH);
			logger.info("Application File Path: " + Constant.FILEPATH);
			logger.info("Application Server ID: " + Constant.SYS_SSID);
			logger.info("Servlet Mapping: " + servletmapping);
			Locale.setDefault(Locale.CHINA);
			TaskService.start(); // 启动线程
		} finally {
			root = server = servletfiler = servletmapping = null;
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sec) {
		logger.info("Web System Close ......");
		TaskService.close(); // 关闭线程
		SysBeaner.destroy();
		System.exit(0);
	}

}
