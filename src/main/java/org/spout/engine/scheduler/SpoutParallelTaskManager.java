package org.spout.engine.scheduler;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.scheduler.TickStage;
import org.spout.api.scheduler.Worker;
import org.spout.api.util.map.concurrent.TSyncIntObjectHashMap;
import org.spout.api.util.map.concurrent.TSyncIntObjectMap;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

public class SpoutParallelTaskManager implements TaskManager {
	
	private final Engine engine;
	
	private final Collection<World> world;
	
	private final AtomicLong upTime;
	
	private long lastPrune = 0;
	
	private final TSyncIntObjectMap<ParallelTaskInfo> activeTasks = new TSyncIntObjectHashMap<ParallelTaskInfo>();
	
	private final ConcurrentLinkedQueue<SpoutRegion> newRegions = new ConcurrentLinkedQueue<SpoutRegion>();

	private final ConcurrentLinkedQueue<SpoutTask> newTasks = new ConcurrentLinkedQueue<SpoutTask>();
	
	private final Scheduler scheduler;

	public SpoutParallelTaskManager(Engine engine) {
		if (engine == null) {
			throw new IllegalArgumentException("Engine cannot be set to null");
		}
		upTime = new AtomicLong(0);
		this.engine = engine;
		this.world = null;
		this.scheduler = engine.getScheduler();
	}
	
	public SpoutParallelTaskManager(Scheduler scheduler, SpoutWorld w) {
		if (w == null) {
			throw new IllegalArgumentException("World cannot be set to null");	
		}
		upTime = new AtomicLong(0);
		this.engine = null;
		this.world = new ArrayList<World>();
		this.world.add(w);
		this.scheduler = scheduler;
	}

	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task) {
		return scheduleSyncDelayedTask(plugin, task, TaskPriority.CRITICAL);
	}
	
	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, TaskPriority priority) {
		return scheduleSyncDelayedTask(plugin, task, 0, priority);
	}
	
	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority) {
		return scheduleSyncRepeatingTask(plugin, task, delay, -1, priority);
	}

	@Override
	public int scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period, TaskPriority priority) {
		return schedule(new SpoutTask(this, scheduler, plugin, task, true, delay, period, priority));
	}

	@Override
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority) {
		return scheduleAsyncRepeatingTask(plugin, task, delay, -1, priority);
	}

	@Override
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, TaskPriority priority) {
		return scheduleAsyncRepeatingTask(plugin, task, 0, -1, priority);
	}

	@Override
	public int scheduleAsyncRepeatingTask(Object plugin, Runnable task, long delay, long period, TaskPriority priority) {
		throw new UnsupportedOperationException("Async tasks can only be initiated by the task manager for the server");
	}
	
	@Override
	public <T> Future<T> callSyncMethod(Object plugin, Callable<T> task, TaskPriority priority) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public void heartbeat(long delta) {
		if (engine != null) {
			TickStage.checkStage(TickStage.TICKSTART);
		} else {
			TickStage.checkStage(TickStage.STAGE1);
		}
		long upTime = this.upTime.getAndAdd(delta);
		if (upTime - lastPrune > 1000) {
			lastPrune = upTime;
			int[] keys = activeTasks.keys();
			for (int key : keys) {
				ParallelTaskInfo info = activeTasks.get(key);
				if (info != null) {
					info.prune();
				}
			}
		}
		SpoutRegion region;
		SpoutTask task;
		while ((task = newTasks.poll()) != null) {
			int taskId = task.getTaskId();
			ParallelTaskInfo info = activeTasks.get(taskId);
			if (info == null) {
				info = new ParallelTaskInfo(task);
				ParallelTaskInfo previous = activeTasks.putIfAbsent(taskId, info);
				if (previous != null) {
					info = previous;
				}
			}
			Collection<World> worlds = (this.world == null) ? engine.getWorlds() : world;
			for (World w : worlds) {
				SpoutWorld sw = (SpoutWorld)w;
				for (Region r : sw.getRegions()) {
					info.add((SpoutRegion)r);
				}
			}
		}
		while ((region = newRegions.poll()) != null) {
			for (ParallelTaskInfo info : activeTasks.values(ParallelTaskInfo.EMPTY_ARRAY)) {
				info.add(region);
			}
		}
	}
	
	protected int schedule(SpoutTask task) {
		ParallelTaskInfo info = new ParallelTaskInfo(task);
		if (task.getPeriod() > 0) {
			activeTasks.put(task.getTaskId(), info);
		}
		newTasks.add(task);
		return task.getTaskId();
	}
	
	public void registerRegion(SpoutRegion r) {
		newRegions.add(r);
	}

	@Override
	public void cancelTask(int taskId) {
		ParallelTaskInfo info = activeTasks.remove(taskId);
		if (info != null) {
			info.stop();
		}
	}

	@Override
	public void cancelTasks(Object plugin) {
		int[] keys = activeTasks.keys();
		for (int key : keys) {
			ParallelTaskInfo info = activeTasks.get(key);
			if (info != null) {
				if (info.getTask().getOwner() == plugin) {
					info.stop();
					activeTasks.remove(key, info);
				}
			}
		}
	}
	
	@Override
	public void cancelAllTasks() {
		int[] keys = activeTasks.keys();
		for (int key : keys) {
			ParallelTaskInfo info = activeTasks.get(key);
			if (info != null) {
				info.stop();
				activeTasks.remove(key, info);
			}
		}
	}

	@Override
	public List<Worker> getActiveWorkers() {
		throw new UnsupportedOperationException("The parallel task manager does not spawn additional workers");
	}

	@Override
	public List<Task> getPendingTasks() {
		throw new UnsupportedOperationException("The operation is not supported");
	}
	
	@Override
	public long getUpTime() {
		return upTime.get();
	}
	
	private static class RegionIdPair {
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
	
	private static class MarkedWeakReference<T, M> extends WeakReference<T> {
		
		private final M mark;
		
		public MarkedWeakReference(T r, M mark, ReferenceQueue<T> q) {
			super(r, q);
			this.mark = mark;
		}
		
		public MarkedWeakReference(T r, M mark) {
			super(r);
			this.mark = mark;
		}
		
		public M getMark() {
			return mark;
		}
		
	}
	
	private static class ParallelTaskInfo {
		
		public static final ParallelTaskInfo[] EMPTY_ARRAY = new ParallelTaskInfo[0];
		
		private final Set<RegionIdPair> children = new HashSet<RegionIdPair>();
		
		private final Set<SpoutRegion> regions = new HashSet<SpoutRegion>();
		
		private final ReferenceQueue<SpoutRegion> refQueue = new ReferenceQueue<SpoutRegion>();
		
		private final SpoutTask task;
		
		private boolean alive = true;
		
		public ParallelTaskInfo(SpoutTask task) {
			this.task = task;
		}
		
		public synchronized boolean add(SpoutRegion region) {
			if (!regions.add(region)) {
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
				return true;
			}
		}
		
		@SuppressWarnings("unchecked")
		public synchronized void prune() {
			MarkedWeakReference<SpoutRegion, RegionIdPair> ref = null;
			while ((ref = (MarkedWeakReference<SpoutRegion, RegionIdPair>)refQueue.poll()) != null) {
				RegionIdPair p = ref.getMark();
				children.remove(p);
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
		
		// Doesn't need synchronized since it is a final variable
		public SpoutTask getTask() {
			return task;
		}
		
	}
	
}
