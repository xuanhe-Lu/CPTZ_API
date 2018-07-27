package com.ypm.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VeRule {

	private static BigDecimal BAI = new BigDecimal(100);

	private static BigDecimal WAN = new BigDecimal(10000);

	private static BigDecimal YEAR = new BigDecimal(365);

	private static DecimalFormat DF2 = new DecimalFormat("0.00");

	private static Map<String, String> mods = new HashMap<String, String>();
	static {
		// iPhone
		mods.put("iPhone", "iPhone");
		mods.put("i386", "iPhone Simulator");
		mods.put("x86_64", "iPhone Simulator");
		mods.put("iPhone1,1", "iPhone 2G");
		mods.put("iPhone1,2", "iPhone 3G");
		mods.put("iPhone2,1", "iPhone 3GS");
		mods.put("iPhone3,1", "iPhone 4(GSM)");
		mods.put("iPhone3,2", "iPhone 4(GSM Rev A)");
		mods.put("iPhone3,3", "iPhone 4(CDMA)");
		mods.put("iPhone4,1", "iPhone 4S");
		mods.put("iPhone5,1", "iPhone 5(GSM)");
		mods.put("iPhone5,2", "iPhone 5(GSM+CDMA)");
		mods.put("iPhone5,3", "iPhone 5c(GSM)");
		mods.put("iPhone5,4", "iPhone 5c(Global)");
		mods.put("iPhone6,1", "iPhone 5s(GSM)");
		mods.put("iPhone6,2", "iPhone 5s(Global)");
		mods.put("iPhone7,1", "iPhone 6 Plus");
		mods.put("iPhone7,2", "iPhone 6");
		mods.put("iPhone8,1", "iPhone 6s");
		mods.put("iPhone8,2", "iPhone 6s Plus");
		mods.put("iPhone9,1", "iPhone 7");
		mods.put("iPhone9,2", "iPhone 7 Plus");
		// iPod Touch
		mods.put("iPod1,1", "iPod Touch 1G");
		mods.put("iPod2,1", "iPod Touch 2G");
		mods.put("iPod3,1", "iPod Touch 3G");
		mods.put("iPod4,1", "iPod Touch 4G");
		mods.put("iPod5,1", "iPod Touch 5G");
		// iPad
		mods.put("iPad1,1", "iPad");
		mods.put("iPad2,1", "iPad 2(WiFi)");
		mods.put("iPad2,2", "iPad 2(GSM)");
		mods.put("iPad2,3", "iPad 2(CDMA)");
		mods.put("iPad2,4", "iPad 2(WiFi + New Chip)");
		mods.put("iPad2,5", "iPad mini (WiFi)");
		mods.put("iPad2,6", "iPad mini (GSM)");
		mods.put("iPad2,7", "ipad mini (GSM+CDMA)");
		mods.put("iPad3,1", "iPad 3(WiFi)");
		mods.put("iPad3,2", "iPad 3(GSM+CDMA)");
		mods.put("iPad3,3", "iPad 3(GSM)");
		mods.put("iPad3,4", "iPad 4(WiFi)");
		mods.put("iPad3,5", "iPad 4(GSM)");
		mods.put("iPad3,6", "iPad 4(GSM+CDMA)");
	}

	/** 文件后续名 */
	public static final String getExt(String str) {
		if (str == null)
			return ".jpg";
		return str.replaceAll("(.*?)(.[\\w]+)$", "$2");
	}

	/** 根据用户编号、版本产生编号 */
	public static final long getSid(long uid, int ver) {
		return ((uid * 1000) + ver);
	}

	/** 用户编号合并主键 */
	public static final long getSid(long uid, long fid) {
		return ((uid * AState.USER_UID_MAX) + fid);
	}

	public static final long getSid(String day, int beg) {
		return getSid(day, beg, 0, 0, 16);
	}

	public static final long getSid(String day, int beg, int end) {
		return getSid(day, beg, end, 0, 16);
	}

	public static final long getSid(String day, int beg, int end, int add) {
		return getSid(day, beg, end, add, 16);
	}

	public static final long getSid(String day, int beg, int end, int add, int num) {
		int j = 0; // 加载数量
		char[] src = day.toCharArray();
		if (end <= 0 || src.length <= end) {
			j = (src.length - beg);
		} else {
			j = (end - beg);
		}
		char[] cs = new char[num];
		if (j >= 1) {
			System.arraycopy(src, beg, cs, 0, j);
			if (add != 0) {
				cs[j - 1] += add;
			}
		}
		for (; j < num; j++) {
			cs[j] = '0';
		} // 输出信息
		return Long.parseLong(new String(cs));
	}

	/** 是否转换 */
	public static int getBoolean(String str, int def) {
		if (str == null) {
			return def;
		} else if (str.equalsIgnoreCase("true")) {
			return 1;
		} else {
			return 0;
		}
	}

	/** 根据身份证号识别性别 */
	public static final int getGender(String card) {
		int len = (card == null) ? 0 : card.length();
		if (card == null || card.length() < 15) {
			return 0; // 未知
		}
		try {
			String gx = null;
			if (len <= 15) {
				gx = card.substring(len - 1);
			} else {
				gx = card.substring((len - 2), (len - 1));
			} // 检测性别信息
			if (Integer.parseInt(gx) % 2 == 0) {
				return 2;
			} else {
				return 1;
			}
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/** 年化收益计算 */
	public static BigDecimal income(BigDecimal A, BigDecimal R, int day) {
		return income(A, R, new BigDecimal(day));
	}

	public static BigDecimal income(BigDecimal A, BigDecimal R, BigDecimal day) {
		BigDecimal X = BAI.multiply(YEAR);
		return A.multiply(R).multiply(day).divide(X, 2, BigDecimal.ROUND_DOWN);
	}

	/** 检测包包含信息 */
	public static boolean isExists(String regex, String str) {
		return Pattern.matches(regex, str);
	}

	/** 检测包包含信息 */
	public static boolean isYes(String regex, String str) {
		return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(str).find();
	}

	/** 检测银行卡号信息 */
	public static boolean isBank(String card) {
		if (card == null) {
			return false;
		}
		switch (card.length()) {
		case 16:
			return true;
		case 17: // 交通银行
			return true;
		case 19:
			return true;
		default:
			return false;
		}
	}

	public static boolean isBank(String bank, String card) {
		int len = (card == null) ? 0 : card.length();
		if (len == 16 || len == 19) {
			return true;
		}
		return false;
	}

	/** 检测身份证号信息 */
	public static boolean isIDCard(String card) {
		if (card == null || card.length() < 15) {
			return false;
		} // 正则检测身份证号
		Matcher m = Pattern.compile("^(1[1-6]|2[1-3]|3[1-7]|4[1-5]|5[0-4]|6[1-5]|[7-9]\\d)\\d{4}").matcher(card);
		if (m.find()) {
			m = Pattern.compile("^\\d{6}(19|20)?(\\d{2}(0\\d|1[012])([012]\\d|3[01]))\\d{3}(\\d|X|x)?$").matcher(card);
			return m.find();
		}
		return false;
	}

	/** 检测整数倍 */
	public static boolean isInteger(double abs, int ref) {
		if (DF2.format(abs).endsWith(".00")) {
			int a = (int) abs;
			return ((a % ref) == 0);
		} else {
			return false;
		}
	}

	/** 检测来路是否为微信 */
	public static boolean isMicroMessenger(String xrw, String agent) {
		if (xrw == null || xrw.indexOf("tencent") == -1) {
			if (agent == null) {
				return false;
			} else {
				return Pattern.compile("(MicroMessenger)", Pattern.CASE_INSENSITIVE).matcher(agent).find();
			}
		} else {
			return true;
		}
	}

	/** 检测来路是否为苹果 */
	public static boolean iPhone(String agent) {
		if (agent == null || agent.indexOf("Android") != -1) {
			return false;
		} // 正则检测是否为IOS设备
		Matcher m = Pattern.compile("(iPhone)", Pattern.CASE_INSENSITIVE).matcher(agent);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	/** 检测是否为 */
	public static boolean isWifi(String agent) {
		if (agent == null)
			return false;
		Matcher m = Pattern.compile("(NetType/WIFI)", Pattern.CASE_INSENSITIVE).matcher(agent);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	/** 苹果设备转换 */
	public static final String toiPhone(String key) {
		return mods.get(key);
	}

	/** 理财期限转换 */
	public static final int toAny(long time) {
		long out = (time - GState.USER_TODAX);
		if (out <= 0) {
			return 0;
		} else {
			return (int) (out / GMTime.MILLIS_PER_DAY);
		}
	}

	/** 收款行行号处理 */
	public static final String toBankId(String bankName) {
		if (bankName == null) {
			return ""; // 默认无
		} else if (bankName.indexOf("工商") != -1) {
			return "102100099996";
		} else if (bankName.indexOf("农") != -1) {
			return "103100000026";
		} else if (bankName.indexOf("中国银行") != -1) {
			return "104100000004";
		} else if (bankName.indexOf("建") != -1) {
			return "105100000017";
		} else if (bankName.indexOf("交") != -1) {
			return "301290000007";
		} else if (bankName.indexOf("邮") != -1) {
			return "403100000004";
		} else if (bankName.indexOf("招商") != -1) {
			return "308584000013";
		} else if (bankName.indexOf("平安") != -1) {
			return "313584099990";
		} else if (bankName.indexOf("中信") != -1) {
			return "302100011106";
		} else if (bankName.indexOf("光大") != -1) {
			return "303100000006";
		} else if (bankName.indexOf("兴业") != -1) {
			return "309391000011";
		} else if (bankName.indexOf("民生") != -1) {
			return "305100000013";
		} else if (bankName.indexOf("华夏") != -1) {
			return "304100040000";
		} else if (bankName.indexOf("浦") != -1) {
			return "310290000013";
		} else if (bankName.indexOf("北京") != -1) {
			return "313100000013";
		} else if (bankName.indexOf("上海") != -1) {
			return "313290000017";
		} else if (bankName.indexOf("广发") != -1) {
			return "306581000003";
		} else if (bankName.indexOf("杭州") != -1) {
			return "313331000014";
		}
		return "";
	}

	/** 积分日志转换 */
	public static String toCredit(String event, int num) {
		String c = String.valueOf(num);
		if (num > 0) {
			return event.replace("#0", c).replace("#1", "0");
		} else {
			return event.replace("#1", c).replace("#0", "0");
		}
	}

	/** 性别转换 */
	public static int toGender(String gender) {
		if (gender == null) {
			return 0;
		} else if (gender.indexOf("男") != -1) {
			return 1;
		} else if (gender.indexOf("女") != -1) {
			return 2;
		} else {
			String sex = gender.toLowerCase();
			if (sex.indexOf("f") != -1) {
				return 2; // F=Female
			} else if (sex.indexOf("m") != -1) {
				return 1; // M=Male
			}
			try {
				return Integer.parseInt(gender);
			} catch (Exception e) {
				return 0;
			}
		}
	}

	/** 格式成日期时间 */
	public static int toMinDay(String key) {
		int len = key.length();
		try {
			if (len >= 8) {
				return Integer.parseInt(key);
			} else {
				char cs[] = new char[8];
				System.arraycopy(key.toCharArray(), 0, cs, 0, len);
				for (int i = len; i < 8; i++) {
					cs[i] = '0';
				}
				return Integer.parseInt(new String(cs));
			}
		} catch (Throwable e) {
			return GState.USER_TOADD;
		}
	}

	public static int toMaxDay(int day, int len) {
		if (len >= 8) {
			return day;
		} else if (len >= 7) {
			return (day + 9);
		} else if (len >= 6) {
			return (day + 31);
		} else if (len >= 5) {
			return (day + 931);
		} else if (len >= 4) {
			return (day + 1231);
		}
		return day;
	}

	/** 隐藏电话信息 */
	public static String toMobile(String mobile) {
		if (mobile == null) {
			return null;
		} else {
			return mobile.replaceAll("^(\\+86-?)?(1[34578]\\d)\\d{4}", "$2****");
		}
	}

	public static BigDecimal toPer(BigDecimal A) {
		return BAI.multiply(A);
	}

	/** 百分比计算结果 */
	public static BigDecimal toPer(BigDecimal A, float fa) {
		return A.multiply(new BigDecimal(fa)).divide(BAI, 2, BigDecimal.ROUND_HALF_UP);
	}

	/** 百分比计算结果 */
	public static BigDecimal toPer(BigDecimal A, BigDecimal R) {
		return A.multiply(R).divide(BAI, 2, BigDecimal.ROUND_HALF_UP);
	}

	/** 百分比计算结果 */
	public static BigDecimal toPer(BigDecimal A, BigDecimal T, float fa, float fb) {
		if (fa <= 0) {
			return A.multiply(new BigDecimal(fb));
		} else {
			return T.multiply(new BigDecimal(fa)).divide(BAI, 2, BigDecimal.ROUND_HALF_UP);
		}
	}

	public static BigDecimal toPers(BigDecimal A, BigDecimal B) {
		return BAI.multiply(B).divide(A, 2, BigDecimal.ROUND_HALF_UP);
	}

	/** 整数 -> 2/4位小数 */
	public static BigDecimal toRMB(BigDecimal A, int num) {
		if (num == 2) {
			return A.divide(BAI);
		} else {
			return A.divide(WAN);
		}
	}

	/** 元 -> 万元 */
	public static BigDecimal toWan(BigDecimal A) {
		return A.multiply(WAN);
	}

	/** 隐藏星号处理 */
	public static String toStar(String str, int beg, int end, int fN, int eN, String def) {
		return toStar(str, beg, end, fN, eN, 3, def);
	}

	public static String toStar(String str, int beg, int end, int fN, int eN, int num, String def) {
		if (str == null) {
			return def;
		} else if (fN < 0 || eN < 0) {
			return str;
		} // 字符长度
		int len = str.length();
		if (beg <= 0) {
			beg = 0;
			if (end <= 0 || end > len) {
				end = len;
			}
		} else if (end <= 0) {
			if (beg >= len) {
				beg = 0;
			}
			end = len;
		} else if (beg > end) {
			int pos = end;
			if (len >= beg) {
				end = beg;
			} else {
				end = len;
			}
			beg = (end - pos);
		} else {
			if (beg >= len) {
				beg = 0;
			}
			if (end <= 0 || end >= len) {
				end = len;
			}
		} // 星号信息处理
		int pos = (end - beg);
		char[] cs = str.toCharArray();
		int sP = (pos - eN);
		if (fN > sP) {
			return new String(cs, beg, pos);
		}
		int dP = (fN + num);
		int ss = (dP + eN);
		char[] s = new char[ss];
		System.arraycopy(cs, beg, s, 0, fN);
		for (; fN < dP; fN++) {
			s[fN] = '*';
		}
		System.arraycopy(cs, pos, s, dP, eN);
		return new String(s);
	}

	/** 隐藏星号处理 */
	public static String toStar(String str, int fN, int eN, int num, String def) {
		if (str == null) {
			return def;
		} else if (fN < 0 || eN < 0) {
			return str;
		}
		int len = str.length();
		int sP = (len - eN);
		if (fN > sP) {
			return str;
		}
		int dP = (fN + num);
		int ss = (dP + eN);
		char[] cs = str.toCharArray();
		if (sP >= dP) {
			for (; fN < dP; fN++) {
				cs[fN] = '*';
			}
			System.arraycopy(cs, sP, cs, dP, eN);
			return new String(cs, 0, ss);
		}
		char[] s = new char[ss];
		System.arraycopy(cs, 0, s, 0, fN);
		for (; fN < dP; fN++) {
			s[fN] = '*';
		}
		System.arraycopy(cs, sP, s, dP, eN);
		return new String(s);
	}

	/** 评价星级处理 */
	public static float toStarLevel(int am, int ao) {
		int a = ((am * 5) / ao) % 10;
		float c = am / (ao << 1);
		if (a <= 0) {
			// Ignored
		} else if (a <= 5) {
			c += 0.5f;
		} else {
			c += 1f;
		}
		return c;
	}
}
