package org.getspout.server.util.thread.coretasks;

import java.io.Serializable;

import org.getspout.server.util.thread.ManagementAsyncExecutor;
import org.getspout.server.util.thread.ManagementRunnable;

public class CopySnapshotTask extends ManagementRunnable {
	
	private static final long serialVersionUID = 1L;

	public Serializable call(ManagementAsyncExecutor executor) throws InterruptedException {
		executor.copySnapshot();
		return null;
	}

}
