package com.ypiao.timer;

import com.ypiao.service.SysBeaner;
import org.apache.log4j.Logger;

public class RunAtSpider implements Runnable {
	private static Logger logger = Logger.getLogger(RunAtSpider.class);
	public void run() {
		try {
			logger.info("自动结标执行");
			SysBeaner.getTradeTaskService().doProd();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
