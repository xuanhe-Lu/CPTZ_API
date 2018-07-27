package com.ypiao.test;

import com.ypiao.util.GMTime;

/**
 * Created by xk on 2018-07-04.
 * 
 * 将数据库中时间字段转成具体日期. 
 */
public class Timestamp {
	
	public static void main(String[] args) {
		long timeStamp = 1530656610984L;
		
		String res = GMTime.format( timeStamp, GMTime.CHINA );
		System.out.println(res);
	}

}
