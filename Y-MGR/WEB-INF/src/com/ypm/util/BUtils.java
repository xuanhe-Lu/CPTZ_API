package com.ypm.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class BUtils {

	/** 参数转换成小写 */
	public static Map<String, String> convert(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> es = request.getParameterNames();
		String name = null; // 参数名称
		while (es.hasMoreElements()) {
			name = es.nextElement(); // 相关参数
			map.put(name.toLowerCase(), request.getParameter(name));
		}
		return map;
	}
	/** 分离数组->Int */
	public static Set<Integer> toInt(String ids) {
		String[] ts = toSplit(ids);
		Set<Integer> set = new LinkedHashSet<Integer>(ts.length);
		for (String id : ts) {
			set.add(Integer.parseInt(id));
		}
		return set;
	}
	/** 分离数组->Long */
	public static Set<Long> toLong(String ids) {
		String[] ts = toSplit(ids);
		Set<Long> set = new LinkedHashSet<Long>(ts.length);
		for (String id : ts) {
			set.add(Long.parseLong(id));
		}
		return set;
	}
	/** 分离数组->String */
	public static String[] toSplit(String ids) {
		if (ids == null) {
			return new String[0];
		} else {
			return ids.replaceAll("\\s+", "").split(",");
		}
	}
	/** 分离数组->String */
	public static String[] toString(String info) {
		if (info == null) {
			return new String[0];
		} else {
			String[] ts = info.replaceAll("(\r\n|\n|\r)", "\u0001").split("\u0001");
			if (ts.length >= 2) {
				return ts;
			}
			return info.replaceAll("\\s+", "").split(",");
		}
	}
	/** 分离数组->User */
	public static String toUser(String str) {
		if (str == null) return null;
		StringBuilder sb = new StringBuilder();
		Matcher m = Pattern.compile("\\]<(.*?)>").matcher(str);
		while (m.find()) {
			sb.append(';').append(m.group(1));
		} // return string
		return (sb.length() <= 1) ? null : sb.substring(1);
	}

}
