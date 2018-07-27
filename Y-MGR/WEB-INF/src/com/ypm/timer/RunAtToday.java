package com.ypm.timer;

import com.ypm.service.SysBeaner;
import com.ypm.util.GMTime;
import com.ypm.util.GState;

public class RunAtToday implements Runnable {

	public void run() {
		long time = GMTime.currentTimeMillis();
		try {
			GState.reToday(time);
			SysBeaner.getTradeTaskService().doProd();
			SysBeaner.getTradeTaskService().schedule();
		} catch (Throwable e) {
			// Ignored
		}
	}
}
