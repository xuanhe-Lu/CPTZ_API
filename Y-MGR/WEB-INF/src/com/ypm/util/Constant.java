package com.ypm.util;

import java.nio.charset.Charset;

public class Constant {

	public static final String VERSION = "V1.0";

	public static final String VERSURL = "www.sunsw.com";

	public static final String MAKEDAY = "2018-03-13";
	/** 系统总缓存文件夹名称 */
	public static final String OSCACHE = "cache";
	/** 系统总导出文件夹名称 */
	public static final String EXPORTS = "export";

	public static Charset CHARSET = Charset.forName("UTF-8");
	/** 系统运行路径 */
	public static String ROOTPATH = "G:\\Java\\workspace\\Y-MGR\\";
	/** 系统服务名称 */
	public static String SERVER_NAME = "Tomcat7";
	/** 系统服务端口 */
	public static int SERVER_PORT = 80;
	/** AJP协议端口 */
	public static int SERVER_AJP = 0;
	/** 系统服务PId */
	public static int SERVER_PID = 0;
	/** 执行文件后缀 */
	public static String SERVLET_MAPPING = ".do";
	/** 系统起始时间=2010-01-01 */
	public static final long USE_START = 1262275200000L;
	/** 系统起始时间=2010-01-01 */
	public static final String USE_STIME = "2010-01-01";

	public static final String USE_SESSION_KEY = "user_session";

	public static final String WEB_AUTHCODE = "Y_code";

	public static final String WEB_AUTHINFO = "Y_info";

	public static final String WEB_AUTHSID = "Y_Psid";
	/** 信息分隔符 */
	public static final String REG_EX = "\u002C\u0001";
	// ---------- 状态信息常量 ----------
	/** 升序 */
	public static final int ORDER_ASC = 0;
	/** 降序 */
	public static final int ORDER_DESC = 1;
	// ---------- 标识信息常量 ----------
	public static String COOKIE_DOMAIN = null;

	public static String COOKIE_PATH = "/";

	public static String COOKIE_KEYS = "LK0Vc2LC7BXpQ3aSLDSYyIBrJqGKXkOM";
	/** 同步地址 */
	public static String SYS_ADDFS = "127.0.0.1";
	/** 管理地址 */
	public static String SYS_ADMIN = "127.0.0.1";
	/** 中心标识 */
	public static String SYS_SSIA = "S";

	public static final Charset SYS_UTF8 = Charset.forName("UTF-8");
	/** 是否调试模式 */
	public static boolean USE_DEBUG = false;
	/** 静态化地址 */
	public static boolean USE_REWRITE = true;
	
	/** png图片格式 */
	public static final String PNG = "png";
	
	/** jpg图片格式 */
	public static final String JPG = "jpg";
	
	public static final String[] EXPORT_COL = new String[] { 
		"票据ID", "借款者类型", "票据名称", "票据金额", "出票日期", "承兑日期", 
		"资金保障", "承兑银行(全称)", "承兑银行(简称)", "还款日期", "借款日期", 
		"借款天数", "借款利息", "管理费率", "借款企业名称", "企业法人代表", 
		"法人联系方式", "企业开户行", "企业银行账号", "总金额", "年化利率", 
		"占用金额", "募集金额", "实际打款", "管理费用", "利息收益", "到期回款", 
		"票据状态", "更新时间", "操作ID", "操作员"
	};

}
