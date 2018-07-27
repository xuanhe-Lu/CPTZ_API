package com.ypiao.util;

import java.io.Serializable;

/**
 * @title 系统配置键值
 * @author Administrator
 * @version 2018-03-13
 */
public interface ConfigKey extends Serializable {

	public static final String USE_DEBUG = "USE_DEBUG";

	public static final String USE_REWRITE = "USE_REWRITE";

	public static final String USE_SENDSMS = "USE_SENDSMS";

	public static final String COMPANY = "COMPANY";

	public static final String VERSION = "VERSION";

	public static final String SITE_NAME = "SITE_NAME";

	public static final String SITE_TEL = "SITE_TEL";

	public static final String SITE_URL = "SITE_URL";

	public static final String KEFU_TEL = "KEFU_TEL";

	public static final String KEFU_EMAIL = "KEFU_EMAIL";

	public static final String KEFU_WEIXI = "KEFU_WEIXI";

	public static final String KEFU_WXGZH = "KEFU_WXGZH";

	public static final String SYS_CASH_FEE = "SYS_CASH_FEE";

	public static final String SYS_CASH_MONTH = "SYS_CASH_MONTH";

	public static final String SYS_CASH_TOMIN = "SYS_CASH_TOMIN";

	public static final String SYS_SHARE_URL = "SYS_SHARE_URL";

	public static final String SYS_ACT_INFB = "SYS_ACT_INFB";
	
	// 短信验证码通道账户
	public static final String IDENTIFY_CODE_ACCOUNT = "IDENTIFY_CODE_ACCOUNT";
		
	// 短信验证码通道密码
	public static final String IDENTIFY_CODE_PSWD = "IDENTIFY_CODE_PSWD";
}
