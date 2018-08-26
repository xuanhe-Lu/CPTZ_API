package com.ypiao.util;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class BUtils {
	private static Logger logger = Logger.getLogger(BUtils.class);
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

	private static void add(StringBuilder sb, char c) {
		if (c > 'Z' || c < 'A') {
			sb.append(c);
		} else {
			sb.append((char) (c + 32));
		}
	}

	private static int doKey(StringBuilder sb, char[] cs, int index) {
		boolean ok = true;
		for (int i = index; i < cs.length; i++) {
			char c = cs[i];
			switch (c) {
			case '"':
				ok = (sb.length() <= 0);
				break;
			case ' ':
				if (ok && sb.length() > 0) {
					add(sb, c);
				}
				break;
			case ':':
				return i;
			case '{':
				break;
			case '\n': // 换行处理
			case '\r': // 换行处理
				if (ok && sb.length() > 0) {
					add(sb, c);
				}
				break;
			case '\\': // 转意直接加载
				i += 1;
				sb.append(cs[i]);
				break;
			default:
				if (ok) {
					add(sb, c);
				}
			}
		}
		sb.setLength(0);
		return index;
	}

	private static int doVal(StringBuilder sb, char[] cs, int index) {
		int d = 0, e = 0, f = 0;
		for (int i = index; i < cs.length; i++) {
			char c = cs[i];
			switch (c) {
			case '\r':
			case '\n':
			case ',':
				if (d == 0 && e == 0 && f == 0) {
					return i;
				}
				sb.append(c);
				break;
			case ' ':
				break;
			case '"':
				if (e > 0 || f > 0) {
					sb.append(c);
				} else if (d > 0) {
					d = 0;
				} else {
					d = 1;
				}
				break;
			case '[':
				e += 1;
				sb.append(c);
				break;
			case ']':
				e -= 1;
				if (e >= 0) {
					sb.append(c);
				} else if (f <= 0) {
					return i;
				}
				break;
			case '{':
				f += 1;
				sb.append(c);
				break;
			case '}':
				f -= 1;
				if (f >= 0) {
					sb.append(c);
				} else if (e <= 0) {
					return i;
				}
				break;
			case '\\': // 转意直接加载
				if (e > 0 || f > 0) {
					sb.append(c);
				}
				i += 1;
				sb.append(cs[i]);
				break;
			default:
				sb.append(c);
			}
		}
		sb.setLength(0);
		return index;
	}

	public static Map<String, String> parse(Map<String, String> map, HttpServletRequest request) throws IOException {
		InputStream is = request.getInputStream();
		logger.info("QueryString:"+request.getQueryString());
		logger.info("RequestURI:"+request.getRequestURI());
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				int a = 0, size = 4096;
				byte b[] = new byte[size];
				while ((a = is.read(b)) != -1) {
					bos.write(b, 0, a);
				}
				logger.info("bos:"+bos.toString());
				return parse(map, bos.toString("UTF-8"));
			} finally {
				bos.close();
			}
		} finally {
			is.close();
		}
	}

	/** JSON数组解析 */
	public static Map<String, String> parse(Map<String, String> map, String body) {
		if (body == null || body.length() < 5) {
			return map;
		} // 数组解析处理
		int index = 0;
		String key = null;
		char[] cs = body.toCharArray();
		StringBuilder sb = new StringBuilder();
		do {
			int pos = doKey(sb, cs, index);
			if (pos > index) {
				key = sb.toString();
				sb.setLength(0);
				index = doVal(sb, cs, (pos + 1));
				map.put(key, sb.toString());
			} else {
				break;
			}
			sb.setLength(0);
			index += 1;
		} while (index < cs.length);
		return map;
	}

	/** 分离数组->Integer */
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

	/** 分离数组->User */
	public static String toUser(String str) {
		if (str == null)
			return null;
		StringBuilder sb = new StringBuilder();
		Matcher m = Pattern.compile("\\]<(.*?)>").matcher(str);
		while (m.find()) {
			sb.append(';').append(m.group(1));
		} // return string
		return (sb.length() <= 1) ? null : sb.substring(1);
	}
}
