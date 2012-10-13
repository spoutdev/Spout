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
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.scheduler.Worker;

public class SpoutTaskManager implements TaskManager {
	
	private final ConcurrentHashMap<SpoutTask, SpoutWorker> activeWorkers = new ConcurrentHashMap<SpoutTask, SpoutWorker>();
	
	private final ConcurrentHashMap<Integer, SpoutTask> activeTasks = new ConcurrentHashMap<Integer, SpoutTask>();

	private final TaskPriorityQueue taskQueue;
	
	private final boolean mainThread;
	
	private final AtomicBoolean alive;
	
	private final AtomicLong upTime;
	
	private final Object scheduleLock = new Object();
	
	private final Scheduler scheduler;
	
	public SpoutTaskManager(Scheduler scheduler, boolean mainThread) {
		this(scheduler, mainThread, Thread.currentThread());
	}
	
	public SpoutTaskManager(Scheduler scheduler, boolean mainThread, Thread t) {
		this(scheduler, mainThread, t, 0L);
	}
	
	public SpoutTaskManager(Scheduler scheduler, boolean mainThread, Thread t, long age) {
		this.taskQueue = new TaskPriorityQueue(t, SpoutScheduler.PULSE_EVERY / 4);
		this.mainThread = mainThread;
		this.alive = new AtomicBoolean(true);
		this.upTime = new AtomicLong(age);
		this.scheduler = scheduler;
	}

	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task) {
		return scheduleSyncDelayedTask(plugin, task, 0, TaskPriority.CRITICAL);
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
		if (!alive.get()) {
			return -1;
		} else if (!mainThread) {
			throw new UnsupportedOperationException("Async tasks can only be initiated by the task manager for the server");
		} else {
			return schedule(new SpoutTask(this, scheduler, plugin, task, false, delay, period, priority));
		}
	}
	
	@Override
	public <T> Future<T> callSyncMethod(Object plugin, Callable<T> task, TaskPriority priority) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public void heartbeat(long delta) {
		long upTime = this.upTime.addAndGet(delta);
		
		Queue<SpoutTask> q;
		
		while ((q = taskQueue.poll(upTime)) != null) {
			boolean checkRequired = !taskQueue.isFullyBelowThreshold(q, upTime);
			Iterator<SpoutTask> itr = q.iterator();
			while (itr.hasNext()) {
				SpoutTask currentTask = itr.next();
				if (checkRequired && currentTask.getPriority() > upTime) {
					continue;
				}
				
				itr.remove();
				currentTask.setUnqueued();

				if (!currentTask.isAlive()) {
					continue;
				} else if (currentTask.isSync()) {
					currentTask.pulse();
					repeatSchedule(currentTask);
				} else {
					SpoutWorker worker = new SpoutWorker(currentTask, this);
					addWorker(worker, currentTask);
					worker.start();
				}
			}
			if (taskQueue.complete(q, upTime)) {
				break;
			}
		}
	}

	public void cancelTask(SpoutTask task) {
		synchronized (scheduleLock) {
			task.stop();
			if (taskQueue.remove(task)) {
				removeTask(task);
			}
		}
		if (!task.isSync()) {
			SpoutWorker worker = activeWorkers.get(task);
			if (worker != null) {
				worker.getThread().interrupt();
			}
		}
	}
	
	public int schedule(SpoutTask task) {
		synchronized (scheduleLock) {
			addTask(task);
			if (task.getDelay() == 0 && task.getPeriod() < 0) {
				SpoutWorker worker = new SpoutWorker(task, this);
				addWorker(worker, task);
				worker.start();
			} else {
				taskQueue.add(task);
			}
			return task.getTaskId();
		}
	}
	
	protected int repeatSchedule(SpoutTask task) {
		synchronized (scheduleLock) {
			if (task.isAlive()) {
				schedule(task);
			} else {
				removeTask(task);
			}
		}
		return task.getTaskId();
	}
	
	public void addWorker(SpoutWorker worker, SpoutTask task) {
		activeWorkers.put(task, worker);
	}
	
	public boolean removeWorker(SpoutWorker worker, SpoutTask task) {
		return activeWorkers.remove(task, worker);
	}
	
	public void addTask(SpoutTask task) {
		activeTasks.put(task.getTaskId(), task);
	}
	
	public boolean removeTask(SpoutTask task) {
		return activeTasks.remove(task.getTaskId(), task);
	}

	public boolean isQueued(int taskId) {
		return activeTasks.containsKey(taskId);
	}

	@Override
	public void cancelTask(int taskId) {
		cancelTask(activeTasks.get(taskId));
	}

	@Override
	public void cancelTasks(Object plugin) {
		ArrayList<SpoutTask> tasks = new ArrayList<SpoutTask>(activeTasks.values());
		for (SpoutTask task : tasks) {
			if (task.getOwner() == plugin) {
				cancelTask(task);
			}
		}
	}
	
	@Override
	public void cancelAllTasks() {
		ArrayList<SpoutTask> tasks = new ArrayList<SpoutTask>(activeTasks.values());
		for (SpoutTask task : tasks) {
			cancelTask(task);
		}
	}

	@Override
	public List<Worker> getActiveWorkers() {
		return new ArrayList<Worker>(activeWorkers.values());
	}

	@Override
	public List<Task> getPendingTasks() {
		List<SpoutTask> tasks = taskQueue.getTasks();
		List<Task> list = new ArrayList<Task>(tasks.size());
		for (SpoutTask t : tasks) {
			list.add(t);
		}
		return list;
	}
	
	public boolean shutdown() {
		return shutdown(1);
	}
	
	public boolean shutdown(long timeout) {
		if (!mainThread) {
			throw new IllegalStateException("Only the task manager for the main thread should be shutdown, since the other task managers do not support async tasks");
		}
		alive.set(false);
		cancelAllTasks();
		return true;
	}
	
	@Override
	public long getUpTime() {
		return upTime.get();
	}
	
}
