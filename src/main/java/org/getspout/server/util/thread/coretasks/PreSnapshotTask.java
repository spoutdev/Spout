package org.getspout.server.util.thread.coretasks;

import java.io.Serializable;

import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.ManagementRunnable;
import org.getspout.server.util.thread.ManagementTaskEnum;

public class PreSnapshotTask extends ManagementRunnable {

	private static final long serialVersionUID = 1L;

	public Serializable call(AsyncExecutor executor) throws InterruptedException {
		executor.getManager().preSnapshotRun();
		return null;
	}

	@Override
	public ManagementTaskEnum getEnum() {
		return ManagementTaskEnum.PRE_SNAPSHOT;
	}

}
