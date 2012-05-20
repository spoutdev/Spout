package org.spout.engine.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;
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
		this.taskQueue = new TaskPriorityQueue(t);
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
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, TaskPriority priority) {
		return scheduleAsyncRepeatingTask(plugin, task, 0, -1, priority);
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
	
	public boolean shutdown() {
		return shutdown(1);
	}
	
	public boolean shutdown(long timeout) {
		if (!mainThread) {
			throw new IllegalStateException("Only the task manager for the main thread should be shutdown, since the other task managers do not support async tasks");
		}
		alive.set(false);
		cancelAllTasks();
		long endTime = System.currentTimeMillis() + timeout;
		boolean success = false;
		while (!success && System.currentTimeMillis() < endTime) {
			cancelAllTasks();
			if (activeWorkers.isEmpty()) {
				success = true;
				continue;
			} else {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		}
		if (!success) {
			Logger logger = Spout.getEngine().getLogger();
			logger.info("Forcing shutdown of tasks that did not properly shut down after " + timeout + "ms");
			logger.info("Task id) Owner");
			for (Worker worker : getActiveWorkers()) {
				Task task = worker.getTask();
				Thread thread = worker.getThread();
				if (thread.isAlive()) {
					Object owner = task.getOwner();
					if (owner instanceof Plugin) {
						Plugin plugin = (Plugin)owner;
						logger.info("Task " + task.getTaskId() + ") " + plugin.getName());
					} else if (owner != null) {
						logger.info("Task " + task.getTaskId() + ") " + owner + " of type " + owner.getClass().getCanonicalName());
					} else {
						logger.info("Task " + task.getTaskId() + ") Owner is null");
					}
					thread.stop();
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
		}
		return success;
	}
	
	@Override
	public long getUpTime() {
		return upTime.get();
	}
	
}
