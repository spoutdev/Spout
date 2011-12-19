package org.getspout.server.util.thread.coretasks;

import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.ManagementTask;

public class StartTickTask implements ManagementTask {

	private long t;
	
	public StartTickTask() {
		t = 0;
	}
	
	public StartTickTask(long ticks) {
		t = ticks;
	}

	public StartTickTask setTicks(long ticks) {
		t = ticks;
		return this;
	}

	public void run(AsyncExecutor executor) throws InterruptedException {
		executor.startTickRun(t);
	}

}