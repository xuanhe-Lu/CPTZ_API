package com.ypiao.pinyin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PinyinHelper {

	private static Map<Integer, String> cache = new HashMap<Integer, String>();

	static {
		loadFile();
	}

	private static void loadFile() {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = PinyinHelper.class.getResourceAsStream("HY.db");
			if (is == null) {
				sb.append("");
			} else {
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				int i = -1; sb.setLength(0);
				while((i=isr.read()) != -1) {
					sb.append((char) i);
				}
				is.close();
				isr.close();
			}
			int key = 0, len = 0;
			String ts[] = sb.toString().split("(\r\n|\r|\n)");
			for (String str : ts) {
				len = str.length();
				if (len < 7) continue;
				key = Integer.parseInt(str.substring(0, 4), 16);
				cache.put(key, str.substring(6, (len - 1)));
			}
		} catch (IOException io) {
			// Ignored
		} finally {
			sb.setLength(0);
		}
	}

	public static String toPinyin(String str, int max) {
		StringBuilder sb = new StringBuilder();
		for (int c : str.toCharArray()) {
			String v = cache.get(c);
			if (v == null) continue;
			int a = v.indexOf(",");
			if (a == -1) {
				sb.append(v);
			} else {
				sb.append(v.substring(0, a));
			}
			if (sb.length() >= max) return sb.toString();
		}
		return sb.toString();
	}

	public static String toIndex(String str, int max, char def) {
		StringBuilder sb = new StringBuilder(max + 6);
		boolean one = true;
		char[] cs = str.toCharArray();
		for (int c : cs) {
			if (c >= 0x4E00) {
				String v = cache.get(c);
				if (v == null) {
					if (one) sb.append(def);
					one = false;
				} else {
					one = true;
					int a = v.indexOf(",");
					if (a == -1) {
						sb.append(v);
					} else {
						sb.append(v.substring(0, a));
					}
				}
			} else if (c > 122) {
				if (one) sb.append(def);
				one = false;
			} else if (c > 96) {
				one = true;
				sb.append((char) c);
			} else if (c > 90) {
				if (one) sb.append(def);
				one = false;
			} else if (c > 64) {
				one = true;
				sb.append((char) c);
			} else if (c > 57) {
				if (one) sb.append(def);
				one = false;
			} else if (c > 47) {
				one = true;
				sb.append((char) c);
			} else if (one) {
				sb.append(def);
				one = false;
			}
			if (sb.length() >= max) return sb.toString();
		}
		return sb.toString();
	}

}
