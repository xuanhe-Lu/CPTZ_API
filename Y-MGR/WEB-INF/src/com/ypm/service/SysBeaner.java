package com.ypm.service;

import java.util.HashMap;
import java.util.Map;
import org.framework.beans.BeansException;
import org.framework.context.ApplicationContext;
import org.framework.context.ApplicationContextAware;
import com.ypm.service.imp.ABeaner;

public class SysBeaner implements ApplicationContextAware {

	/** Spring 管理对象 */
	public static ApplicationContext AC = null;

	private static Map<String, String> AK = new HashMap<String, String>();

	private static APIBaseService apiBaseService;

	private static DictInfoService dictInfoService;

	private static DownloadService downloadService;

	private static TradeTaskService tradeTaskService;

	public SysBeaner() {
	}

	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		if (ac == null) {
			// Ignored
		} else {
			new ABeaner(AC = ac);
		} // 配置信息加载
		try {
			apiBaseService = (APIBaseService) AC.getBean("apiBaseService");
			dictInfoService = (DictInfoService) AC.getBean("dictInfoService");
			downloadService = (DownloadService) AC.getBean("downloadService");
			tradeTaskService = (TradeTaskService) AC.getBean("tradeTaskService");
		} catch (Exception e) {
			e.printStackTrace();
			// Ignored
		} finally {
			getAPIBaseService().initServer(ac);
		}
	}

	// ==================== 临时对象信息 ====================
	public static void destroy() {
		apiBaseService = null;
		dictInfoService = null;
		tradeTaskService = null;
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

	public static DictInfoService getDictInfoService() {
		return dictInfoService;
	}

	public static DownloadService getDownloadService() {
		return downloadService;
	}

	public static TradeTaskService getTradeTaskService() {
		return tradeTaskService;
	}
}
