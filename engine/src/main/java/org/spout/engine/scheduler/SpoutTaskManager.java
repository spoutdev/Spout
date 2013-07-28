/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.Spout;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.scheduler.Worker;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;

public class SpoutTaskManager implements TaskManager {
	private final ConcurrentHashMap<SpoutTask, SpoutWorker> activeWorkers = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Integer, SpoutTask> activeTasks = new ConcurrentHashMap<>();
	private final TaskPriorityQueue taskQueue;
	private final boolean mainThread;
	private final AtomicBoolean alive;
	private final AtomicLong upTime;
	private final Object scheduleLock = new Object();
	private final Scheduler scheduler;
	private final ExecutorService pool = Executors.newFixedThreadPool(20, new NamedThreadFactory("Scheduler Thread Pool Thread"));

	public SpoutTaskManager(Scheduler scheduler, Thread mainThread) {
		this(scheduler, mainThread, null, 0L);
	}

	public SpoutTaskManager(Scheduler scheduler, AsyncManager manager) {
		this(scheduler, null, manager, 0L);
	}

	public SpoutTaskManager(Scheduler scheduler, Thread mainThread, AsyncManager manager, long age) {
		this.taskQueue = new TaskPriorityQueue(manager, SpoutScheduler.PULSE_EVERY / 4);
		this.mainThread = mainThread != null;
		this.alive = new AtomicBoolean(true);
		this.upTime = new AtomicLong(age);
		this.scheduler = scheduler;
	}

	@Override
	public Task scheduleSyncDelayedTask(Object plugin, Runnable task) {
		return scheduleSyncDelayedTask(plugin, task, 0, TaskPriority.CRITICAL);
	}

	@Override
	public Task scheduleSyncDelayedTask(Object plugin, Runnable task, TaskPriority priority) {
		return scheduleSyncDelayedTask(plugin, task, 0, priority);
	}

	@Override
	public Task scheduleSyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority) {
		return scheduleSyncRepeatingTask(plugin, task, delay, -1, priority);
	}

	@Override
	public Task scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period, TaskPriority priority) {
		return schedule(new SpoutTask(this, scheduler, plugin, task, true, delay, period, priority, false));
	}

	@Override
	public Task scheduleAsyncTask(Object plugin, Runnable task) {
		return scheduleAsyncTask(plugin, task, false);
	}

	@Override
	public Task scheduleAsyncTask(Object plugin, Runnable task, boolean longLife) {
		return scheduleAsyncDelayedTask(plugin, task, 0, TaskPriority.CRITICAL, longLife);
	}

	@Override
	public Task scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority) {
		return scheduleAsyncDelayedTask(plugin, task, delay, priority, true);
	}

	@Override
	public Task scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority, boolean longLife) {
		if (!alive.get()) {
			return null;
		} else if (!mainThread) {
			throw new UnsupportedOperationException("Async tasks can only be initiated by the task manager for the server");
		} else {
			return schedule(new SpoutTask(this, scheduler, plugin, task, false, delay, -1, priority, longLife));
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
					Spout.getLogger().info("Async repeating task submitted");
				}
			}
			if (taskQueue.complete(q, upTime)) {
				break;
			}
		}
	}

	public void cancelTask(SpoutTask task) {
		if (task == null) {
			throw new IllegalArgumentException("Task cannot be null!");
		}
		synchronized (scheduleLock) {
			task.stop();
			if (taskQueue.remove(task)) {
				removeTask(task);
			}
		}
		if (!task.isSync()) {
			SpoutWorker worker = activeWorkers.get(task);
			if (worker != null) {
				worker.interrupt();
			}
		}
	}

	public Task schedule(SpoutTask task) {
		synchronized (scheduleLock) {
			if (!addTask(task)) {
				return task;
			}
			if (!task.isSync()) {
				SpoutWorker worker = new SpoutWorker(task, this);
				addWorker(worker, task);
				worker.start(pool);
			} else {
				taskQueue.add(task);
			}
			return task;
		}
	}

	protected Task repeatSchedule(SpoutTask task) {
		synchronized (scheduleLock) {
			if (task.isAlive()) {
				schedule(task);
			} else {
				removeTask(task);
			}
		}
		return task;
	}

	public void addWorker(SpoutWorker worker, SpoutTask task) {
		activeWorkers.put(task, worker);
	}

	public boolean removeWorker(SpoutWorker worker, SpoutTask task) {
		return activeWorkers.remove(task, worker);
	}

	public boolean addTask(SpoutTask task) {
		activeTasks.put(task.getTaskId(), task);
		if (!alive.get()) {
			cancelTask(task);
			return false;
		}
		return true;
	}

	public boolean removeTask(SpoutTask task) {
		return activeTasks.remove(task.getTaskId(), task);
	}

	@Override
	public boolean isQueued(int taskId) {
		return activeTasks.containsKey(taskId);
	}

	@Override
	public void cancelTask(int taskId) {
		cancelTask(activeTasks.get(taskId));
	}

	@Override
	public void cancelTask(Task task) {
		if (task == null) {
			throw new IllegalArgumentException("Task cannot be null!");
		}
		cancelTask(activeTasks.get(task.getTaskId()));
	}

	@Override
	public void cancelTasks(Object plugin) {
		ArrayList<SpoutTask> tasks = new ArrayList<>(activeTasks.values());
		for (SpoutTask task : tasks) {
			if (task.getOwner() == plugin) {
				cancelTask(task);
			}
		}
	}

	@Override
	public void cancelAllTasks() {
		ArrayList<SpoutTask> tasks = new ArrayList<>(activeTasks.values());
		for (SpoutTask task : tasks) {
			cancelTask(task);
		}
	}

	@Override
	public List<Worker> getActiveWorkers() {
		return new ArrayList<Worker>(activeWorkers.values());
	}

	public boolean waitForAsyncTasks(long timeout) {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + timeout) {
			try {
				if (activeWorkers.isEmpty()) {
					return true;
				}
				Thread.sleep(10);
			} catch (InterruptedException ie) {
				return false;
			}
		}
		return false;
	}

	@Override
	public List<Task> getPendingTasks() {
		List<SpoutTask> tasks = taskQueue.getTasks();
		List<Task> list = new ArrayList<>(tasks.size());
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
		pool.shutdown();
		cancelAllTasks();
		return true;
	}

	@Override
	public long getUpTime() {
		return upTime.get();
	}
}
