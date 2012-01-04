package org.getspout.server.util.thread.coretasks;

import java.io.Serializable;

import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.ManagementRunnable;
import org.getspout.server.util.thread.ManagementTaskEnum;

public class StartTickTask extends ManagementRunnable {

	private static final long serialVersionUID = 1L;

	private long delta;
	private int stage;

	public StartTickTask() {
		delta = 0;
	}

	public StartTickTask(int stage, long delta) {
		this.stage = stage;
		this.delta = delta;
	}

	public StartTickTask setStageDelta(int stage, long delta) {
		this.delta = delta;
		this.stage = stage;
		return this;
	}

	public Serializable call(AsyncExecutor executor) throws InterruptedException {
		executor.getManager().startTickRun(stage, delta);
		return null;
	}

	@Override
	public ManagementTaskEnum getEnum() {
		return ManagementTaskEnum.START_TICK;
	}

}