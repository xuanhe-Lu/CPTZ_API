package com.ypiao.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sunsw.http.protocol.HTTP;
import com.ypiao.util.Constant;

public class SendAtHttp {

	public static final String get(String url, Map<String, String> headers) throws IOException {
		HttpURLConnection conn = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			if (headers == null || headers.size() <= 0) {
				conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			} else {
				Iterator<Entry<String, String>> it = headers.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> e = it.next();
					conn.setRequestProperty(e.getKey(), e.getValue());
				}
			} // return result
			if (conn.getResponseCode() == 200) {
				return doString(conn.getInputStream());
			} else {
				return conn.getResponseMessage();
			}
		} finally {
			if (conn != null) conn.disconnect();
		}
	}

	public static final String post(String url, String text) throws IOException {
		return post(url, null, text);
	}

	public static final String post(String url, Map<String, String> headers, String text) throws IOException {
		byte[] b = text.getBytes(Constant.SYS_UTF8);
		HttpURLConnection conn = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length", String.valueOf(b.length));
			conn.setRequestProperty(HTTP.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if (headers == null || headers.size() <= 0) {
				conn.setRequestProperty("content-Type", "application/x-www-form-urlencoded");
			} else {
				Iterator<Entry<String, String>> it = headers.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> e = it.next();
					conn.setRequestProperty(e.getKey(), e.getValue());
				}
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream out = conn.getOutputStream();
			try {
				out.write(b);
				out.flush();
			} finally {
				out.close();
			}
			return doString(conn.getInputStream());
		} finally {
			if (conn != null) conn.disconnect();
		}
	}

	public static Map<String, String> decode(Map<String, String> map, String body) {
		if (map == null) {
			map = new HashMap<String, String>();
		} else {
			map.clear();
		}
		Matcher m = Pattern.compile("\"([^\"]+)\"(\\s+)?:([\\s|\"]+)?(.*?)(\"|,|})").matcher(body);
		while (m.find()) {
			map.put(m.group(1), m.group(4));
		}
		return map;
	}

	private static String doString(InputStream is) throws IOException {
		InputStreamReader isr = new InputStreamReader(is, Constant.SYS_UTF8);
		try {
			String r = null;
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(isr);
			while ((r = br.readLine()) != null) {
				sb.append(r.trim());
			}
			br.close();
			return sb.toString();
		} finally {
			isr.close();
		}
	}

}
