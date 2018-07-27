package com.ypiao.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ypiao.util.Constant;

public class NormalRule {

	/** 客户端重写 */
	public static final String URL_REDIRECT = "URLRedirect";

	private HashSet<String> cache;

	private HashMap<String, String> rules;

	public NormalRule() {
		this.cache = new HashSet<String>();
		this.rules = new HashMap<String, String>();
		this.loadConf(); // Config
	}

	/** 信息添加 */
	private void add(StringBuilder sb, String key, String uri) {
		sb.setLength(0); // init
		sb.append("/").append(uri).append(Constant.SERVLET_MAPPING);
		this.rules.put(key, sb.toString());
	}

	public void loadConf() {
		StringBuilder sb = new StringBuilder();
		this.add(sb, "index", "index");
		this.add(sb, "login", "login");
		this.add(sb, "logout", "logout");
		this.add(sb, "author", "author");
	}

	private StringBuilder toBuf(String act, int size) {
		StringBuilder sb = new StringBuilder(size);
		return sb.append(act).append(Constant.SERVLET_MAPPING);
	}

	private StringBuilder toBuf(String fix, String act) {
		StringBuilder sb = new StringBuilder(3);
		return sb.append(fix.toLowerCase()).append('_').append(act.toLowerCase()).append(Constant.SERVLET_MAPPING);
	}

	private StringBuilder toBuf(String path, String act, int size) {
		StringBuilder sb = new StringBuilder(size);
		return sb.append(path).append('/').append(act).append(Constant.SERVLET_MAPPING);
	}

	public String getUrl(String path, String action) {
		StringBuilder sb = new StringBuilder();
		return sb.append(path).append('/').append(action).append(Constant.SERVLET_MAPPING).toString();
	}

	public String execute(String uri) {
		Matcher m = Pattern.compile("^/js/([^/]+)(/([^/$]+))?(/|$)", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			StringBuilder sb = toBuf("/js", m.group(1), 30);
			if (m.group(3) != null) {
				sb.append("?action=").append(m.group(3));
			}
			return sb.toString();
		} // return
		return null;
	}

	public String executeHTM(String uri) {
		Matcher m = Pattern.compile("^/(alipay|fuiou|wxpay).html$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			return this.toBuf("notify", 32).append("?action=").append(m.group(1).toLowerCase()).toString();
		} // 公告，学堂拦截
		m = Pattern.compile("^/(note)/(\\d{8}\\w{7,11}).html$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			return toBuf(m.group(1).toLowerCase(), 36).append("?sid=").append(m.group(2)).toString();
		} // 投资记录
		m = Pattern.compile("^/(prod)/([^/]+)/(\\d{8}\\w{7,11}).html$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			return toBuf(m.group(1), m.group(2)).append("?Pid=").append(m.group(3)).toString();
		} // 芝麻拦截处理
		m = Pattern.compile("^/(zhima).html$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			return this.toBuf("zhima", 32).toString();
		}
		return null;
	}

	public String executeJSP(String uri) {
		Matcher m = Pattern.compile("^(/[^/]+)?/(\\w{3,15}).jsp$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			String url = this.rules.get(m.group(2).toLowerCase());
			if (url == null) {
				// Ignored
			} else if (m.group(1) == null) {
				return url;
			} else {
				StringBuilder sb = new StringBuilder();
				return sb.append(m.group(1)).append(url).toString();
			}
		}
		return null;
	}

	public String executeURI(String uri) {
		return null;
	}

	public boolean isFailed(String serverName) {
		if (cache.contains(serverName)) {
			return false;
		} // 正则检测域名规则 (.*?).(sunsw|jrker|).com$
		if("localhost".equals(serverName)){
			serverName = "127.0.0.1";
		}
		Matcher m = Pattern.compile("(.*?).(\\w+).(com|cn)$", Pattern.CASE_INSENSITIVE).matcher(serverName);
		if (m.find()) {
			cache.add(serverName);
			return false;
		} // 正则检测IP规则
		m = Pattern.compile("^(25[0-5]|2[0-4]\\d|(0|1)?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|(0|1)?\\d{1,2})){3}$").matcher(serverName);
		if (m.find()) {
			cache.add(serverName);
			return false;
		} else {
			return true;
		}
	}
}
