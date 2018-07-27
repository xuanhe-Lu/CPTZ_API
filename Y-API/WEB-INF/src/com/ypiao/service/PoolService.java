package com.ypiao.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import com.sunsw.http.core.client.CloseableHttpClient;
import com.sunsw.http.core.client.HttpClients;

public class PoolService {

	private static CloseableHttpClient HC;

	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

	private static ExecutorService server = Executors.newCachedThreadPool();

	static {
		HC = HttpClients.custom().setMaxConnPerRoute(10).setMaxConnTotal(200).build();
	}

	public static CloseableHttpClient getHttpClient() {
		return HC;
	}

	public static ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	public static ExecutorService getService() {
		return server;
	}
}
