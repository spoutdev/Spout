package org.getspout.unchecked.server.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

/**
 * A class which schedules {@link SpoutTask}s.
 *
 * @author Graham Edgecombe
 */
public final class SpoutScheduler implements BukkitScheduler {
	/**
	 * The number of milliseconds between pulses.
	 */
	private static final int PULSE_EVERY = 50;

	/**
	 * The server this scheduler is managing for.
	 */
	private final SpoutServer server;

	/**
	 * The scheduled executor service which backs this scheduler.
	 */
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	/**
	 * A list of new tasks to be added.
	 */
	private final List<SpoutTask> newTasks = new ArrayList<SpoutTask>();

	/**
	 * A list of tasks to be removed.
	 */
	private final List<SpoutTask> oldTasks = new ArrayList<SpoutTask>();

	/**
	 * A list of active tasks.
	 */
	private final List<SpoutTask> tasks = new ArrayList<SpoutTask>();

	private final List<SpoutWorker> activeWorkers = Collections.synchronizedList(new ArrayList<SpoutWorker>());

	/**
	 * Creates a new task scheduler.
	 */
	public SpoutScheduler(SpoutServer server) {
		this.server = server;

		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					pulse();
				} catch (Exception ex) {
					SpoutServer.logger.log(Level.SEVERE, "Error while pulsing: {0}", ex.getMessage());
					ex.printStackTrace();
				}
			}
		}, 0, PULSE_EVERY, TimeUnit.MILLISECONDS);
	}

	/**
	 * Stops the scheduler and all tasks.
	 */
	public void stop() {
		cancelAllTasks();
		executor.shutdown();
	}

	/**
	 * Schedules the specified task.
	 *
	 * @param task The task.
	 */
	private int schedule(SpoutTask task) {
		synchronized (newTasks) {
			newTasks.add(task);
		}
		return task.getTaskId();
	}

	/**
	 * Adds new tasks and updates existing tasks, removing them if necessary.
	 */
	private void pulse() {
		// Perform basic world pulse.
		server.getSessionRegistry().pulse();
		for (World world : server.getWorlds()) {
			((SpoutWorld) world).pulse();
		}

		// Bring in new tasks this tick.
		synchronized (newTasks) {
			for (SpoutTask task : newTasks) {
				tasks.add(task);
			}
			newTasks.clear();
		}

		// Remove old tasks this tick.
		synchronized (oldTasks) {
			for (SpoutTask task : oldTasks) {
				tasks.remove(task);
			}
			oldTasks.clear();
		}

		// Run the relevant tasks.
		for (Iterator<SpoutTask> it = tasks.iterator(); it.hasNext();) {
			SpoutTask task = it.next();
			boolean cont = false;
			try {
				if (task.isSync()) {
					cont = task.pulse();
				} else {
					activeWorkers.add(new SpoutWorker(task, this));
				}
			} finally {
				if (!cont) {
					it.remove();
				}
			}
		}
	}

	@Override
	public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
		return scheduleSyncRepeatingTask(plugin, task, delay, -1);
	}

	@Override
	public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
		return scheduleSyncDelayedTask(plugin, task, 0);
	}

	@Override
	public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
		return schedule(new SpoutTask(plugin, task, true, delay, period));
	}

	@Override
	public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay) {
		return scheduleAsyncRepeatingTask(plugin, task, delay, -1);
	}

	@Override
	public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task) {
		return scheduleAsyncRepeatingTask(plugin, task, 0, -1);
	}

	@Override
	public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period) {
		return schedule(new SpoutTask(plugin, task, false, delay, period));
	}

	@Override
	public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void cancelTask(int taskId) {
		synchronized (oldTasks) {
			for (SpoutTask task : tasks) {
				if (task.getTaskId() == taskId) {
					oldTasks.add(task);
					return;
				}
			}
		}
	}

	@Override
	public void cancelTasks(Plugin plugin) {
		synchronized (oldTasks) {
			for (SpoutTask task : tasks) {
				if (task.getOwner() == plugin) {
					oldTasks.add(task);
				}
			}
		}
	}

	@Override
	public void cancelAllTasks() {
		synchronized (oldTasks) {
			for (SpoutTask spoutTask : tasks) {
				oldTasks.add(spoutTask);
			}
		}
	}

	@Override
	public boolean isCurrentlyRunning(int taskId) {
		for (SpoutWorker worker : activeWorkers) {
			if (worker.getTaskId() == taskId && worker.getThread().isAlive()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isQueued(int taskId) {
		synchronized (tasks) {
			for (SpoutTask task : tasks) {
				if (task.getTaskId() == taskId) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<BukkitWorker> getActiveWorkers() {
		return new ArrayList<BukkitWorker>(activeWorkers);
	}

	@Override
	public List<BukkitTask> getPendingTasks() {
		ArrayList<BukkitTask> result = new ArrayList<BukkitTask>();
		for (SpoutTask spoutTask : tasks) {
			result.add(spoutTask);
		}
		return result;
	}

	synchronized void workerComplete(SpoutWorker worker) {
		activeWorkers.remove(worker);
		if (!worker.shouldContinue()) {
			oldTasks.add(worker.getTask());
		}
	}
}
