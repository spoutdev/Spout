package org.getspout.server.util.thread.coretasks;

import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.ManagementTask;

public class CopySnapshotTask implements ManagementTask {

	private static final long serialVersionUID = 1L;

	@Override
	public void run(AsyncExecutor executor) throws InterruptedException {
		executor.copySnapshot();
	}

}
