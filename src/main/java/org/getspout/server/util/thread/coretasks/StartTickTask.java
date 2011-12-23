package org.getspout.server.util.thread.coretasks;

import java.io.Serializable;

import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.ManagementRunnable;
import org.getspout.server.util.thread.ManagementTask;
import org.getspout.server.util.thread.ManagementTaskEnum;

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

	public Serializable call(AsyncExecutor executor) throws InterruptedException {
		executor.getManager().startTickRun(t);
		return null;
	}
	
	@Override
	public ManagementTaskEnum getEnum() {
		return ManagementTaskEnum.START_TICK;
	}

}