package com.ypm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.commons.code.DigestUtils;
import org.commons.lang.*;

public class VeStr {

	private final static String KEY = "now";
	/** 当前索引值 */
	private static long USID = 0;
	/** 指针序列化 */
	private static int USID_INDEX = 0;
	/** 时间戳 */
	private static long USID_TIME = 0;

	private final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static Map<String, String> UUID = new HashMap<String, String>();
	/** 指针序列化 */
	private static int UUID_INDEX = 0;
	/** 时间戳 */
	private static long UUID_TIME = 0;

	private static IPSeeker seeker = new IPSeeker();

	public static String getCountry(String ip) {
		return seeker.getCountry(ip);
	}

	public static String getArea(String ip) {
		return seeker.getArea(ip);
	}
	/**
	 * 获取客户端真实IP地址
	 * @return String
	 */
	public static final String getRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			return request.getRemoteAddr();
		} else {
			return ip;
		}
	}
	/** 获取文件后缀名称 */
	public static String getSuffixName(String fileName) {
		if (fileName == null) return null;
		int period = fileName.lastIndexOf(".");
		if (period > 0) {
			return fileName.substring(period).trim();
		}
		return null;
	}
	/**
	 * Browse the URL (Virtual catalog)
	 * @param request
	 */
	public static final String getWebRealHttp(HttpServletRequest request) {
		StringBuilder sb = getWebRealPath(request.getServerName(), request.getServerPort());
		sb.append(request.getContextPath());
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append("/");
		}
		return sb.toString();
	}
	/**
	 * Browse the URL (No Virtual catalog)
	 * @param request
	 */
	public static final String getWebRealPath(HttpServletRequest request) {
		return getWebRealPath(request.getServerName(), request.getServerPort()).toString();
	}

	private static final StringBuilder getWebRealPath(String serverName, int prot) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(serverName);
		if (prot == 80) {
			return sb;
		} else {
			return sb.append(":").append(prot);
		}
	}

	public static final String getActionMappingNameWithoutPrefix(String action) {
		String value = action;
		int question = action.indexOf("?");
		if (question >= 0) {
			value = value.substring(0, question);
		}
		int slash = value.lastIndexOf("/");
		int period = value.lastIndexOf(".");
		if ((period >= 0) && (period > slash)) {
			value = value.substring(0, period);
		}
		return value;
	}

	public static final String getActionMappingURLWithoutPrefix(String action) {
		StringBuilder sb = new StringBuilder();
		String servletMapping = Constant.SERVLET_MAPPING;
		if (servletMapping != null) {
			String queryString = null;
			int question = action.indexOf("?");
			if (question >= 0) {
				queryString = action.substring(question);
			}
			String actionMapping = getActionMappingNameWithoutPrefix(action);
			if (servletMapping.startsWith("*.")) {
				sb.append(actionMapping).append(servletMapping.substring(1));
			} else if (servletMapping.endsWith("/*")) {
				sb.append(servletMapping.substring(0, servletMapping.length() - 2)).append(actionMapping);
			} else if (servletMapping.equals("/")) {
				sb.append(actionMapping);
			}
			if (queryString != null) {
				sb.append(queryString);
			}
		}
		return sb.toString();
	}
	/** 16位数字编号 */
	public static final Long getUSid() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		if (c.getTimeInMillis() > USID_TIME) {
			USID_TIME = c.getTimeInMillis();
			USID_INDEX = TimeUtils.getRandom(0, 99);
			SimpleDateFormat out = TimeUtils.get("MMddHHmmss");
			StringBuilder sb = new StringBuilder();
			sb.append(c.get(Calendar.YEAR) - 2000);
			sb.append(out.format(c.getTime()));
			sb.append(Integer.parseInt(Constant.SYS_SSIA, 36));
			USID = Long.parseLong(sb.toString()) * 100;
		} else if (USID_INDEX >= 99) {
			USID += 3600; // 1个周期
			USID_INDEX = 0;
		} else {
			USID_INDEX++;
		}
		return (USID + USID_INDEX);
	}
	/** 随机编码生成Id(15/18位) */
	public static final String getUSid(boolean isHax) {
		return getUSid(Constant.SYS_SSIA, isHax);
	}
	/** 随机编码生成Id(15/18位) */
	public synchronized static final String getUSid(String mark, boolean isHax) {
		String fix = UUID.get(KEY);
		SimpleDateFormat out = TimeUtils.get("yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		if (c.getTimeInMillis() > UUID_TIME) {
			UUID_INDEX = TimeUtils.getRandom(0, 999);
			UUID_TIME = c.getTimeInMillis();
			if (fix == null) fix = "2013";
		} else {
			c.setTimeInMillis(UUID_TIME);
		} // 获取当前前置编号
		char f = mark.charAt(0);
		String str = out.format(c.getTime());
		if (!fix.equals(str)) UUID.clear();
		int num = UUID_INDEX; // 转换位置
		while (UUID.containsKey(String.valueOf(num))) {
			num += 1; // 下移一位
			if (num > 999) {
				num = 1; // 防止超出
			} else if (UUID_INDEX == num) {
				num = TimeUtils.getRandom(0, 999);
				c.add(Calendar.MILLISECOND, 1000);
				str = out.format(c.getTime());
				UUID_TIME = c.getTimeInMillis();
				UUID.clear();
			}
		}
		UUID.put(KEY, str);
		UUID.put(String.valueOf(num), str);
		if (num < 999) UUID_INDEX = (num + 1);
		StringBuilder sb = new StringBuilder();
		if (isHax) {
			sb.append(str.substring(8));
			if (num < 10) {
				sb.append("00");
			} else if (num < 100) {
				sb.append("0");
			}
			long s = Long.parseLong(sb.append(num).toString());
			fix = toHex(s, 34);
			sb.setLength(0); // 清除构建
			sb.append(str.substring(0, 8)).append(f);
			for (int i = (6 - fix.length()); i > 0; i--) {
				sb.append('0');
			}
			sb.append(fix);
		} else {
			sb.append(str).append(f % 10);
			if (num < 10) {
				sb.append("00");
			} else if (num < 100) {
				sb.append("0");
			}
			sb.append(num);
		}
		return sb.toString();
	}

	/** 否定条件 */
	public static int getNot(String str) {
		if (str == null) return 1;
		if (str.equals("1") || str.equalsIgnoreCase("yes")) {
			return 0;
		} else {
			return 1;
		}
	}
	/** 肯定条件 */
	public static int getYes(String str) {
		if (str == null || !str.equalsIgnoreCase("yes")) {
			return 0;
		} else {
			return 1;
		}
	}
	/** 设置否定条件 */
	public static String setNot(int yes) {
		return (yes == 0) ? "yes" : null;
	}
	/** 设置肯定条件 */
	public static String setYes(int yes) {
		return (yes == 1) ? "yes" : null;
	}
	/** 域名检测 */
	public static boolean isDomain(String str, String text, String domain) {
		if (str != null && str.equalsIgnoreCase(domain)) {
			return true;
		} // Check text
		if (text == null) return false;
		String[] ts = text.split(",");
		for (int i = 0; i < ts.length; i++) {
			if (ts[i].equalsIgnoreCase(domain)) return true;
		}
		return false;
	}
	/** 图片检测 */
	public static boolean isImage(String distName) {
		if (distName == null) return false;
		Matcher m = Pattern.compile("(bmp|png|jpg|jpeg|gif)$", Pattern.CASE_INSENSITIVE).matcher(distName);
		if (m.find()) {
			return true;
		}
		return false;
	}

	public static boolean isEmail(String email) {
		if (getEmail(email) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static final String getEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		} // 检测是否符合标准
		if (email.startsWith("www")) {
			return null;
		} // 正则检测邮件地址
		if (Pattern.matches("\\w+([-\\.+]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", email)) {
			return email.toLowerCase();
		} else {
			return null;
		}
	}
	/** 定向转换号码 */
	public static String getTel(int num, String tel) {
		if (tel == null || tel.length() < num) return null;
		String str = tel.trim();
		if (StringUtils.isNumber(tel)) {
			int n = NumberUtils.toInt(tel.substring(0, 1));
			if (n < 9 && n > 1 && tel.length() == num) {
				return str;
			}
		}
		return null;
	}
	/** 电话号码检测 */
	public static boolean isTel(int num, String tel) {
		if (tel == null) return true;
		if (StringUtils.isNumber(tel)) {
			int n = NumberUtils.toInt(tel.substring(0, 1));
			if (n < 9 && n > 1 && tel.length() == num) {
				return true;
			}
		}
		return false;
	}
	/** 手机号码检测 */
	public static final boolean isMobile(String str) {
		Matcher m = Pattern.compile("^1([34589])\\d{9}$").matcher(str);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static final String getMobile(String str) {
		if (str == null) {
			return null;
		}
		Matcher m = Pattern.compile("^(\\+86-?)?(1([345789])\\d{9})$").matcher(str.replaceAll("\\s+", ""));
		if (m.find()) {
			return "+86-"+ m.group(2);
		} else {
			return null;
		}
	}
	/** 银行卡号检测 */
	public static boolean isBank(String acc) {
		if (acc == null) return false;
		return Pattern.matches("\\d{18,20}", acc);
	}
	/** QQ号码检测 */
	public static boolean isQQ(String qq) {
		if (qq == null) return true;
		return Pattern.matches("\\d{5,11}", qq);
	}

	public static String getQQ(String qq) {
		if (qq == null || !isQQ(qq)) {
			return null;
		} else {
			return qq.trim();
		}
	}

	public static final String getFilter(String str) {
		return getFilter(str, 0);
	}

	public static final String getFilter(String str, int type) {
		if (str == null) return null;
		String temp = null, text = null;
		try {
			byte[] byts = str.getBytes("ISO8859-1");
			if (type == 1) {
				return new String(byts, Constant.CHARSET);
			} // 系统分析
			temp = new String(byts, "UTF-8");
			int s = str.length(), t = temp.length();
			if (s > t) return temp.trim();
			text = new String(byts, "GBK");
			if (s > text.length()) {
				return text.trim();
			} else if (s == t) {
				return str.trim();
			} else {
				return temp.trim();
			}
		} catch (Exception e) {
			if (temp == null) {
				return str.trim();
			} else {
				return temp.trim();
			}
		} finally {
			temp = text = null;
		}
	}

	private static String toHex(long i, int radix) {
		if (radix < Character.MIN_RADIX || radix == 10 || radix > 34) {
			return String.valueOf(i);
		}
		int a = 64;
		char[] c = new char[65];
		boolean negative = (i < 0);
		if (!negative) {
			i = -i;
		}
		while (i <= -radix) {
			c[a--] = digits[(int) (-(i % radix))];
			i = i / radix;
		}
		c[a] = digits[(int) (-i)];
		if (negative) {
			c[--a] = '-';
		}
		return new String(c, a, (65 - a));
	}
	/** 提交数组分隔处理 */
	public static final String[] toSplit(String str) {
		return toStr(str, 2).split("\\],\\[");
	}
	/** 字符特殊转换处理 */
	public static final String toStr(String str) {
		if (StringUtils.isBlank(str) || str.equalsIgnoreCase("null")) {
			return null;
		} else {
			return str.trim();
		}
	}
	/** 数组数据转换处理 */
	public static final String toStr(String str, int beg) {
		if (str == null) return "none";
		char[] cs = str.toCharArray();
		int len = cs.length - beg; // 转换数组信息
		boolean openSY = false;
		StringBuilder sb = new StringBuilder(cs.length);
		for (int i = beg; i < len; i++) {
			char c = cs[i];
			switch (c) {
			case '"': // 34 双引号检测
				if (openSY) {
					openSY = false;
				} else {
					openSY = true;
				} break;
			case ',': // 44
				if (openSY) {
					sb.append(c);
				} else {
					sb.append(Constant.REG_EX);
				} break;
			case '\\': // 92
				char d = cs[i+1];
				if (d == '"') {
					sb.append(d); i++;
				} else {
					sb.append(c);
				} break;
			case ']': // 93
			case '}':
				d = cs[i+1];
				if (d == ',') {
					sb.append(c).append(d);
					i++; break;
				}
			default:
				sb.append(c);
			}
		} // 转换结果
		return sb.toString();
	}
	/** 网址转换处理 */
	public static final String toUrl(String url) {
		return UrlUtils.toUrl(url);
	}

	public static final String encode(String str) {
		return UrlUtils.encode(str, Constant.CHARSET);
	}

	public static final String MD5(String data) {
		return DigestUtils.md5Hex(data).toUpperCase();
	}

}
