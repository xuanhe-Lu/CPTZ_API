package com.ypiao.service.imp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.commons.io.FileUtils;
import com.sunsw.velocity.Template;
import com.sunsw.velocity.VelocityContext;
import com.ypiao.bean.*;
import com.ypiao.service.HtmlProdService;
import com.ypiao.service.ProdInfoService;
import com.ypiao.service.UserOrderService;
import com.ypiao.util.Constant;
import com.ypiao.util.VeFile;

public class HtmlProdServiceImp extends AbMaker implements HtmlProdService {

	private ProdInfoService prodInfoService;

	private UserOrderService userOrderService;

	public ProdInfoService getProdInfoService() {
		return prodInfoService;
	}

	public void setProdInfoService(ProdInfoService prodInfoService) {
		this.prodInfoService = prodInfoService;
	}

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	private void doAtOrder(File file, SystemInfo sys, long Pid) {
		try {
			this.makeAtOrder(null, file, sys, Pid);
		} catch (Exception e) {
			// Ignored
		}
	}

	private int makeAtOrder(HtmlInfo html, File file, SystemInfo sys, long Pid) throws Exception {
		ProdInfo info = this.getProdInfoService().findProdByPid(Pid);
		if (info == null) {
			return html.setNotFound();
		}
		StringBuilder sb = new StringBuilder();
		Template template = this.getTemplate(sb, "Prod", "order");
		VelocityContext vc = new VelocityContext();
		try {
			vc.put("info", info); // 产品信息
			if (info.getAo() >= 1) {
				List<ProdOrder> ls = this.getUserOrderService().findOrderByPid(Pid);
				vc.put("ords", ls);
				vc.put("num", ls.size());
			} else {
				vc.put("num", 0);
			}
			String body = this.parse(vc, template, file);
			if (html != null) {
				html.setText(body);
			}
			FileUtils.write(file, body, Constant.CHARSET);
			return HtmlInfo.PAGE_OK;
		} finally {
			sb.setLength(0);
		}
	}

	private File readHtmlOrder(HtmlInfo html, long Pid) {
		File file = VeFile.getProd("order", Pid);
		if (file.isFile()) {
			try {
				html.setText(FileUtils.readFile(file, Constant.CHARSET));
				html.setLastModified(file.lastModified());
				if (html.expired()) {
					file.setLastModified(System.currentTimeMillis());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public HtmlInfo setAtOrder(HtmlInfo html, SystemInfo sys, long Pid) throws Exception {
		html.setTimeout(30000);
		File file = this.readHtmlOrder(html, Pid);
		if (html.isFailed()) {
			this.makeAtOrder(html, file, sys, Pid);
		} else if (html.isExpired()) {
			this.execute(() -> doAtOrder(file, sys, Pid));
		}
		return html;
	}
}
