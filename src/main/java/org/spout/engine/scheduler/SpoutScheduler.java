/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.gui.ScreenStack;
import org.spout.api.math.Vector2;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.SnapshotLock;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.scheduler.TickStage;
import org.spout.api.scheduler.Worker;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutEngine;
import org.spout.engine.SpoutRenderer;
import org.spout.engine.SpoutServer;
import org.spout.engine.protocol.NetworkSendThreadPool;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncExecutorUtils;
import org.spout.engine.util.thread.ThreadsafetyManager;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableArrayList;

/**
 * A class which handles scheduling for the engine {@link SpoutTask}s.<br>
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
 * - chunks are compressed if necessary.
 * <li><b>Pre-snapshot</b><br>
 * This is a MONITOR stage, data is stable and no modifications are allowed.
 * <li><b>Copy Snapshot</b><br>
 * During this stage all live values are copied to their stable snapshot. Data
 * is unstable so no reads are permitted during this stage.
 * </ul>
 * </ul>
 */
public final class SpoutScheduler implements Scheduler {
	/**
	 * The threshold before physics and dynamic updates are aborted
	 */
	private final static int UPDATE_THRESHOLD = 100000;
	/**
	 * The number of milliseconds between pulses.
	 */
	public static final int PULSE_EVERY = 50;
	/**
	 * A time that is at least 1 Pulse below the maximum time instant
	 */
	public static final long END_OF_THE_WORLD = Long.MAX_VALUE - PULSE_EVERY;
	/**
	 * Target Frames per Second for the renderer
	 */
	private static final int TARGET_FPS = 80;
	/**
	 * Used to detect if the render is under heavy load
	 */
	private static final int OVERHEAD_FPS = TARGET_FPS / 2;
	/**
	 * The engine this scheduler is managing for.
	 */
	private final Engine engine;
	/**
	 * A snapshot manager for local snapshot variables
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();
	/**
	 * A list of all AsyncManagers
	 */
	private final SnapshotableArrayList<AsyncExecutor> asyncExecutors = new SnapshotableArrayList<AsyncExecutor>(snapshotManager, null);
	/**
	 * Update count for physics and dynamic updates
	 */
	private final AtomicInteger updates = new AtomicInteger(0);

	private final AtomicLong tickStartTime = new AtomicLong();
	private volatile boolean shutdown = false;
	private final SpoutSnapshotLock snapshotLock = new SpoutSnapshotLock();
	private final Thread mainThread;
	private final RenderThread renderThread;
	private final Thread guiThread;
	private final SpoutTaskManager taskManager;
	private SpoutParallelTaskManager parallelTaskManager = null;
	private final AtomicBoolean heavyLoad = new AtomicBoolean(false);
	private final ConcurrentLinkedQueue<Runnable> coreTaskQueue = new ConcurrentLinkedQueue<Runnable>();
	private final LinkedBlockingDeque<Runnable> finalTaskQueue = new LinkedBlockingDeque<Runnable>();
	private final ConcurrentLinkedQueue<Runnable> lastTickTaskQueue = new ConcurrentLinkedQueue<Runnable>();

	public long getFps(){
		return renderThread.getFps();
	}

	public boolean isRendererOverloaded(){
		return renderThread != null && (renderThread.getFrameOverhead() || renderThread.getFps() < OVERHEAD_FPS);
	}

	/**
	 * Creates a new task scheduler.
	 */
	public SpoutScheduler(Engine engine) {

		this.engine = engine;

		mainThread = new MainThread();
		renderThread = new RenderThread();
		guiThread = new GUIThread();

		taskManager = new SpoutTaskManager(this, true, mainThread);
	}
	
	

	private class RenderThread extends Thread {
		private int fps = 0;
		boolean tooLongFrame = false;
		SpoutRenderer renderer;
		private ConcurrentLinkedQueue<Runnable> renderTaskQueue = new ConcurrentLinkedQueue<Runnable>();
		

		public RenderThread() {
			super("Render Thread");
		}
		public void setRenderer(SpoutRenderer renderer) {			
			this.renderer = renderer;
		}
		public void enqueueRenderTask(Runnable task){
			renderTaskQueue.add(task);
		}

		public int getFps() {
			return fps;
		}

		public boolean getFrameOverhead() {
			return tooLongFrame;
		}

		@Override
		public void run() {
			SpoutClient c = (SpoutClient) Spout.getEngine();
			renderer.initRenderer();
			int frames = 0;
			long lastFrameTime = System.currentTimeMillis();
			int targetFrame = (int)(1f / TARGET_FPS * 1000);
			int rate = (int) ((1f / TARGET_FPS) * 1000000000);

			long lastTick = System.nanoTime();

			long timeError = 0;
			long maxError = rate >> 2; // time error total limited to 0.25 seconds 

		while (!shutdown) {
			if (Display.isCloseRequested() || !c.isRendering()) {
				c.stop();
				break;
			}
			long currentTime = System.nanoTime();
			long delta = currentTime - lastTick;
			lastTick = currentTime;

			// Calculate error in frame time
			timeError += delta - rate;
			if (timeError > maxError) {
				timeError = maxError;
			} else if (timeError < -maxError) {
				timeError = -maxError;
			}
			
			while (renderTaskQueue.peek() != null) {
				Runnable task = renderTaskQueue.poll();
				task.run();
			}

			renderer.render(delta / 1000000000f);

			Display.update(true);

			currentTime = System.nanoTime();
			delta = currentTime - lastTick; // Time for render

			// Round delay to the nearest ms value (from ns)
			long delay = (rate - delta + 500000) / 1000000;

			// Adjust delay by 1ms depending on current time error
			// Forces average to the target rate
			if (timeError > 0) {
				delay--;
			} else if (timeError < 0) {
				delay++;
			}

			if (delay > 0) {

				renderer.updateRender(delay);//Use free time to make update

				delay = (rate - delta + 500000) / 1000000;

				// Adjust delay by 1ms depending on current time error
				// Forces average to the target rate
				if (timeError > 0) {
					delay--;
				} else if (timeError < 0) {
					delay++;
				}

				if (delay > 0) {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						Spout.log("[Severe] Interrupted while sleeping!");
					}
				}
				tooLongFrame = false;
			}else if(delay < - targetFrame){
				tooLongFrame = true;
			}else{
				tooLongFrame = false;
			}


			if (System.currentTimeMillis() - lastFrameTime > 1000) {
				lastFrameTime = System.currentTimeMillis();
				fps = frames;
				frames = 0;
			}
			frames++;
		}
		Display.destroy();
		c.stopEngine();
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
			boolean lastTickOverloaded = false;

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
					if (!lastTickOverloaded) {
						heavyLoad.set(false);
					}
					lastTickOverloaded = false;
					try {
						Thread.sleep(freeTime);
					} catch (InterruptedException e) {
						shutdown = true;
					}
				} else {
					lastTickOverloaded = true;
					heavyLoad.set(true);
				}
			}

			try {
				if (renderThread.isAlive()) {
					renderThread.join();
				}
			} catch (InterruptedException ie) {
				Spout.getLogger().info("Interrupted when waiting for render thread to end");
			}

			try {
				if (guiThread.isAlive()) {
					guiThread.join();
				}
			} catch (InterruptedException ie) {
				Spout.getLogger().info("Interrupted when waiting for gui thread to end");
			}

			heavyLoad.set(false);

			asyncExecutors.copySnapshot();
			try {
				copySnapshotWithLock(asyncExecutors.get());
			} catch (InterruptedException ex) {
				Spout.getLogger().log(Level.SEVERE, "Interrupt while running final snapshot copy: {0}", ex.getMessage());
			}

			taskManager.heartbeat(PULSE_EVERY << 2);
			taskManager.shutdown(1L);

			long delay = 2000;
			while (!taskManager.waitForAsyncTasks(delay)) {
				List<Worker> workers = taskManager.getActiveWorkers();
				if (workers.size() == 0) {
					break;
				}
				Spout.getLogger().info("Unable to shutdown due to async tasks still running");
				for (Worker w : workers) {
					Object owner = w.getOwner();
					if (owner instanceof CommonPlugin) {
						CommonPlugin p = (CommonPlugin) owner;
						Spout.getLogger().info("Task with id of " + w.getTaskId() + " owned by " + p.getName() + " is still running");
					} else {
						Spout.getLogger().info("Task with id of " + w.getTaskId() + " owned by " + w.getOwner() + " is still running");
					}
				}
				if (delay < 8000) {
					delay = delay << 1;
				}
			}

			runLastTickTasks();

			asyncExecutors.copySnapshot();
			try {
				copySnapshotWithLock(asyncExecutors.get());
			} catch (InterruptedException ex) {
				Spout.getLogger().log(Level.SEVERE, "Interrupt while running final snapshot copy: {0}", ex.getMessage());
			}

			asyncExecutors.copySnapshot();

			// Halt all executors, except the Server
			for (AsyncExecutor e : asyncExecutors.get()) {
				if (!(e.getManager() instanceof SpoutServer)) {
					if (!e.haltExecutor()) {
						throw new IllegalStateException("Unable to halt executor, " + e + " for " + e.getManager());
					}
				}
			}

			try {
				copySnapshotWithLock(asyncExecutors.get());
			} catch (InterruptedException ex) {
				Spout.getLogger().log(Level.SEVERE, "Error while halting all executors: {0}", ex.getMessage());
			}

			asyncExecutors.copySnapshot();

			// Halt the executor for the Server
			for (AsyncExecutor e : asyncExecutors.get()) {
				if (!(e.getManager() instanceof SpoutEngine)) {
					throw new IllegalStateException("Only the engine should be left to shutdown");
				}

				if (!e.haltExecutor()) {
					throw new IllegalStateException("Unable to halt engine executor");
				}
			}

			try {
				copySnapshotWithLock(asyncExecutors.get());
			} catch (InterruptedException ex) {
				engine.getLogger().log(Level.SEVERE, "Error while shutting down engine: {0}", ex.getMessage());
			}

			NetworkSendThreadPool.shutdown();

			runFinalTasks();

		}
	}

	private class GUIThread extends Thread {
		public GUIThread() {
			super("GUI Thread");
		}

		@Override
		public void run() {
			long targetPeriod = 1000/40;
			long lastTick = System.currentTimeMillis();
			long nextTick = lastTick + targetPeriod;
			float dt = (float) targetPeriod;

			ScreenStack stack = null;
			stack = ((Client) Spout.getEngine()).getScreenStack();

			while (!shutdown) {
				try {
					stack.tick(dt);
				} catch (Exception ex) {
					Spout.getLogger().log(Level.SEVERE, "[GUI] Error while pulsing: {0}", ex.getMessage());
					ex.printStackTrace();
				}
				long now = System.currentTimeMillis();
				long sleepFor = nextTick - now;
				if (sleepFor < 0) {
					sleepFor = 0;
				}
				try {
					sleep(sleepFor);
				} catch (InterruptedException e) {
					break;
				}
				dt = (float) (now - lastTick);
				lastTick = now;
				nextTick += targetPeriod;
			}
		}
	}

	public void startMainThread() {
		if (mainThread.isAlive()) {
			throw new IllegalStateException("Attempt was made to start the main thread twice");
		}

		mainThread.start();
	}

	public SpoutRenderer startRenderThread(Vector2 resolution, boolean ccoverride) {
		if (!(Spout.getEngine() instanceof SpoutClient)) {
			throw new IllegalStateException("Cannot start the rendering thread unless on the client");
		}
		if (renderThread.isAlive()) {
			throw new IllegalStateException("Attempt was made to start the render thread twice");
		}
		SpoutRenderer renderer = new SpoutRenderer(resolution, ccoverride);
		renderThread.setRenderer(renderer);
		renderThread.start();
		return renderer;
	}
	
	public void enqueueRenderTask(Runnable task){
		renderThread.enqueueRenderTask(task);		
	}
	
	public void startGuiThread() {
		if (!(Spout.getEngine() instanceof SpoutClient)) {
			throw new IllegalStateException("Cannot start the rendering thread unless on the client");
		}
		if (guiThread.isAlive()) {
			throw new IllegalStateException("Attempt was made to start the GUI thread twice");
		}
		guiThread.start();
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
	 * Stops the scheduler
	 */
	public void stop() {
		shutdown = true;
	}

	public void submitFinalTask(Runnable task) {
		submitFinalTask(task, false);
	}

	public void submitFinalTask(Runnable task, boolean addToStart) {
		if (addToStart) {
			finalTaskQueue.addFirst(task);
		} else {
			finalTaskQueue.addLast(task);
		}
		if (!mainThread.isAlive()) {
			runFinalTasks();
			Spout.getLogger().info("Attempting to submit final task after main thread had shutdown");
			Thread.dumpStack();
		}
	}

	public void submitLastTickTask(Runnable task) {
		lastTickTaskQueue.add(task);
		if (!mainThread.isAlive()) {
			runLastTickTasks();
			Spout.getLogger().info("Attempting to submit last tick task after main thread had shutdown");
			Thread.dumpStack();
		}
	}

	public void runFinalTasks() {
		Runnable r;
		while ((r = finalTaskQueue.poll()) != null) {
			r.run();
		}
	}

	public void runLastTickTasks() {
		Runnable r;
		while ((r = lastTickTaskQueue.poll()) != null) {
			r.run();
		}
	}

	/**
	 * Adds new tasks and updates existing tasks, removing them if necessary.
	 */
	private boolean tick(long delta) throws InterruptedException {
		TickStage.setStage(TickStage.TICKSTART);
		asyncExecutors.copySnapshot();

		taskManager.heartbeat(delta);

		if (parallelTaskManager == null) {
			parallelTaskManager = ((SpoutParallelTaskManager)engine.getParallelTaskManager());
		}
		parallelTaskManager.heartbeat(delta);

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
					if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
						logLongDurationTick("Stage " + stage, executors);
					}
				}
			}
			stage++;
		}

		lockSnapshotLock();

		try {
			int totalUpdates = -1;
			updates.set(1);
			while (updates.get() > 0 && totalUpdates < UPDATE_THRESHOLD) {
				totalUpdates += updates.getAndSet(0);

				doDynamicUpdates(executors);

				doPhysics(executors);
			}

			updates.set(1);
			while (updates.get() > 0 && totalUpdates < UPDATE_THRESHOLD) {
				totalUpdates += updates.getAndSet(0);

				doLighting(executors);
			}

			if (totalUpdates >= UPDATE_THRESHOLD) {
				Spout.getLogger().warning("Physics updates per tick of " + totalUpdates + " exceeded threshold " + UPDATE_THRESHOLD);
			}

			finalizeTick(executors);

			copySnapshot(executors);

			runCoreTasks();

			TickStage.setStage(TickStage.TICKSTART);
		} finally {
			unlockSnapshotLock();
		}
		return true;
	}

	private void doPhysics(List<AsyncExecutor> executors) throws InterruptedException {
		int passStartUpdates = updates.get() - 1;
		int startUpdates = updates.get();
		while (passStartUpdates < updates.get() && updates.get() < startUpdates + UPDATE_THRESHOLD) {
			passStartUpdates = updates.get();
			for (int sequence = -1; sequence < 27 && updates.get() < startUpdates + UPDATE_THRESHOLD; sequence++) {
				if (sequence == -1) {
					TickStage.setStage(TickStage.PHYSICS);
				} else {
					TickStage.setStage(TickStage.GLOBAL_PHYSICS);
				}

				for (AsyncExecutor e : executors) {
					if (!e.doPhysics(sequence)) {
						throw new IllegalStateException("Attempt made to do physics while the previous operation was still active");
					}
				}

				boolean joined = false;
				while (!joined) {
					try {
						AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
						joined = true;
					} catch (TimeoutException e) {
						if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
							logLongDurationTick("Local Physics", executors);
						}
					}
				}

			}
		}
	}

	private void doDynamicUpdates(List<AsyncExecutor> executors) throws InterruptedException {
		int passStartUpdates = updates.get() - 1;
		int startUpdates = updates.get();

		TickStage.setStage(TickStage.GLOBAL_DYNAMIC_BLOCKS);

		long earliestTime = END_OF_THE_WORLD;

		for (AsyncExecutor e : executors) {
			long firstTime = e.getManager().getFirstDynamicUpdateTime();
			if (firstTime < earliestTime) {
				earliestTime = firstTime;
			}
		}

		while (passStartUpdates < updates.get() && updates.get() < startUpdates + UPDATE_THRESHOLD) {
			passStartUpdates = updates.get();

			for (int sequence = -1; sequence < 27 && updates.get() < startUpdates + UPDATE_THRESHOLD; sequence++) {
				if (sequence == -1) {
					TickStage.setStage(TickStage.DYNAMIC_BLOCKS);
				} else {
					TickStage.setStage(TickStage.GLOBAL_DYNAMIC_BLOCKS);
				}
				long threshold = earliestTime + PULSE_EVERY - 1;

				for (AsyncExecutor e : executors) {
					if (!e.doDynamicUpdates(threshold, sequence)) {
						throw new IllegalStateException("Attempt made to pulse while the previous operation was still active");
					}
				}

				boolean joined = false;
				while (!joined) {
					try {
						AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
						joined = true;
					} catch (TimeoutException e) {
						if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
							logLongDurationTick("Local Dynamic Blocks", executors);
						}
					}
				}
			}
		}
	}

	private void doLighting(List<AsyncExecutor> executors) throws InterruptedException {
		int passStartUpdates = updates.get() - 1;
		int startUpdates = updates.get();
		while (passStartUpdates < updates.get() && updates.get() < startUpdates + UPDATE_THRESHOLD) {
			passStartUpdates = updates.get();
			for (int sequence = -1; sequence < 27 && updates.get() < startUpdates + UPDATE_THRESHOLD; sequence++) {
				if (sequence == -1) {
					TickStage.setStage(TickStage.LIGHTING);
				} else {
					TickStage.setStage(TickStage.GLOBAL_LIGHTING);
				}

				for (AsyncExecutor e : executors) {
					if (!e.doLighting(sequence)) {
						throw new IllegalStateException("Attempt made to do lighting while the previous operation was still active");
					}
				}

				boolean joined = false;
				while (!joined) {
					try {
						AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
						joined = true;
					} catch (TimeoutException e) {
						if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
							logLongDurationTick("Lighting", executors);
						}
					}
				}

			}
		}
	}

	public void addUpdates(int inc) {
		updates.addAndGet(inc);
	}

	private void runCoreTasks() {
		Runnable r;
		while ((r = coreTaskQueue.poll()) != null) {
			try {
				r.run();
			} catch (Exception e) {
				Spout.log("Exception thrown when executing core task");
				e.printStackTrace();
			}
		}
	}

	private void finalizeTick(List<AsyncExecutor> executors) throws InterruptedException {
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
				if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
					logLongDurationTick("Finalize", executors);
				}
			}
		}
	}

	private void copySnapshotWithLock(List<AsyncExecutor> executors) throws InterruptedException {
		lockSnapshotLock();
		try {
			copySnapshot(executors);

			TickStage.setStage(TickStage.TICKSTART);
		} finally {
			unlockSnapshotLock();
		}
	}

	private void copySnapshot(List<AsyncExecutor> executors) throws InterruptedException {

		TickStage.setStage(TickStage.PRESNAPSHOT);

		for (AsyncExecutor e : executors) {
			if (!e.preSnapshot()) {
				throw new IllegalStateException("Attempt made to enter the pre-snapshot stage for a tick while the previous operation was still active");
			}
		}

		boolean joined = false;
		while (!joined) {
			try {
				AsyncExecutorUtils.pulseJoinAll(executors, (PULSE_EVERY << 4));
				joined = true;
			} catch (TimeoutException e) {
				if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
					logLongDurationTick("Pre Snapshot", executors);
				}
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
				if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
					logLongDurationTick("Copy Snapshot", executors);
				}
			}
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
				long stallTime = System.currentTimeMillis() - startTime;
				engine.getLogger().info("Unable to lock snapshot after " + stallTime + "ms");
				for (Plugin p : violatingPlugins) {
					engine.getLogger().info(p.getDescription().getName() + " has locked the snapshot lock for more than " + threshold + "ms");
				}
				for (String s : snapshotLock.getLockingTasks()) {
					engine.getLogger().info("Core task " + s + " is holding the lock");
				}
				if (stallTime > 2000) {
					engine.getLogger().info("--- Stack dump of core Threads holding lock ---");
					for (Thread t : snapshotLock.getCoreLockingThreads()) {
						AsyncExecutorUtils.dumpStackTrace(t);
					}
					engine.getLogger().info("-----------------------------------------------");
				}
			}
		}
	}

	private void unlockSnapshotLock() {
		snapshotLock.writeUnlock();
	}

	@Override
	public Task scheduleSyncDelayedTask(Object plugin, Runnable task) {
		return taskManager.scheduleSyncDelayedTask(plugin, task);
	}

	@Override
	public Task scheduleSyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority) {
		return taskManager.scheduleSyncDelayedTask(plugin, task, delay, priority);
	}

	@Override
	public Task scheduleSyncDelayedTask(Object plugin, Runnable task, TaskPriority priority) {
		return taskManager.scheduleSyncDelayedTask(plugin, task, priority);
	}

	@Override
	public Task scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period, TaskPriority priority) {
		return taskManager.scheduleSyncRepeatingTask(plugin, task, delay, period, priority);
	}

	@Override
	public Task scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority) {
		return taskManager.scheduleAsyncDelayedTask(plugin, task, delay, priority);
	}


	@Override
	public Task scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority, boolean longLife) {
		return taskManager.scheduleAsyncDelayedTask(plugin, task, delay, priority, longLife);
	}

	@Override
	public Task scheduleAsyncTask(Object plugin, Runnable task) {
		return taskManager.scheduleAsyncTask(plugin, task);
	}

	@Override
	public Task scheduleAsyncTask(Object plugin, Runnable task, boolean longLife) {
		return taskManager.scheduleAsyncTask(plugin, task, longLife);
	}

	@Override
	public <T> Future<T> callSyncMethod(Object plugin, Callable<T> task, TaskPriority priority) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isQueued(int taskId) {
		return taskManager.isQueued(taskId);
	}

	@Override
	public void cancelTask(int taskId) {
		taskManager.cancelTask(taskId);
	}

	@Override
	public void cancelTask(Task task) {
		taskManager.cancelTask(task);
	}

	@Override
	public void cancelTasks(Object plugin) {
		taskManager.cancelTasks(plugin);
	}

	@Override
	public void cancelAllTasks() {
		taskManager.cancelAllTasks();
	}

	@Override
	public List<Worker> getActiveWorkers() {
		return taskManager.getActiveWorkers();
	}

	@Override
	public List<Task> getPendingTasks() {
		return taskManager.getPendingTasks();
	}

	@Override
	public long getUpTime() {
		return taskManager.getUpTime();
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

	@Override
	public boolean isServerOverloaded() {
		if (heavyLoad.get()) {
			return true;
		}

		if (getRemainingTickTime() >= 0) {
			return false;
		}

		heavyLoad.set(true);
		return true;
	}

	/**
	 * For internal use only.  This is for tasks that must happen right at the start of the new tick.<br>
	 * <br>
	 * Tasks are executed in the order that they are received.<br>
	 * <br>
	 * It is used for region unloading and multi-region dynamic block updates
	 *
	 * @param r
	 */
	public void scheduleCoreTask(Runnable r) {
		coreTaskQueue.add(r);
	}

	private void logLongDurationTick(String stage, Iterable<AsyncExecutor> executors) {
		/*
		engine.getLogger().info("Tick stage (" + stage + ") had not completed after " + (PULSE_EVERY << 4) + "ms");
		AsyncExecutorUtils.dumpAllStacks();
		AsyncExecutorUtils.checkForDeadlocks();
		for (AsyncExecutor executor : executors) {
			if (!executor.isPulseFinished()) {
				if (executor.getManager() instanceof SpoutRegionManager) {
					SpoutRegionManager m = (SpoutRegionManager)executor.getManager();
					engine.getLogger().info("Region manager has not completed pulse " + m.getParent());
				} else if (executor.getManager() instanceof SpoutWorld) {
					SpoutWorld w = (SpoutWorld)executor.getManager();
					engine.getLogger().info("World has not completed pulse " + w);
				} else {
					engine.getLogger().info("Async Manager has not completed pulse " + executor.getManager().getClass().getSimpleName());
				}
				if (executor instanceof Thread) {
					StackTraceElement[] stackTrace = ((Thread)executor).getStackTrace();
					engine.getLogger().info("Thread for stalled manager is executing");
					for (StackTraceElement e : stackTrace) {
						engine.getLogger().info("\tat " + e);
					}
				}
			}
		}
		 */
	}

}
