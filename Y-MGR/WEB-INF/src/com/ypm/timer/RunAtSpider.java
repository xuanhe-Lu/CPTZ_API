package com.ypm.timer;

import com.ypm.service.SysBeaner;

public class RunAtSpider implements Runnable {

	public void run() {
		try {
			SysBeaner.getTradeTaskService().doProd();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
