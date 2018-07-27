package com.ypiao.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.framework.beans.BeansException;
import org.framework.context.ApplicationContext;
import org.framework.context.ApplicationContextAware;
import org.framework.context.support.ResourceBundleMessageSource;
import com.ypiao.service.imp.ABeaner;

public class SysBeaner implements ApplicationContextAware {

	/** Spring 管理对象 */
	private static ApplicationContext AC = null;

	private static Map<String, String> AK = new HashMap<String, String>();

	private static ResourceBundleMessageSource rms;

	private static APIBaseService apiBaseService;

	private static PayInfoService payInfoService;

	private static DownloadService downloadService;

	private static TradeTaskService tradeTaskService;

	private static UserLogerService userLogerService;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		if (context == null) {
			// Ignored
		} else {
			new ABeaner(AC = context);
		} // 配置信息加载
		try {
			rms = (ResourceBundleMessageSource) AC.getBean("messageSource");
			apiBaseService = (APIBaseService) AC.getBean("apiBaseService");
			payInfoService = (PayInfoService) AC.getBean("payInfoService");
			downloadService = (DownloadService) AC.getBean("downloadService");
			tradeTaskService = (TradeTaskService) AC.getBean("tradeTaskService");
			userLogerService = (UserLogerService) AC.getBean("userLogerService");
		} catch (Exception e) {
			// Ignored
		} finally {
			getAPIBaseService().initServer(context);
		}
	}

	// ==================== 临时对象信息 ====================
	public static void destroy() {
		apiBaseService = null;
		downloadService = null;
		tradeTaskService = null;
		userLogerService = null;
	}

	/** 获取资源信息 */
	public static String getMessage(String code, Object[] objs, Locale locale) {
		try {
			return rms.getMessage(code, objs, locale);
		} catch (Exception e) {
			return code;
		}
	}

	@SuppressWarnings("unchecked")
	public static <E> E get(Class<E> cls) {
		if (AC == null) {
			return null;
		}
		String sn = cls.getSimpleName();
		String key = AK.get(sn);
		if (key == null) {
			key = sn.substring(0, 3).toLowerCase();
			key += sn.substring(3);
			AK.put(sn, key);
		}
		return (E) AC.getBean(key);
	}

	public static APIBaseService getAPIBaseService() {
		return apiBaseService;
	}

	public static PayInfoService getPayInfoService() {
		return payInfoService;
	}

	public static DownloadService getDownloadService() {
		return downloadService;
	}

	public static TradeTaskService getTradeTaskService() {
		return tradeTaskService;
	}

	public static UserLogerService getUserLogerService() {
		return userLogerService;
	}
}
