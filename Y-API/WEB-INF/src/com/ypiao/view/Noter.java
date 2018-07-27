package com.ypiao.view;

import org.commons.lang.StringUtils;
import com.ypiao.bean.HtmlInfo;
import com.ypiao.bean.SystemInfo;
import com.ypiao.service.HtmlNoteService;
import com.ypiao.util.VeStr;

public class Noter extends Action {

	private static final long serialVersionUID = 3155852624347405817L;

	private HtmlNoteService htmlNoteService;

	private HtmlInfo htmlInfo;

	private String sid = "AA";

	public HtmlNoteService getHtmlNoteService() {
		return htmlNoteService;
	}

	public void setHtmlNoteService(HtmlNoteService htmlNoteService) {
		this.htmlNoteService = htmlNoteService;
	}

	public HtmlInfo getHtmlInfo() {
		return htmlInfo;
	}

	public void setHtmlInfo(HtmlInfo htmlInfo) {
		this.htmlInfo = htmlInfo;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public String index() {
		String sid = VeStr.toStr(this.getSid());
		int len = StringUtils.length(sid);
		if (len < 15 || len > 19) {
			return NOTHTML;
		} // getHtml
		SystemInfo sys = this.getSystem();
		HtmlInfo html = HtmlInfo.get(false);
		try {
			html.setSid(sid.toUpperCase());
			this.getHtmlNoteService().setAtInfo(html, sys);
			switch (html.getState()) {
			case HtmlInfo.PAGE_OK:
				this.setHtmlInfo(html);
				return SHOW;
			case HtmlInfo.PAGE_NFOUND:
				return NOTHTML;
			default:
				return this.failed(sys, sid);
			}
		} catch (Exception e) {
			return this.failed(sys, sid);
		} finally {
			html.close();
			sid = null;
		}
	}

	private String failed(SystemInfo sys, String sid) {
		return FAILED;
	}
}
