package com.ypm.data.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.ypm.util.Constant;

public class ReadUtils {

	private static ReadUtils rd = null;

	public static String readSQL(String type, String name) {
		if (rd == null) {
			rd = new ReadUtils();
		}
		return rd.getSQL(type, name);
	}

	public static String readFile(String fileName) {
		if (rd == null) {
			rd = new ReadUtils();
		}
		return rd.getFile(fileName);
	}

	/** 读取SQL信息 */
	public String getFile(String fileName) {
		try {
			InputStream is = this.getClass().getResourceAsStream(fileName);
			if (is == null) {
				return null;
			}
			int i = -1; // char
			StringBuilder sb = new StringBuilder();
			InputStreamReader r = new InputStreamReader(is, Constant.SYS_UTF8);
			while ((i = r.read()) != -1) {
				sb.append((char) i);
			}
			r.close();
			is.close();
			return sb.toString();
		} catch (IOException e) {
			return null;
		}
	}

	public String getSQL(String type, String name) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(type).append("_").append(name).append(".sql");
			InputStream is = this.getClass().getResourceAsStream(sb.toString());
			if (is == null) {
				return null;
			}
			int i = -1;
			sb.setLength(0);
			InputStreamReader r = new InputStreamReader(is, Constant.SYS_UTF8);
			while ((i = r.read()) != -1) {
				sb.append((char) i);
			}
			r.close();
			is.close();
			return sb.toString();
		} catch (IOException e) {
			return null;
		} finally {
			sb.setLength(0);
		}
	}
}
