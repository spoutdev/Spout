package org.spout.engine.scheduler.parallel;

import java.lang.ref.ReferenceQueue;

import org.spout.engine.world.SpoutRegion;

public class RegionIdPair {
	private final int taskId;
	private final SpoutRegion region;
	
	public RegionIdPair(int id, SpoutRegion r) {
		this.taskId = id;
		this.region = r;
	}
	
	public final SpoutRegion getRegion() {
		return region;
	}
	
	public final int getTaskId() {
		return taskId;
	}
}
