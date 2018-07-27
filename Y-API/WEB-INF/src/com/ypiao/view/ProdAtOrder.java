package com.ypiao.view;

import com.ypiao.bean.HtmlInfo;
import com.ypiao.bean.SystemInfo;
import com.ypiao.service.HtmlProdService;
import com.ypiao.util.AState;
import com.ypiao.util.AUtils;

public class ProdAtOrder extends Action {

	private static final long serialVersionUID = 1030988530966338816L;

	private HtmlProdService htmlProdService;

	private HtmlInfo htmlInfo;

	private String pid = "AA";

	public HtmlProdService getHtmlProdService() {
		return htmlProdService;
	}

	public void setHtmlProdService(HtmlProdService htmlProdService) {
		this.htmlProdService = htmlProdService;
	}

	public HtmlInfo getHtmlInfo() {
		return htmlInfo;
	}

	public void setHtmlInfo(HtmlInfo htmlInfo) {
		this.htmlInfo = htmlInfo;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public String index() {
		long Pid = AUtils.toLong(this.getPid());
		if (AState.USER_UID_BEG >= Pid) {
			return NOTHTML;
		}
		SystemInfo sys = this.getSystem();
		HtmlInfo html = HtmlInfo.get(false);
		try {
			html.setSid(String.valueOf(Pid));
			this.getHtmlProdService().setAtOrder(html, sys, Pid);
			switch (html.getState()) {
			case HtmlInfo.PAGE_OK:
				this.setHtmlInfo(html);
				return SHOW;
			case HtmlInfo.PAGE_NFOUND:
				return NOTHTML;
			default:
				return this.failed(sys, Pid);
			}
		} catch (Exception e) {
			return this.failed(sys, Pid);
		} finally {
			html.close();
		}
	}

	private String failed(SystemInfo sys, long Pid) {
		return FAILED;
	}
}
