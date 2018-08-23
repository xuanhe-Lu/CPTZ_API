package com.ypiao.timer;

import com.ypiao.service.SysBeaner;

public class RunAtSpider implements Runnable {

	public void run() {
		try {
			System.out.println("自动结标执行");
			SysBeaner.getTradeTaskService().doProd();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
