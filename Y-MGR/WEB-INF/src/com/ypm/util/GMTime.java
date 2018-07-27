package com.ypm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commons.lang.TimeUtils;

public class GMTime {

	/** 每天秒数 */
	public static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

	/** 每小时秒数 */
	public static final long MILLIS_PER_HOUR = 60 * 60 * 1000;

	/** 每分秒数 */
	public static final long MILLIS_PER_MIU = 60 * 1000;

	/** 中国时差信息 */
	public static int CHINA = 28800000;

	/** 格林威治时间 */
	public static int OFFSET = 0;

	/** 格林威治时小时差 */
	public static int OFFSETHOUR = 0;

	public static final int HOUR = 24;

	public static final SimpleDateFormat OUT_FULL = TimeUtils.OUT_FULL;

	public static final SimpleDateFormat OUT_MILL = new SimpleDateFormat("HHmm");

	public static final SimpleDateFormat OUT_OKS = new SimpleDateFormat("yyyyMMddHHmmss");

	public static final SimpleDateFormat OUT_SHORT = TimeUtils.OUT_SHORT;
	static {
		offset();
	}

	/** 更新当前时区信息 */
	public static final long offset() {
		long time = System.currentTimeMillis();
		TimeZone tz = TimeZone.getDefault();
		OFFSET = tz.getOffset(time); // 标准时差
		OFFSETHOUR = (int) (OFFSET / MILLIS_PER_HOUR);
		return GMTime.currentTimeMillis();
	}

	/** 格林威治标准时间 */
	public static final long currentTimeMillis() {
		return (System.currentTimeMillis() - OFFSET);
	}

	/** 增加日期 */
	public static final long add(long time, int day) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.add(Calendar.DATE, day);
		return c.getTimeInMillis();
	}

	/** 根据日期产生编号 */
	public static final long getSid(int offset) {
		return getSid(offset, 0, 0);
	}

	/** 根据日期类型产生编号 */
	public static final long getSid(int offset, int day) {
		return getSid(offset, day, 0);
	}

	public static final long getSid(int offset, int day, int tid) {
		long a = getTday(offset, day) * 100;
		long sid = a + (12 + toHour(offset));
		if (tid > 0) {
			return (sid * 100 + tid);
		} else {
			return sid;
		}
	}

	/** Uid+YYYYMMDD */
	public static final long getUSid(long uid) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, CHINA - OFFSET);
		long a = (uid * 100000000);
		a += c.get(Calendar.YEAR) * 10000;
		a += ((c.get(Calendar.MONTH) + 1) * 100);
		return (a += c.get(Calendar.DATE));
	}

	public static final Calendar getCalendar(int offset) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, offset - OFFSET);
		return c;
	}

	public static final Calendar getCalendar(int tday, int offset) {
		int year = 1970, month = 0, date = tday;
		if (tday >= 10000) {
			year = tday / 10000;
		}
		if (tday >= 1000) {
			String s = String.valueOf(tday);
			int j = s.length() - 2;
			month = Integer.parseInt(s.substring((j - 2), j)) - 1;
			date = Integer.parseInt(s.substring(j));
		}
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, 0, 0, 0);
		c.add(Calendar.MILLISECOND, -offset);
		return c;
	}

	public static final int getTday(Calendar c) {
		int x = c.get(Calendar.YEAR) * 10000;
		x += ((c.get(Calendar.MONTH) + 1) * 100);
		return (x + c.get(Calendar.DATE));
	}

	/** 获取当天时间yyyyMMdd */
	public static final int getTday(int offset) {
		return getTday(0, offset, 0);
	}

	public static final int getTday(int offset, int day) {
		return getTday(0, offset, day);
	}

	public static final int getTday(long t, int offset, int day) {
		Calendar c = Calendar.getInstance();
		if (t > 1000) {
			c.setTimeInMillis(t);
		}
		c.add(Calendar.MILLISECOND, offset - OFFSET);
		if (day != 0) {
			c.add(Calendar.DATE, day);
		}
		return getTday(c);
	}

	public static final int getTday(String tday, int day) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(OUT_SHORT.parse(tday));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.add(Calendar.DATE, day);
		return getTday(c);
	}

	/** 标准时间偏移 */
	public static final long getTime(int offset) {
		return getCalendar(offset).getTimeInMillis();
	}

	/** 标准时间偏移 */
	public static final long getTime(int month, int day) {
		Calendar c = getCalendar(0);
		if (month != 0)
			c.add(Calendar.MONTH, month);
		if (day != 0)
			c.add(Calendar.DATE, day);
		return c.getTimeInMillis();
	}

	public static final String getTime(String format, int offset) {
		Calendar c = getCalendar(offset);
		return TimeUtils.get(format).format(c.getTime());
	}

	public static long getTime(String day, int beg, int end) {
		return getTime(day, beg, end, 0);
	}

	public static long getTime(String day, int beg, int end, int add) {
		int j = 0; // 加载数量
		char[] src = day.toCharArray();
		if (end <= 0 || src.length <= end) {
			j = (src.length - beg);
		} else {
			j = (end - beg);
		}
		char[] cs = new char[14];
		if (j >= 14) {
			System.arraycopy(src, beg, cs, 0, 14);
		} else {
			System.arraycopy(src, beg, cs, 0, j);
			if (add != 0) {
				cs[j - 1] += add;
			}
			for (; j < 14; j++) {
				cs[j] = '0';
			}
		} // 格式化时间信息
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(OUT_OKS.parse(new String(cs)));
			c.add(Calendar.MILLISECOND, -OFFSET);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	/** 转换时间差 */
	public static final String getTimeInterval(long s, long t, int offset) {
		long diff = (t - s);
		if (diff < MILLIS_PER_MIU) {
			return "当前";
		} else if (diff < MILLIS_PER_HOUR) {
			return (diff / MILLIS_PER_MIU) + "分钟前";
		} else if (diff < MILLIS_PER_DAY) {
			return (diff / MILLIS_PER_HOUR) + "小时前";
		} else if (diff < 604800000) {
			return (diff / MILLIS_PER_DAY) + "天前";
		} else if (diff < 2592000000L) {
			return "7天前";
		} else if (diff < 5184000000L) {
			return "1个月前";
		} else {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(s);
			c.add(Calendar.MILLISECOND, offset);
			return OUT_FULL.format(c.getTime());
		}
	}

	/** 获取当天标准时间 */
	public static final long getToday() {
		return getToday(OFFSET).getTimeInMillis();
	}

	/** 获取当天转换时间 */
	public static final Calendar getToday(int offset) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MILLISECOND, -offset);
		return c;
	}

	/** 获取当月标准时间 */
	public static final long getMonth() {
		return getMonth(OFFSET, 0).getTimeInMillis();
	}

	/** 获取下月标准时间 */
	public static final long getMonths() {
		return getMonth(OFFSET, 1).getTimeInMillis();
	}

	/** 对应时间增加月份 */
	public static final Calendar getMonth(long time, int m) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.add(Calendar.MONTH, m);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}

	public static final Calendar getMonth(int offset, int m) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, m);
		c.set(Calendar.DAY_OF_MONTH, 0);
		c.set(Calendar.HOUR_OF_DAY, 24);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MILLISECOND, -offset);
		return c;
	}

	/** 获取下月标准时间 */
	public static final long getMonth(int m) {
		Calendar c = getToday(OFFSET);
		c.add(Calendar.MONTH, m);
		c.add(Calendar.DATE, 1);
		return c.getTimeInMillis();
	}

	/** 获取指定日期时间 */
	public static final long getNext(int day) {
		Calendar c = getToday(OFFSET);
		c.add(Calendar.DATE, day);
		return c.getTimeInMillis();
	}

	public static final int getHours(int offset) {
		return getHours(GMTime.currentTimeMillis(), offset);
	}

	public static final int getHours(long time, int offset) {
		Calendar c = Calendar.getInstance();
		if (time > 0) {
			c.setTimeInMillis(time);
		}
		c.add(Calendar.MILLISECOND, offset);
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		if (h <= 0) {
			return m;
		} else {
			return (h * 100 + m);
		}
	}

	/** 获取当前日期所在周 */
	public static final int getWeek(int offset) {
		return getWeek(offset, 0);
	}

	public static final int getWeek(int offset, int day) {
		Calendar c = getCalendar(offset);
		if (day != 0) {
			c.add(Calendar.DATE, day);
		}
		int year = c.get(Calendar.YEAR) * 100;
		int w = c.get(Calendar.WEEK_OF_YEAR);
		if (w <= 1 && c.get(Calendar.MONTH) > 10) {
			year += 100;
		} // 所在周期序号
		return (year + w);
	}

	public static final String format(int offset, String format) {
		return format(0, (offset - OFFSET), format);
	}

	/** yyyy-MM-dd HH:mm:ss */
	public static final String format(long t, int offset) {
		return format(t, offset, OUT_FULL);
	}

	public static final String format(long t, int offset, SimpleDateFormat sdf) {
		Calendar c = Calendar.getInstance();
		if (t == 0) {
			// Ignored
		} else {
			c.setTimeInMillis(t);
			c.add(Calendar.MILLISECOND, offset);
		}
		return sdf.format(c.getTime());
	}

	public static final String format(long t, int offset, String format) {
		return format(t, offset, TimeUtils.get(format));
	}

	public static final String format(long s, long t, int offset) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(s + offset);
		int year = c.get(Calendar.YEAR);
		String ss = OUT_FULL.format(c.getTime());
		c.setTimeInMillis(t + offset);
		String st = OUT_FULL.format(c.getTime());
		if (st.endsWith("00:00:00")) {
			c.add(Calendar.SECOND, -1);
			st = OUT_FULL.format(c.getTime());
		} // builder time
		StringBuilder sb = new StringBuilder();
		sb.append(ss).delete(16, 19); // 前半段
		if (c.get(Calendar.YEAR) == year) {
			if (ss.startsWith(st.substring(0, 10))) {
				sb.append('-').append(st.substring(11, 16));
			} else {
				sb.append('T').append(st.substring(5, 16));
			}
		} else {
			sb.append('T').append(st.substring(0, 16));
		}
		return sb.toString();
	}

	/** yyyy-MM-dd HH:mm:ss */
	public static final String formater(long t, int offset) {
		if (t < 10000 && t > -10000) {
			return "-";
		} else {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t);
			c.add(Calendar.MILLISECOND, offset);
			return OUT_FULL.format(c.getTime());
		}
	}

	public static final String formater(long t, int offset, String format) {
		if (t < 10000) {
			return "-";
		} else {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t);
			c.add(Calendar.MILLISECOND, offset);
			return TimeUtils.get(format).format(c.getTime());
		}
	}

	public static final String formatInt(int day) {
		if (day <= 10000000) {
			return "-";
		} else {
			return formatInt(String.valueOf(day));
		}
	}

	public static final String formatInt(String s) {
		if (s == null || s.length() < 8) {
			return "-";
		} else {
			StringBuilder sb = new StringBuilder(10);
			sb.append(s.substring(0, 4)).append('-').append(s.substring(4, 6)).append('-').append(s.substring(6));
			return sb.toString();
		}
	}

	public static final int parse(String tday) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(OUT_SHORT.parse(tday));
			return getTday(c);
		} catch (ParseException e) {
			return AUtils.toInt(tday.replace("-", ""));
		}
	}

	/** 获取当前年份 */
	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/** 毫秒时区 -> 小时时区 */
	public static final int toHour(int offset) {
		return (offset / 3600000);
	}

	/** 日期 -> GMT */
	public static final long valueOf(int day) {
		int year = 1976;
		int month = 0;
		if (day > 10000) {
			year = day / 10000;
		}
		if (day > 100) {
			month = (day - (year * 10000)) / 100;
		}
		int date = (day % 100);
		Calendar c = Calendar.getInstance();
		c.set(year, (month - 1), date, 0, 0, 0);
		c.set(Calendar.MILLISECOND, -OFFSET);
		return c.getTimeInMillis();
	}

	/** GMT -> 当地时间 */
	public static final long valueOf(long time) {
		return (time + OFFSET);
	}

	/** 指定时间 -> GMT */
	public static final long valueOf(long time, int offset) {
		return (time - offset);
	}

	/** 年月转换 */
	public static final long valueOf(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(0); // 清除状态
		c.set(year, (month - 1), 0);
		c.add(Calendar.MILLISECOND, OFFSET);
		return c.getTimeInMillis();
	}

	public static final long valueOf(String tday) {
		return valueOf(tday, OFFSET);
	}

	public static final long valueOf(String tday, int offset) {
		if (tday == null || tday.length() < 8) {
			return GMTime.currentTimeMillis();
		}
		String str = tday.trim();
		Calendar c = Calendar.getInstance();
		try {
			Matcher m = Pattern.compile("^(\\d{4})(年|-|/|\\.)(\\d+)(月|-|/|\\.)(\\d{1,2})([^\\d+])?((2[0-3]|[01]?\\d):([0-5]\\d+):?([0-5]\\d+)?)?").matcher(str);
			if (m.find()) {
				StringBuffer sb = new StringBuffer();
				sb.append(m.group(1)).append("-").append(m.group(3)).append("-").append(m.group(5)).append(" ");
				if (m.group(10) == null) {
					if (m.group(8) == null) {
						c.setTime(OUT_SHORT.parse(sb.toString()));
					} else {
						c.setTime(OUT_FULL.parse(sb.append(m.group(7)).append(":00").toString()));
					}
				} else {
					c.setTime(OUT_FULL.parse(sb.append(m.group(7)).toString()));
				}
			} else if (str.length() == 8) {
				c.setTime(TimeUtils.get("yyyyMMdd").parse(str));
			}
		} catch (Exception e) {
			c.setTimeInMillis(System.currentTimeMillis());
		} finally {
			str = null;
		} // 输出时间
		c.add(Calendar.MILLISECOND, -offset);
		return c.getTimeInMillis();
	}
}
