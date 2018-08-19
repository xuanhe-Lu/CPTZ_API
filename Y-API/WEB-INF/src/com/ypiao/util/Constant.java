package com.ypiao.util;

import org.commons.lang.LRUMap;

import java.nio.charset.Charset;
import java.util.Map;

public class Constant {

    public static final String VERSION = "V1.0";

    public static final String VERSURL = "www.sunsw.com";

    public static final String MAKEDAY = "2018-03-12";
    /**
     * 系统总缓存文件夹名称
     */
    public static final String OSCACHE = "cache";

    public static Charset CHARSET = Charset.forName("UTF-8");
    /**
     * 文件系统路径
     */
    public static String FILEPATH = "E:\\WebRoot\\Y-IMG\\";
    /**
     * 系统运行路径
     */
    public static String ROOTPATH = "E:\\WebRoot\\Y-API\\";
    /**
     * 系统服务名称
     */
    public static String SERVER_NAME = "Tomcat7";
    /**
     * 系统服务端口
     */
    public static int SERVER_PORT = 80;
    /**
     * AJP协议端口
     */
    public static int SERVER_AJP = 0;
    /**
     * 系统服务PId
     */
    public static int SERVER_PID = 0;
    /**
     * 执行文件后缀
     */
    public static String SERVLET_MAPPING = ".do";

    public static final String USE_SESSION_KEY = "user_session";

    public static final String USE_SESSION_URL = "urls_session";

    public static final String CODE_SESSION_KEY = "code_session";

    public static final String WEB_AUTHCODE = "Y_code";

    public static final String WEB_AUTHINFO = "Y_info";

    public static final String WEB_AUTHSID = "Y_sid";

    // ---------- 状态信息常量 ----------
    public static final int PAGE_OK = 200;
    /**
     * 信息不存在
     */
    public static final int PAGE_NFOUND = 404;
    /**
     * 系统错误
     */
    public static final int PAGE_FAILED = 500;

    // ---------- 标识信息常量 ----------
    public static String COOKIE_DOMAIN = null;

    public static String COOKIE_PATH = "/";

    public static String COOKIE_KEYS = "R040mOPefpIJi2prK6MdnOMwqH9myPKP";
    /**
     * 缓存锁定信息
     */
    public static Map<String, Long> SYS_CACHE_KEYS = new LRUMap<String, Long>();
    /**
     * 缓存最小时间
     */
    public static final long SYS_CACHE_TIME = 15 * 1000L;
    /**
     * 系统时间格式
     */
    public static String SYS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 管理地址
     */
    public static String SYS_ADMIN_HOST = "127.0.0.1";
    /**
     * 管理端口
     */
    public static int SYS_ADMIN_PORT = 8603;
    /**
     * 服务端口
     */
    public static int SYS_SERVER_PORT = 8080;

    public static String SYS_ROOT = "S1";
    /**
     * 中心标识
     */
    public static String SYS_SSIA = "K";
    /**
     * 本机标识
     */
    public static String SYS_SSID = "K1";

    public static final Charset SYS_UTF8 = Charset.forName("UTF-8");
    /**
     * 是否开发模式
     */
    public static boolean USE_DEBUG = false;
    /**
     * 静态化地址
     */
    public static boolean USE_REWRITE = true;

    // 网站地址
    public static String URL = "chinamtea.com";
    //白银猫图片地址
    public static String SILVER_CAT_IMG = "app.yingpiaolicai.com/img/cat/home_img_car_nor.png";
    //黄金猫图片地址
    public static String GOLD_CAT_IMG = "60.205.191.116:8081/app/images/goldcat.png";

    //富有协议支付加密方式
    public static String SIGN_TP = "MD5";

    // 富有协议卡绑定发送短信地址 测试地址在上
//	public static  String SEND_SMS = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/bindMsg.pay";//测试地址
    public static String SEND_SMS = "https://mpay.fuiou.com/newpropay/bindMsg.pay";//生产地址

    // 富有协议卡绑定地址
    public static String PROTOCOL_BIND = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/bindCommit.pay"; //测试地址
//    	public static  String PROTOCOL_BIND =  "https://mpay.fuiou.com/newpropay/bindCommit.pay";//生产地址

//	协议解绑接口
    public static String UNBIND = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/unbind.pay"; //测试地址
//    	public static  String UNBIND =  "https://mpay.fuiou.com/newpropay/unbind.pay"; //生产地址

    //协议支付接口
    public static String ORDER = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/order.pay";//测试地址
//    public static String ORDER = "https://mpay.fuiou.com/newpropay/order.pay";//生产地址

    //商户支持卡 Bin 查询接口
    public static String CARD_BIN_QUERY = "http://www-1.fuiou.com:18670/mobile_pay/findPay/cardBinQuery.pay";//测试地址
//    public static String CARD_BIN_QUERY = "https://mpay.fuiou.com/findPay/cardBinQuery.pay";//生产地址



    public static String FUIOU_PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBCcvUDkw3ONsVx7Rzh9IJoKKurwBnKSjJEJbLXQWDKIPZMtmxcHa5jNu6OgpQ0BatOYl4p4BmgH3HzVwWyn6iDOsDlxwZezFzArtPjtECq241nfmoGhbz9lMr7T56yY5PhATws32Dm1ZQbY8DvsFvTe2hKgmIGbZQ030seRnfSwIDAQAB";
    public static String FUIOU_PRI_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIbA52JWbirSYa2iTd/P7G6NGgOAAmgGFcTaktRVhHtgeeTHd24iT2MNTCIw/3ykcWu/55hbpHBcZIiLf/XZ940iaeSgIGmfoJa9xdVmZ5l4ElPUVtLJMntUfbPdAMP8SEwjMP8Nr6PvzjcKXS5GCfCuTW/F/dKz1mR1LOcxAkLBAgMBAAECgYBjkzBoLk4CPqwHTqQU+uRPXN0YMQOWMsjrSkittvPK56OrNuo97ASVwUG9Ek/4ntthL9HHeBCvJtbzP4Iy/fo6sevZVcaURNb3mn1R/gdIitwFur8bdF+VA5mZX8cTR4D4liZZvBHwx+UtvdWClzoOSeSpFZn7/6nMXpYzam3WQQJBALvXIHeAdPrtktmRtqmdVNYGqmgtE7jqkaqZ9VgUMcIt8W01oPEDp27NtmGTM06nneIk/ajagq97nsbc6JPa6PUCQQC3pm9RM782qnL/5fzNsv7HyTjFAlIg3Q+PNlSj1d3ekNlqRJ0hv4/aLiqrLqtqbfHu98aeGt4JsdilT/Z9rwMdAkEAlFgwFtBHEkh/Wf3ewRM0hZZcC8vVsIrnoVDXVskUBuNbsEDTKqQVHceuSl8C/RIY+Rj3jtuKq+W4HhsmPmZ65QJAbtbypG244ENreOrT80ou32Gg87Z83vzMoUDHQMKZT/TYY3zZ4T5+kc3/TqWyK2AD/photY+9pthByzRBroVsOQJAMUQ/c+Mngb8kKxU+mF/CwDSlwbL8/lM/xoDnT/qQxmxTiEysohd3jO98C0BA3+YHFswaXKtY7/Tp/H1VeX9EdA==";


}
