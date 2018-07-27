package com.ypm.util;

import java.io.Serializable;
/**
 * @title	系统配置键值
 * @author	Administrator
 * @version	2018-03-13
 */
public interface ConfigKey extends Serializable {

	public static final String SITE_NAME = "SITE_NAME";

	public static final String SITE_TEL = "SITE_TEL";

	public static final String USE_DEBUG = "USE_DEBUG";

	public static final String USE_REWRITE = "USE_REWRITE";
	
	// 短信验证码通道账户
	public static final String IDENTIFY_CODE_ACCOUNT = "IDENTIFY_CODE_ACCOUNT";
	
	// 短信验证码通道密码
	public static final String IDENTIFY_CODE_PSWD = "IDENTIFY_CODE_PSWD";
}
