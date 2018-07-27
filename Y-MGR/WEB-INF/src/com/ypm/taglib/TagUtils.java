package com.ypm.taglib;

public class TagUtils {

	/** 检测是否为空 */
	public static final boolean isBlank(String str) {
		if (str == null || str.length() < 1) {
			return true;
		} else {
			return false;
		}
	}

	/** 检测是否为空 */
	public static final boolean isNotBlank(String str) {
		if (str == null || str.length() < 1) {
			return false;
		} else {
			return true;
		}
	}

	public static final String getName(String name, String def) {
		if (isBlank(name)) {
			return def;
		} else {
			return name;
		}
	}

	public static final String getId(String id, String name) {
		if (isBlank(id)) {
			return id;
		} else {
			return name;
		}
	}

}
