/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.lwjgl.opengl.Display;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.SnapshotLock;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TickStage;
import org.spout.api.scheduler.Worker;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutServer;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncExecutorUtils;
import org.spout.engine.util.thread.ThreadsafetyManager;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableArrayList;

/**
 * A class which handles scheduling for the server {@link SpoutTask}s.<br>
 * <br>
 * Tasks can be submitted to the scheduler for execution by the main thread.
 * These tasks are executed during a period where none of the auxiliary threads
 * are executing.<br>
 * <br>
 * Each tick consists of a number of stages. Each stage is executed in parallel,
 * but the next stage is not started until all threads have completed the
 * previous stage.<br>
 * <br>
 * Except for executing queued serial tasks, all threads are run in parallel.
 * The full sequence is as follows:<br>
 * <ul>
 * <li>Single Thread
 * <ul>
 * <li><b>Execute queued tasks</b><br>
 * Tasks that are submitted for execution are executed one at a time.
 * </ul>
 * <li>Parallel Threads
 * <ul>
 * <li><b>Stage 1</b><br>
 * This is the first stage of execution. Most Events are generated during this
 * stage and the API is fully open for use. - chunks are
 * populated.
 * <li><b>Stage 2</b><br>
 * During this stage, entity collisions are handled.
 * <li><b>Finalize Tick</b><br>
 * During this stage - entities are moved between entity managers.
 *  - chunks are compressed if necessary.
 * <li><b>Pre-snapshot</b><br>
 * This is a MONITOR stage, data is stable and no modifications are allowed.
 * <li><b>Copy Snapshot</b><br>
 * During this stage all live values are copied to their stable snapshot. Data
 * is unstable so no reads are permitted during this stage.
 * </ul>
 * </ul>
 *
 */
public final class SpoutScheduler implements Scheduler {
	/**
	 * The number of milliseconds between pulses.
	 */
	private static final int PULSE_EVERY = 50;

	/**
	 * Target Frames per Second for the renderer
	 */
	private static final int TARGET_FPS = 60;

	/**
	 * The server this scheduler is managing for.
	 */
	private final Engine server;

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
	 * A snapshot manager for local snapshot variables
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * A list of all AsyncManagers
	 */
	private final SnapshotableArrayList<AsyncExecutor> asyncExecutors = new SnapshotableArrayList<AsyncExecutor>(snapshotManager, null);

	private final AtomicLong tickStartTime = new AtomicLong();

	private volatile boolean shutdown = false;

	private final SpoutSnapshotLock snapshotLock = new SpoutSnapshotLock();

	private final Thread mainThread;

	private Thread renderThread;

	/**
	 * Creates a new task scheduler.
	 */
	public SpoutScheduler(Engine server) {

		this.server = server;

		mainThread = new MainThread();
		renderThread  = new RenderThread();
	}


	private class RenderThread extends Thread {

		public RenderThread(){
			super("Render Thread");
		}

		@Override
		public void run(){
			SpoutClient c = (SpoutClient)Spout.getEngine();
			c.initRenderer();
			int rate = (int) ((1f / TARGET_FPS) * 1000);
			long lastTick = System.currentTimeMillis();
			while (!shutdown) {
				long startTime = System.currentTimeMillis();
				long delta = startTime - lastTick;
				c.render(delta / 1000f);

				Display.update();
				lastTick = System.currentTimeMillis();
				if (rate - delta > 0) {
					try {
						Thread.sleep(rate - delta);
					} catch (InterruptedException e) {
						Spout.log("[Severe] Interrupted while sleeping!");
					}
				}

			}



		}

	}


	private class MainThread extends Thread {

		public MainThread() {
			super("MainThread");
			ThreadsafetyManager.setMainThread(this);
		}

		@Override
		public void run() {
			long targetPeriod = PULSE_EVERY;
			long lastTick = System.currentTimeMillis();

			while (!shutdown) {
				long startTime = System.currentTimeMillis();
				tickStartTime.set(startTime);
				long delta = startTime - lastTick;
				try {
					if (!tick(delta)) {
						throw new IllegalStateException("Attempt made to start a tick before the previous one ended");
					}
					lastTick = startTime;
				} catch (Exception ex) {
					Spout.getLogger().log(Level.SEVERE, "Error while pulsing: {0}", ex.getMessage());
					ex.printStackTrace();
				}
				long finishTime = System.currentTimeMillis();
				long freeTime = targetPeriod - (finishTime - startTime);

				if (freeTime > 0) {
					try {
						Thread.sleep(freeTime);
					} catch (InterruptedException e) {
						shutdown = true;
					}
				}
			}

			asyncExecutors.copySnapshot();
			TickStage.setStage(TickStage.TICKSTART);

			// Halt all executors, except the Server
			for (AsyncExecutor e : asyncExecutors.get()) {
				if (!(e.getManager() instanceof SpoutServer)) {
					if (!e.haltExecutor()) {
						throw new IllegalStateException("Unable to halt executor for " + e.getManager());
					}
				}
			}

			try {
				copySnapshot(asyncExecutors.get());
			} catch (InterruptedException ex) {
				Spout.getLogger().log(Level.SEVERE, "Error while halting all executors: {0}", ex.getMessage());
			}

			asyncExecutors.copySnapshot();

			// Halt the executor for the Server
			for (AsyncExecutor e : asyncExecutors.get()) {
				if (!(e.getManager() instanceof SpoutServer)) {
					throw new IllegalStateException("Only the server should be left to shutdown");
				} else {
					if (!e.haltExecutor()) {
						throw new IllegalStateException("Unable to halt SpoutServer executor");
					}
				}
			}

			try {
				copySnapshot(asyncExecutors.get());
			} catch (InterruptedException ex) {
				SpoutServer.logger.log(Level.SEVERE, "Error while shutting down server: {0}", ex.getMessage());
			}
		}
	}

	public void startMainThread() {
		if (mainThread.isAlive()) {
			throw new IllegalStateException("Attempt was made to start the main thread twice");
		} else {
			mainThread.start();
		}
	}


	public void startRenderThread() {
		if(!(Spout.getEngine() instanceof SpoutClient)) throw new IllegalStateException("Cannot start the rendering thread unless on the client");
		if(renderThread.isAlive()){
			throw new IllegalStateException("Attempt was made to start the render thread twice");
		}
		renderThread.start();

	}

	/**
	 * Adds an async manager to the scheduler
	 */
	@DelayedWrite
	public void addAsyncExecutor(AsyncExecutor manager) {
		asyncExecutors.add(manager);
	}

	/**
	 * Removes an async manager from the scheduler
	 */
	@DelayedWrite
	public void removeAsyncExecutor(AsyncExecutor manager) {
		asyncExecutors.remove(manager);
	}

	/**
	 * Stops the scheduler and all tasks.
	 */
	public void stop() {
		cancelAllTasks();
		shutdown = true;
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
	private boolean tick(long delta) throws InterruptedException {
		TickStage.setStage(TickStage.TICKSTART);
		asyncExecutors.copySnapshot();

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

		List<AsyncExecutor> executors = asyncExecutors.get();

		int stage = 0;
		boolean allStagesComplete = false;
		boolean joined = false;

		TickStage.setStage(TickStage.STAGE1);

		while (!allStagesComplete) {

			if (stage == 0) {
				TickStage.setStage(TickStage.STAGE1);
			} else {
				TickStage.setStage(TickStage.STAGE2P);
			}

			allStagesComplete = true;

			for (AsyncExecutor e : executors) {
				if (stage < e.getManager().getStages()) {
					allStagesComplete = false;
					if (!e.startTick(stage, delta)) {
						return false;
					}
				} else {
					continue;
				}
			}

			joined = false;
			while (!joined) {
				try {
					AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
					joined = true;
				} catch (TimeoutException e) {
					server.getLogger().info("Tick had not completed after " + (PULSE_EVERY << 4) + "ms");
					AsyncExecutorUtils.dumpAllStacks();
					AsyncExecutorUtils.checkForDeadlocks();
				}
			}
			stage++;
		}

		copySnapshot(executors);
		return true;
	}

	private void copySnapshot(List<AsyncExecutor> executors) throws InterruptedException {
		TickStage.setStage(TickStage.FINALIZE);

		for (AsyncExecutor e : executors) {
			if (!e.finalizeTick()) {
				throw new IllegalStateException("Attempt made to finalize a tick before snapshot copy while the previous operation was still active");
			}
		}

		boolean joined = false;
		while (!joined) {
			try {
				AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
				joined = true;
			} catch (TimeoutException e) {
				server.getLogger().info("Tick had not completed after " + (PULSE_EVERY << 4) + "ms");
				AsyncExecutorUtils.dumpAllStacks();
				AsyncExecutorUtils.checkForDeadlocks();
			}
		}

		lockSnapshotLock();
		TickStage.setStage(TickStage.PRESNAPSHOT);

		try {
			for (AsyncExecutor e : executors) {
				if (!e.preSnapshot()) {
					throw new IllegalStateException("Attempt made to enter the pre-snapshot stage for a tick while the previous operation was still active");
				}
			}

			joined = false;
			while (!joined) {
				try {
					AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
					joined = true;
				} catch (TimeoutException e) {
					server.getLogger().info("Tick had not completed after " + (PULSE_EVERY << 4) + "ms");
					AsyncExecutorUtils.dumpAllStacks();
				}
			}

			TickStage.setStage(TickStage.SNAPSHOT);

			for (AsyncExecutor e : executors) {
				if (!e.copySnapshot()) {
					throw new IllegalStateException("Attempt made to copy the snapshot for a tick while the previous operation was still active");
				}
			}

			joined = false;
			while (!joined) {
				try {
					AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
					joined = true;
				} catch (TimeoutException e) {
					server.getLogger().info("Tick had not completed after " + (PULSE_EVERY << 4) + "ms");
					AsyncExecutorUtils.dumpAllStacks();
				}
			}
		} finally {
			unlockSnapshotLock();
		}
	}

	private void lockSnapshotLock() {

		int delay = 500;
		int threshold = 50;

		long startTime = System.currentTimeMillis();

		boolean success = false;

		while (!success) {
			success = snapshotLock.writeLock(delay);
			if (!success) {
				delay *= 1.5;
				List<Plugin> violatingPlugins = snapshotLock.getLockingPlugins(threshold);
				server.getLogger().info("Unable to lock snapshot after " + (System.currentTimeMillis() - startTime) + "ms");
				for (Plugin p : violatingPlugins) {
					server.getLogFile().indexOf(p.getDescription().getName() + " has locked the snapshot lock for more than " + threshold + "ms");
				}
			}
		}
	}

	private void unlockSnapshotLock() {
		snapshotLock.writeUnlock();
	}

	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, long delay) {
		return scheduleSyncRepeatingTask(plugin, task, delay, -1);
	}

	@Override
	public int scheduleSyncDelayedTask(Object plugin, Runnable task) {
		return scheduleSyncDelayedTask(plugin, task, 0);
	}

	@Override
	public int scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period) {
		return schedule(new SpoutTask(plugin, task, true, delay, period));
	}

	@Override
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay) {
		return scheduleAsyncRepeatingTaskInternal(plugin, task, delay, -1);
	}

	@Override
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task) {
		return scheduleAsyncRepeatingTaskInternal(plugin, task, 0, -1);
	}

	@Override
	public int scheduleAsyncRepeatingTask(Object plugin, Runnable task, long delay, long period) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	private int scheduleAsyncRepeatingTaskInternal(Object plugin, Runnable task, long delay, long period) {
		return schedule(new SpoutTask(plugin, task, false, delay, period));
	}

	@Override
	public <T> Future<T> callSyncMethod(Object plugin, Callable<T> task) {
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
	public void cancelTasks(Object plugin) {
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
	public List<Worker> getActiveWorkers() {
		return new ArrayList<Worker>(activeWorkers);
	}

	@Override
	public List<Task> getPendingTasks() {
		ArrayList<Task> result = new ArrayList<Task>();
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

	@Override
	public SnapshotLock getSnapshotLock() {
		return snapshotLock;
	}

	public final Thread getMainThread() {
		return mainThread;
	}


	@Override
	public long getTickTime() {
		return System.currentTimeMillis() - tickStartTime.get();
	}

	@Override
	public long getRemainingTickTime() {
		return PULSE_EVERY - getTickTime();
	}
}
