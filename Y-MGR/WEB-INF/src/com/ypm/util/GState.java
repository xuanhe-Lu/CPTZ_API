package com.ypm.util;

import java.util.Calendar;

public class GState implements AState, APState {
	/** 日期信息 */
	public static int USER_TOADD = 0;
	/** 今日开始值 */
	public static long USER_TODAX = 0;
	/** 今日结束值 */
	public static long USER_TODAY = 0;
	/** 本月开始值 */
	public static long USER_MONTH_BEG = 0;
	/** 本月结束值 */
	public static long USER_MONTH_END = 0;

	public static String TODAY = "2018-03-12";

	public static void reToday() {
		reToday(GMTime.currentTimeMillis());
	}

	public static void reToday(long time) {
		if (time < USER_TODAY) {
			// Ignored
		} else {
			int zone = GMTime.CHINA;
			USER_TOADD = GMTime.getTday(zone);
			Calendar c = GMTime.getToday(zone);
			USER_TODAX = c.getTimeInMillis();
			c.add(Calendar.DATE, 1);
			USER_TODAY = c.getTimeInMillis();
			c.add(Calendar.MONTH, 1);
			c.set(Calendar.DAY_OF_MONTH, 0);
			USER_MONTH_END = c.getTimeInMillis();
			c.add(Calendar.MONTH, -1);
			USER_MONTH_BEG = c.getTimeInMillis();
			TODAY = GMTime.format(zone, "yyyy-MM-dd");
		}
	}

}
