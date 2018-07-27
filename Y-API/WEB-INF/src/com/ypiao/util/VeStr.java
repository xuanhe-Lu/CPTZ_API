package com.ypiao.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.commons.code.DigestUtils;
import org.commons.lang.StringUtils;
import org.commons.lang.TimeUtils;

public class VeStr {

	private static final String KEY = "now";
	/** 当前索引值 */
	private static long USID = 0;
	/** 指针序列化 */
	private static int USID_INDEX = 0;
	/** 时间戳 */
	private static long USID_TIME = 0;

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

	public static String getMobile(String str) {
		return getMobile("+86", str);
	}

	public static String getMobile(String fix, String str) {
		if (str == null) return null;
		Matcher m = Pattern.compile("^(\\+86-?)?(1([345789])\\d{9})$").matcher(str.replaceAll("\\s+", ""));
		if (m.find()) {
			return "+86-"+ m.group(2);
		} else {
			return null;
		}
	}
	/** 检测是否为号码 */
	public static final boolean isPhone(String tel) {
		if (tel == null || tel.length() < 6) {
			return false;
		} // 检测号码信息
		Matcher m = Pattern.compile("^(\\+86-?)?((1([345789])\\d{9})|(0?(\\d{2,3})?-?\\d{7,8}))$").matcher(tel.replaceAll("\\s+", ""));
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}
	/** 判断密码是否过于简单 */
	public static final boolean isSimple(String pwd) {
		if (pwd == null) return true;
		int len = pwd.length();
		if (len < 6) {
			return true;
		} else if (len == 6 && StringUtils.isNumber(pwd)) {
			return true;
		}
		return false;
	}
	/** 根据用户编号加载文件路径 */
	public static final StringBuilder getUdir(StringBuilder sb, long uid) {
		long a = uid % 1000;
		if (a < 10) {
			sb.append("00").append(a);
		} else if (a < 100) {
			sb.append("0").append(a);
		} else {
			sb.append(a);
		}
		return sb;
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
	/** 随机编码生成Id(15/19位) */
	public static final String getUSid(boolean isHax) {
		return getUSid(Constant.SYS_SSID, isHax);
	}
	/** 随机编码生成Id(15/19位) */
	public synchronized static final String getUSid(String mark, boolean isHax) {
		String fix = UUID.get(KEY);
		SimpleDateFormat out = TimeUtils.get("yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		if (c.getTimeInMillis() > UUID_TIME) {
			UUID_INDEX = TimeUtils.getRandom(0, 99);
			UUID_TIME = c.getTimeInMillis();
			if (fix == null) fix = "2013";
		} else {
			c.setTimeInMillis(UUID_TIME);
		} // 获取当前前置编号
		String str = out.format(c.getTime());
		if (!fix.equals(str)) UUID.clear();
		int num = UUID_INDEX; // 转换位置
		while (UUID.containsKey(String.valueOf(num))) {
			num += 1; // 下移一位
			if (num > 99) {
				num = 1; // 防止超出
			} else if (UUID_INDEX == num) {
				num = TimeUtils.getRandom(0, 99);
				c.add(Calendar.MILLISECOND, 1000);
				str = out.format(c.getTime());
				UUID_TIME = c.getTimeInMillis();
				UUID.clear();
			}
		}
		UUID.put(KEY, str);
		UUID.put(String.valueOf(num), str);
		if (num < 99) UUID_INDEX = (num + 1);
		StringBuilder sb = new StringBuilder();
		if (isHax) {
			sb.append(str.substring(8));
			if (num < 10) sb.append("0");
			long s = Long.parseLong(sb.append(num).toString());
			fix = StringUtils.leftPad(Long.toString(s, 36), 5, "0").toUpperCase();
			sb.setLength(0); // 清除构建
			sb.append(str.substring(0, 8)).append(fix.substring(0, 4)).append(mark).append(fix.substring(4));
		} else {
			sb.append(str).append(Integer.parseInt(mark, 36) % 1000);
			if (num < 10) sb.append("0");
			sb.append(num);
		}
		return sb.toString();
	}
	/** 转换HTML换行标签 */
	public static final String toHtml(String text) {
		if (text == null) {
			return "";
		} else {
			return text.replaceAll("(\r\n|\r|\n)", "<br/>");
		}
	}
	/** 提取分类转换处理 */
	public static final String toSplit(String str) {
		if (str == null) return null;
		String[] ts = str.replaceAll("\\s+", "").split(",");
		for (int i = (ts.length-1); i >= 0; i--) {
			if ((str=ts[i]).length() > 0 && !str.equals("0")) return ts[i];
		}
		return null;
	}
	/** 字符特殊转换处理 */
	public static final String toStr(String str) {
		if (str == null) return null;
		String txt = str.trim();
		if (txt.length() <=0 || str.equalsIgnoreCase("null")) {
			return null;
		} else {
			return txt;
		}
	}

	public static final String MD5(String data) {
		return DigestUtils.md5Hex(data, Constant.SYS_UTF8).toUpperCase();
	}

	public static final String toMD5(String data) {
		if (data.length() == 32) {
			return data;
		} else {
			return MD5(data);
		}
	}

}
