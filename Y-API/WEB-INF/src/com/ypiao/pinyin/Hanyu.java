package com.ypiao.pinyin;

public class Hanyu {

	/** 拼音检索信息 */
	public static String toIndex(String str) {
		return PinyinHelper.toIndex(str, 40, '#');
	}

	public static void main(String arg[]) {
		Logger.info(toIndex("ige"));
		Logger.info(toIndex("嬲"));
	}
}
