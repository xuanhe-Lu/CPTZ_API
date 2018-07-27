package com.ypiao.sign;

import java.util.HashMap;
import java.util.Map;
import com.ypiao.util.AUtils;

public class JSON {

	private Map<String, JSON> json = new HashMap<String, JSON>();

	private Map<String, String> kvs = new HashMap<String, String>();

	public JSON(String body) {
		this.parse(body);
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

	public JSON parse(String body) {
		if (body == null || body.length() < 5) {
			return this;
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
				kvs.put(key, sb.toString());
			} else {
				break;
			}
			sb.setLength(0);
			index += 1;
		} while (index < cs.length);
		return this;
	}

	public JSON getJSON(String key) {
		JSON j = json.get(key);
		if (j == null) {
			String body = kvs.get(key);
			if (body == null) {
				return null;
			} else {
				j = new JSON(body);
				json.put(key, j);
			}
		}
		return j;
	}

	public String get(String key) {
		return kvs.get(key);
	}

	public int getInt(String key) {
		return AUtils.toInt(get(key));
	}

	public String getString(String key) {
		return kvs.get(key);
	}
}
