package org.spout.engine.scheduler.parallel;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import org.spout.engine.world.SpoutRegion;

public class RegionIdPair {
	private final int taskId;
	private final WeakReference<SpoutRegion> region;

	public RegionIdPair(int id, SpoutRegion r, ReferenceQueue<SpoutRegion> q) {
		this.taskId = id;
		this.region = new MarkedWeakReference<SpoutRegion, RegionIdPair>(r, this, q);
	}

	public final SpoutRegion getRegion() {
		return region.get();
	}

	public final int getTaskId() {
		return taskId;
	}
}
