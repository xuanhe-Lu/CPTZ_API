package com.ypm.filter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ypm.util.Constant;

public class NormalRule {

	/** 客户端重写 */
	public static final String URL_REDIRECT = "URLRedirect";

	private HashMap<String, String> rules;

	public NormalRule() {
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
		this.add(sb, "login", "login");
		this.add(sb, "logout", "logout");

		this.add(sb, "trialindex", "index");
	}

	public String getUrl(String path, String action) {
		StringBuilder sb = new StringBuilder();
		return sb.append(path).append("/").append(action).append(Constant.SERVLET_MAPPING).toString();
	}

	public String execute(String uri) {
		Matcher m = Pattern.compile("^/(\\w{3,15}).html$", Pattern.CASE_INSENSITIVE).matcher(uri);
		if (m.find()) {
			return this.rules.get(m.group(1).toLowerCase());
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

	public static void main(String arg[]) {
		NormalRule nr = new NormalRule();
		System.out.println(nr.executeJSP("/E-Admin/trialindex.jsp"));
		System.out.println(nr.executeJSP("/trialindex.jsp"));
	}

}
