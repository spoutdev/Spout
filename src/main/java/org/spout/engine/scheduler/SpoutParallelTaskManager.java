/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.Engine;
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
import org.spout.engine.scheduler.parallel.ParallelTaskInfo;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

public class SpoutParallelTaskManager implements TaskManager {
	
	private final Engine engine;
	
	private final Collection<World> world;
	
	private final AtomicLong upTime;
	
	private final TSyncIntObjectMap<ParallelTaskInfo> activeTasks = new TSyncIntObjectHashMap<ParallelTaskInfo>();
	
	private final ConcurrentLinkedQueue<SpoutRegion> newRegions = new ConcurrentLinkedQueue<SpoutRegion>();

	private final ConcurrentLinkedQueue<SpoutRegion> deadRegions = new ConcurrentLinkedQueue<SpoutRegion>();
	
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
	public int scheduleAsyncTask(Object plugin, Runnable task) {
		return scheduleAsyncRepeatingTask(plugin, task, 0, -1, TaskPriority.CRITICAL);
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
				task.setParallelInfo(info);
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
		while ((region = deadRegions.poll()) != null) {
			while (newRegions.remove(region))
				;
			for (ParallelTaskInfo info : activeTasks.values(ParallelTaskInfo.EMPTY_ARRAY)) {
				while (info.remove(region))
					;
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
	
	public void unRegisterRegion(SpoutRegion r) {
		TickStage.checkStage(TickStage.TICKSTART);
		deadRegions.add(r);
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

	@Override
	public boolean isQueued(int taskId) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
