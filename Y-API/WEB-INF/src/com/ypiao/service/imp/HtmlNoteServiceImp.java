package com.ypiao.service.imp;

import java.io.File;
import java.io.IOException;
import org.commons.io.FileUtils;
import com.sunsw.velocity.Template;
import com.sunsw.velocity.VelocityContext;
import com.ypiao.bean.AdsNote;
import com.ypiao.bean.HtmlInfo;
import com.ypiao.bean.SystemInfo;
import com.ypiao.service.AderNoteService;
import com.ypiao.service.HtmlNoteService;
import com.ypiao.util.Constant;
import com.ypiao.util.VeFile;

public class HtmlNoteServiceImp extends AbMaker implements HtmlNoteService {

	private AderNoteService aderNoteService;

	public AderNoteService getAderNoteService() {
		return aderNoteService;
	}

	public void setAderNoteService(AderNoteService aderNoteService) {
		this.aderNoteService = aderNoteService;
	}

	private void doAtInfo(File file, SystemInfo sys, String sid) {
		try {
			this.makeAtInfo(null, file, sys, sid);
		} catch (Exception e) {
			// Ignored
		}
	}

	private int makeAtInfo(HtmlInfo html, File file, SystemInfo sys, String sid) throws Exception {
		AdsNote a = this.getAderNoteService().findNoteBySid(sid);
		if (a == null) {
			return html.setNotFound();
		} // 加载模板
		StringBuilder sb = new StringBuilder();
		Template template = this.getTemplate(sb, "Note", "info");
		VelocityContext vc = new VelocityContext();
		try {
			vc.put("note", a); // 公告信息
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

	private File readHtmlInfo(HtmlInfo html, String sid) {
		File file = VeFile.getNote(sid);
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

	public HtmlInfo setAtInfo(HtmlInfo html, SystemInfo sys) throws Exception {
		html.setTimeout(30000);
		String sid = html.getSid();
		File file = this.readHtmlInfo(html, sid);
		if (html.isFailed()) {
			this.makeAtInfo(html, file, sys, sid);
		} else if (html.isExpired()) {
			this.execute(() -> doAtInfo(file, sys, sid));
		} // second
		return html;
	}
}
