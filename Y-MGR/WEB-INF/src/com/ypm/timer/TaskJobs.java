package com.ypm.timer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.ypm.bean.SiteJobs;
import com.ypm.util.GMTime;

public class TaskJobs extends TimerTask {

	private List<SiteJobs> jobs;

	public TaskJobs(List<SiteJobs> jobs, Timer timer, long time) {
		long delay = jobs.get(0).getNext() - time;
		if (delay <= 0) delay = 0;
		this.setJobs(jobs); // 任务对象
		timer.schedule(this, delay, 1000L);
	}

	public List<SiteJobs> getJobs() {
		return jobs;
	}

	public void setJobs(List<SiteJobs> jobs) {
		this.jobs = jobs;
	}

	@Override
	public void run() {
		long time = 0;
		List<SiteJobs> ls = this.getJobs();
		try {
			while (ls.size() > 0) {
				SiteJobs job = ls.get(0);
				time = GMTime.currentTimeMillis();
				if (job.getNext() > time) break;
				ls.remove(0); // 移除当前对象
				switch(job.getType()) {
				case 0: // 系统脚本
					JobService.execsys(job.getScript(), time); break;
				case 1: // SQL语句
					JobService.execute(job.getScript()); break;
				case 2: // SQL脚本
					JobService.execute(job.getScript()); break;
				case 3: // CMD脚本
					break;
				case 4: // HTTP访问
					break;
				case 5: // Tomcat服务
					break;
				case 6:
					break;
				} // update SiteJobs
				if (job.getType() == 7) {
					
				} else {
					JobService.saveSiteJobs(job);
				}
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			if (ls == null || ls.size() <= 0) {
				this.cancel(); // 结束任务
			}
		}
	}

}
