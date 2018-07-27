package com.ypiao.timer;

import com.ypiao.service.SysBeaner;
import com.ypiao.util.GMTime;
import com.ypiao.util.GState;

public class RunAtToday implements Runnable {

	public void run() {
		long time = GMTime.currentTimeMillis();
		try {
			GState.reToday(time); // 计算当前时间
			SysBeaner.getTradeTaskService().schedule();
		} catch (Throwable e) {
			// Ignored
		}
	}
}
