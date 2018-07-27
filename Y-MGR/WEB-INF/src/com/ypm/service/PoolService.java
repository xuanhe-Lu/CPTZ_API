package com.ypm.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PoolService {

	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

	private static ExecutorService server = Executors.newCachedThreadPool();

	static {
	}
	/** 定时任务线程池 */
	public static ScheduledExecutorService getScheduler() {
		return scheduler;
	}
	/** 普通任务线程池 */
	public static ExecutorService getService() {
		return server;
	}

}
