package com.ypiao.util;

import java.nio.charset.Charset;
import java.util.Map;
import org.commons.lang.LRUMap;

public class Constant {

	public static final String VERSION = "V1.0";

	public static final String VERSURL = "www.sunsw.com";

	public static final String MAKEDAY = "2018-03-12";
	/** 系统总缓存文件夹名称 */
	public static final String OSCACHE = "cache";

	public static Charset CHARSET = Charset.forName("UTF-8");
	/** 文件系统路径 */
	public static String FILEPATH = "E:\\WebRoot\\Y-IMG\\";
	/** 系统运行路径 */
	public static String ROOTPATH = "E:\\WebRoot\\Y-API\\";
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

	public static final String USE_SESSION_KEY = "user_session";

	public static final String USE_SESSION_URL = "urls_session";

	public static final String CODE_SESSION_KEY = "code_session";

	public static final String WEB_AUTHCODE = "Y_code";

	public static final String WEB_AUTHINFO = "Y_info";

	public static final String WEB_AUTHSID = "Y_sid";

	// ---------- 状态信息常量 ----------
	public static final int PAGE_OK = 200;
	/** 信息不存在 */
	public static final int PAGE_NFOUND = 404;
	/** 系统错误 */
	public static final int PAGE_FAILED = 500;

	// ---------- 标识信息常量 ----------
	public static String COOKIE_DOMAIN = null;

	public static String COOKIE_PATH = "/";

	public static String COOKIE_KEYS = "R040mOPefpIJi2prK6MdnOMwqH9myPKP";
	/** 缓存锁定信息 */
	public static Map<String, Long> SYS_CACHE_KEYS = new LRUMap<String, Long>();
	/** 缓存最小时间 */
	public static final long SYS_CACHE_TIME = 15 * 1000L;
	/** 系统时间格式 */
	public static String SYS_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** 管理地址 */
	public static String SYS_ADMIN_HOST = "127.0.0.1";
	/** 管理端口 */
	public static int SYS_ADMIN_PORT = 8603;
	/** 服务端口 */
	public static int SYS_SERVER_PORT = 8080;

	public static String SYS_ROOT = "S1";
	/** 中心标识 */
	public static String SYS_SSIA = "K";
	/** 本机标识 */
	public static String SYS_SSID = "K1";

	public static final Charset SYS_UTF8 = Charset.forName("UTF-8");
	/** 是否开发模式 */
	public static boolean USE_DEBUG = false;
	/** 静态化地址 */
	public static boolean USE_REWRITE = true;
}
