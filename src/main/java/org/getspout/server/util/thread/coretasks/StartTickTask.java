package org.getspout.server.util.thread.coretasks;

import java.io.Serializable;

import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.ManagementRunnable;
import org.getspout.server.util.thread.ManagementTaskEnum;

public class StartTickTask extends ManagementRunnable {

	private static final long serialVersionUID = 1L;
	
	private long delta;

	public StartTickTask() {
		delta = 0;
	}

	public StartTickTask(long delta) {
		this.delta = delta;
	}

	public StartTickTask setDelta(long delta) {
		this.delta = delta;
		return this;
	}

	public Serializable call(AsyncExecutor executor) throws InterruptedException {
		executor.getManager().startTickRun(delta);
		return null;
	}
	
	@Override
	public ManagementTaskEnum getEnum() {
		return ManagementTaskEnum.START_TICK;
	}

}