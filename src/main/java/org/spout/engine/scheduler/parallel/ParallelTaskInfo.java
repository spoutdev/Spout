package org.spout.engine.scheduler.parallel;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.scheduler.Task;
import org.spout.engine.scheduler.SpoutTask;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.world.SpoutRegion;

public class ParallelTaskInfo {
	
	public static final ParallelTaskInfo[] EMPTY_ARRAY = new ParallelTaskInfo[0];
	
	private final Set<RegionIdPair> children = new HashSet<RegionIdPair>();
	
	private final Map<SpoutRegion, SpoutTask> regions = new HashMap<SpoutRegion, SpoutTask>();
	
	private final ReferenceQueue<SpoutRegion> refQueue = new ReferenceQueue<SpoutRegion>();
	
	private final SpoutTask task;
	
	private boolean alive = true;
	
	public ParallelTaskInfo(SpoutTask task) {
		this.task = task;
	}
	
	public synchronized boolean add(SpoutRegion region) {
		if (regions.containsKey(region)) {
			return false;
		} if (!alive) {
			return false;
		} else {
			SpoutTask newTask = task.getRegionTask(region);
			if (newTask == null) {
				Spout.getLogger().info("Unable to create parallel task for " + task);
			}
			int newId = ((SpoutTaskManager)region.getTaskManager()).schedule(newTask);
			children.add(new RegionIdPair(newId, region, refQueue));
			regions.put(region, newTask);
			return true;
		}
	}
	
	public synchronized Task getTask(Region r) {
		return regions.get(r);
	}
	
	// TODO - this is broken, need to fix or there will be a memory leak
	@SuppressWarnings("unchecked")
	public synchronized void prune() {
		MarkedWeakReference<SpoutRegion, RegionIdPair> ref = null;
		while ((ref = (MarkedWeakReference<SpoutRegion, RegionIdPair>)refQueue.poll()) != null) {
			RegionIdPair p = ref.getMark();
			children.remove(p);
			regions.remove(p.getRegion());
		}
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