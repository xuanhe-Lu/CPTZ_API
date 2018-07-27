package com.ypm.timer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.commons.lang.RandomUtils;
import com.ypm.bean.SiteJobs;
import com.ypm.service.PoolService;
import com.ypm.util.GMTime;
import com.ypm.util.GState;

public class TaskService extends TimerTask {

	private static TaskService TASK = null;

	private Timer timer = null;

	private int count = 10;

	private int times = 0;

	public TaskService() {
		this.timer = new Timer();
	}
	/** 停止注销线程 */
	public static void close() {
		if (TASK == null) {
			// Ignored
		} else {
			TASK.cancel();
			TASK.timer.cancel();
			TASK.timer.purge();
		}
		TASK = null;
	}
	/** 启动操作线程 */
	public static void start() {
		if (TASK == null) {
		} else {
			TASK.cancel();
		} // 创建相应定时器
		TASK = new TaskService();
		TASK.count = RandomUtils.randomNumeric(5, 25);
		TASK.timer.schedule(TASK, 5000, 60000L);
		TaskConfig.init(5000);
	}
	/** 获取定时器 */
	protected static Timer timer() {
		if (TASK == null) {
			TASK = new TaskService();
		}
		return TASK.timer;
	}
	@Override
	public void run() {
		long time = GMTime.offset();
		try {
			if (GState.USER_TODAY <= time || GState.USER_TODAX >= time) {
				PoolService.getService().execute(new RunAtToday());
			} // 检测计划任务信息
			PoolService.getService().execute(new RunAtSpider());
			List<SiteJobs> jobs = JobService.getSiteJobs(time);
			if (jobs == null || jobs.size() <= 0) {
				// ignored
			} else {
				new TaskJobs(jobs, this.timer, time);
			} // 检测网络状态
			if (this.times == this.count) {
				//PoolService.getService().execute(new RunAtTime(time));
			} else if (this.times >= 30) {
				this.times = 0;
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			this.times += 1;
		}
	}

}
