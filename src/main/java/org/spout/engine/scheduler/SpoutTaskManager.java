package org.spout.engine.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.Worker;

public class SpoutTaskManager implements TaskManager {
	
	private final ConcurrentHashMap<SpoutTask, SpoutWorker> activeWorkers = new ConcurrentHashMap<SpoutTask, SpoutWorker>();
	
	private final ConcurrentHashMap<Integer, SpoutTask> activeTasks = new ConcurrentHashMap<Integer, SpoutTask>();

	private final TaskPriorityQueue taskQueue;
	
	private final boolean mainThread;
	
	private final AtomicBoolean alive;
	
	private AtomicLong upTime = new AtomicLong(0);
	
	private final Object scheduleLock = new Object();
	
	public SpoutTaskManager(boolean mainThread) {
		this(mainThread, Thread.currentThread());
	}
	
	public SpoutTaskManager(boolean mainThread, Thread t) {
		this.taskQueue = new TaskPriorityQueue(t);
		this.mainThread = mainThread;
		this.alive = new AtomicBoolean(true);
	}

	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task) {
		return scheduleSyncDelayedTask(plugin, task, 0);
	}
	
	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, long delay) {
		return scheduleSyncRepeatingTask(plugin, task, delay, -1);
	}

	@Override
	public int scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period) {
		return schedule(new SpoutTask(this, plugin, task, true, delay, period));
	}

	@Override
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay) {
		return scheduleAsyncRepeatingTask(plugin, task, delay, -1);
	}

	@Override
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task) {
		return scheduleAsyncRepeatingTask(plugin, task, 0, -1);
	}

	@Override
	public int scheduleAsyncRepeatingTask(Object plugin, Runnable task, long delay, long period) {
		if (!alive.get()) {
			return -1;
		} else if (!mainThread) {
			throw new UnsupportedOperationException("Async tasks can only be initiated by the task manager for the server");
		} else {
			return schedule(new SpoutTask(this, plugin, task, false, delay, period));
		}
	}
	
	@Override
	public <T> Future<T> callSyncMethod(Object plugin, Callable<T> task) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public void heartbeat(long delta) {
		long upTime = this.upTime.getAndAdd(delta);
		while ((taskQueue.hasPendingTasks(upTime))) {
			SpoutTask currentTask = taskQueue.getPendingTask(upTime);
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
	
	protected int schedule(SpoutTask task) {
		synchronized (scheduleLock) {
			addTask(task);
			taskQueue.add(task);
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
		return new ArrayList<Task>(taskQueue);
	}
	
	public void shutdown() {
		alive.set(false);
	}
	
	public long getUpTime() {
		return upTime.get();
	}
	
}
