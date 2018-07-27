package com.ypm.servlet;

import java.io.File;
import java.util.Locale;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import com.ypm.service.SysBeaner;
import com.ypm.timer.TaskService;
import com.ypm.util.Constant;

public class SysListener extends HttpServlet implements ServletContextListener {

	private static final long serialVersionUID = -2108981921139636341L;

	private static final Logger logger = LoggerFactory.getLogger(SysListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Web System Start ......");
		String root = sce.getServletContext().getRealPath("/");
		try {
			if (root == null) {
				root = Constant.ROOTPATH = File.separator;
			} else if (root.endsWith(File.separator)) {
				Constant.ROOTPATH = root;
				String str = root.substring(0, root.length() - 1);
				int end = str.lastIndexOf(File.separator);
				if (end > -1)
					root = str.substring(0, end);
				root += File.separator;
			} else {
				Constant.ROOTPATH = root + File.separator;
				root = root.substring(0, root.lastIndexOf(File.separator) + 1);
			}
			logger.info("Application Run Path: " + Constant.ROOTPATH);
			String servletmapping = sce.getServletContext().getInitParameter("servletmapping");
			if (servletmapping == null)
				servletmapping = "*.do";
			int beg = servletmapping.indexOf(".");
			Constant.SERVLET_MAPPING = servletmapping.substring(beg);
			logger.info("Servlet Mapping: " + servletmapping);
			Locale.setDefault(Locale.CHINA);
			TaskService.start(); // 启动线程
		} finally {
			root = null;
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
