package org.getspout.server.util.thread.coretasks;

import java.io.Serializable;

import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.ManagementRunnable;
import org.getspout.server.util.thread.ManagementTaskEnum;

public class CopySnapshotTask extends ManagementRunnable {
	
	private static final long serialVersionUID = 1L;
	
	public Serializable call(AsyncExecutor executor) throws InterruptedException {
		executor.getManager().copySnapshotRun();
		return null;
	}

	@Override
	public ManagementTaskEnum getEnum() {
		return ManagementTaskEnum.COPY_SNAPSHOT;
	}

}
