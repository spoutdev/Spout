package org.getspout.server;

import org.getspout.api.Server;
import org.getspout.server.util.thread.AsyncExecutor;
import org.getspout.server.util.thread.AsyncManager;

/**
 * This class just passes through the period method calls to the SpoutRegion
 */
public class SpoutRegionManager extends AsyncManager {

	private final SpoutRegion parent;

	public SpoutRegionManager(SpoutRegion parent, int maxStage, AsyncExecutor executor, Server server) {
		super(maxStage, executor, server);
		this.parent = parent;
	}

	public SpoutRegion getParent() {
		return parent;
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		parent.copySnapshotRun();

	}

	@Override
	public void startTickRun(int stage, long delta) throws InterruptedException {
		parent.startTickRun(stage, delta);
	}

	@Override
	public void haltRun() throws InterruptedException {
		parent.haltRun();
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		parent.preSnapshotRun();
	}

}
