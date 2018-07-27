package com.ypm.timer;

import java.sql.SQLException;
import java.util.TimerTask;

public class TaskConfig extends TimerTask {

	private static boolean STATUS = false;

	int times = 0;

	public TaskConfig() {
	}

	public static void init(long delay) {
		if (delay < 0) delay = 0;
		TaskConfig task = new TaskConfig();
		TaskService.timer().schedule(task, delay, 1000L);
	}
	@Override
	public void run() {
		if (STATUS) return;
		try {
			STATUS = true;
			JobService.initServer();
			this.cancel();
		} catch (SQLException e) {
			// Ignored
		} finally {
			STATUS = false;
			if (times++ > 5) this.cancel();
		}
	}

}
