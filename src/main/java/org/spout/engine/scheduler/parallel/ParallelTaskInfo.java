package org.spout.engine.scheduler.parallel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TickStage;

import org.spout.engine.scheduler.SpoutTask;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.world.SpoutRegion;

public class ParallelTaskInfo {
	public static final ParallelTaskInfo[] EMPTY_ARRAY = new ParallelTaskInfo[0];
	private final Set<RegionIdPair> children = new HashSet<RegionIdPair>();
	private final Map<SpoutRegion, SpoutTask> regions = new HashMap<SpoutRegion, SpoutTask>();
	private final SpoutTask task;
	private boolean alive = true;

	public ParallelTaskInfo(SpoutTask task) {
		this.task = task;
	}

	public synchronized boolean add(SpoutRegion region) {
		if (regions.containsKey(region)) {
			return false;
		}
		if (!alive) {
			return false;
		} else {
			SpoutTask newTask = task.getRegionTask(region);
			if (newTask == null) {
				Spout.getLogger().info("Unable to create parallel task for " + task);
			}
			int newId = ((SpoutTaskManager) region.getTaskManager()).schedule(newTask);
			children.add(new RegionIdPair(newId, region));
			regions.put(region, newTask);
			return true;
		}
	}

	public synchronized boolean remove(SpoutRegion region) {
		TickStage.checkStage(TickStage.TICKSTART);
		if (!regions.containsKey(region)) {
			return false;
		} else {
			boolean success = false;
			Iterator<RegionIdPair> itr = children.iterator();
			while (itr.hasNext()) {
				RegionIdPair pair = itr.next();
				if (pair.getRegion() == region) {
					success = true;
					itr.remove();
				}
			}
			if (!success) {
				throw new IllegalStateException("Region exists in region map and but not in children map");
			}
			if (regions.remove(region) == null) {
				throw new IllegalStateException("Region exists in region map with contains but cannot be removed");
			}
			return success;
		}
	}

	public synchronized Task getTask(Region r) {
		return regions.get(r);
	}

	public synchronized void stop() {
		alive = false;
		for (RegionIdPair regionId : children) {
			SpoutRegion r = regionId.getRegion();
			if (r != null) {
				r.getTaskManager().cancelTask(regionId.getTaskId());
			}
		}
	}

	public SpoutTask getTask() {
		return task;
	}
}