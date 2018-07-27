package com.ypiao.timer;

import com.ypiao.service.SysBeaner;

public class RunAtSpider implements Runnable {

	public void run() {
		try {
			SysBeaner.getTradeTaskService().doProd();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
