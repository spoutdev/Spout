package org.getspout.server.util.thread.coretasks;

import java.io.Serializable;

import org.getspout.server.util.thread.ManagementAsyncExecutor;
import org.getspout.server.util.thread.ManagementRunnable;

public class StartTickTask extends ManagementRunnable {

	private static final long serialVersionUID = 1L;

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

	public Serializable call(ManagementAsyncExecutor executor) throws InterruptedException {
		executor.startTickRun(t);
		return null;
	}

}