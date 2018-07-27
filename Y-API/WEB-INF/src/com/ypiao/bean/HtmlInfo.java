package com.ypiao.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ypiao.service.HtmlBaseService;
import com.ypiao.service.SysBeaner;

public class HtmlInfo extends HtmlBase {

	private static final long serialVersionUID = 4216008188398111819L;

	private static final String REGEX = "<!--#include\\s+(file|virtual)(\\s+)?=(\\s+)?\"([^\"]{1,50})\"(\\s+)?-->(\\s+[\r\n]+)?";

	public static final int PAGE_OK = 200;
	/** 信息不存在 */
	public static final int PAGE_NFOUND = 404;
	/** 系统错误 */
	public static final int PAGE_FAILED = 500;

	private static List<HtmlInfo> CACHE = new ArrayList<HtmlInfo>(SIZE);
	/** 静态获取HTML对象 */
	public synchronized static HtmlInfo get(boolean ssi) {
		HtmlInfo info = null;
		if (CACHE.size() > 0) {
			info = CACHE.remove(0);
		} else {
			info = new HtmlInfo();
			info.service = SysBeaner.get(HtmlBaseService.class);
		} // 默认加载失败
		info.SSI = ssi;
		info.setState(PAGE_NFOUND);
		info.setLastModified(0);
		info.setFailed(true);
		info.setExpired(true);
		return info;
	}

	private HtmlBaseService service;

	private int state = PAGE_OK;

	private boolean SSI = false;

	private Map<String, Object> vals = new HashMap<String, Object>();

	public HtmlInfo() {
	}
	/** 清除当前信息 */
	private void clear() {
		this.name = null;
		this.vals.clear();
		this.setSid(null);
		this.setText(null);
		if (CACHE.size() < SIZE) {
			CACHE.add(this);
		}
	}
	/** 关闭当前对象 */
	public void close() {
		if (PAGE_OK == this.state) {
			// Ignored
		} else {
			this.clear();
		}
	}

	public HtmlBaseService getService() {
		return service;
	}

	public Map<String, Object> getVals() {
		return vals;
	}

	public int getState() {
		return state;
	}

	public int setNotFound() {
		return (this.state = PAGE_NFOUND);
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setText(String text) {
		if (text == null) {
			this.state = PAGE_NFOUND;
		} else {
			this.state = PAGE_OK;
		} // 页面内容
		this.text = text;
	}

	public String getString() {
		String txt = this.getText();
		try {
			if (txt == null) {
				return null;
			} // Server Side Include or HTML
			if (this.SSI) {
				StringBuffer sb = new StringBuffer(txt.length() << 1);
				Matcher m = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE).matcher(txt);
				while (m.find()) {
					txt = this.getService().getInclude(m.group(4), name);
					if (txt == null) {
						txt = "[an error occurred while processing this directive]";
					}
					m.appendReplacement(sb, txt);
				}
				return m.appendTail(sb).toString();
			} else if (vals.size() <= 0) {
				return txt; // Nothing
			} else {
				StringBuffer sb = new StringBuffer(txt.length() << 1);
				Matcher m = Pattern.compile("\\$!?\\{([\\w.]{2,30})\\}").matcher(txt);
				while (m.find()) {
					Object obj = vals.get(m.group(1));
					if (obj == null) {
						m.appendReplacement(sb, "");
					} else {
						m.appendReplacement(sb, String.valueOf(obj));
					}
				}
				return m.appendTail(sb).toString();
			}
		} finally {
			this.clear();
			txt = null;
		}
	}

}
