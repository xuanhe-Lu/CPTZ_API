package com.ypiao.util;

/**
 * Created by xk on 2018-05-17.
 * 
 * 数组工具类.
 */
public final class ArrUtils {
	
	private static final StringBuilder STRING_BUILDER = new StringBuilder();
	
	/**
	 * @param arr[] int
	 * @return String
	 * 
	 * 把数组转化成字符串,以逗号分割
	 */
	public static String convertArr2Str(int arr[]) {
		if (arr == null) {
			return null;
		}
		
		if (arr.length == 0) {
			return "";
		}
		
		for (int i = 0; i < arr.length; i++) {
			STRING_BUILDER.append( i == arr.length - 1 ? arr[i] : arr[i] + "," );
		}
		
		return STRING_BUILDER.toString();
	}
}
