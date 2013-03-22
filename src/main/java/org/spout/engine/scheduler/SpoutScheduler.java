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

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.lwjgl.opengl.Display;
import org.spout.api.Engine;
import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.gui.ScreenStack;
import org.spout.api.math.Vector2;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.scheduler.TickStage;
import org.spout.api.scheduler.Worker;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.SpoutRenderer;
import org.spout.engine.protocol.NetworkSendThreadPool;
import org.spout.engine.util.thread.AsyncExecutorUtils;
import org.spout.engine.util.thread.AsyncManager;
import org.spout.engine.util.thread.coretasks.CopySnapshotTask;
import org.spout.engine.util.thread.coretasks.DynamicUpdatesTask;
import org.spout.engine.util.thread.coretasks.FinalizeTask;
import org.spout.engine.util.thread.coretasks.LightingTask;
import org.spout.engine.util.thread.coretasks.ManagerRunnableFactory;
import org.spout.engine.util.thread.coretasks.PhysicsTask;
import org.spout.engine.util.thread.coretasks.PreSnapshotTask;
import org.spout.engine.util.thread.coretasks.StartTickTask;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableArrayList;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;

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
	private static final int TARGET_FPS = 60;
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
	private final SnapshotableArrayList<AsyncManager> asyncManagers = new SnapshotableArrayList<AsyncManager>(snapshotManager, null);
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
	
	// Scheduler tasks
	private final StartTickTask[] startTickTask = new StartTickTask[] {
			new StartTickTask(0),
			new StartTickTask(1),
			new StartTickTask(2)
	};
	private final DynamicUpdatesTask dynamicUpdatesTask = new DynamicUpdatesTask();
	private final PhysicsTask physicsTask = new PhysicsTask();
	private final LightingTask lightingTask = new LightingTask();
	private final FinalizeTask finalizeTask = new FinalizeTask();
	private final PreSnapshotTask preSnapshotTask = new PreSnapshotTask();
	private final CopySnapshotTask copySnapshotTask = new CopySnapshotTask();
	
	// scheduler executor service
	private final ExecutorService executorService;

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
		if (engine.getPlatform() == Platform.CLIENT) {
			renderThread = new RenderThread((SpoutClient) engine);
			guiThread = new GUIThread((SpoutClient) engine);
		} else {
			renderThread = null;
			guiThread = null;
		}
		
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1, new NamedThreadFactory("SpoutScheduler - async manager executor service", true));
		
		taskManager = new SpoutTaskManager(this, mainThread);
	}
	
	

	private class RenderThread extends Thread {
		private int fps = 0;
		boolean tooLongFrame = false;
		private SpoutRenderer renderer;
		private final SpoutClient client;
		private ConcurrentLinkedQueue<Runnable> renderTaskQueue = new ConcurrentLinkedQueue<Runnable>();
		Canvas parent = null;
		

		public RenderThread(SpoutClient client) {
			super("Render Thread");
			this.client = client;
		}

		public void setRenderer(SpoutRenderer renderer) {			
			this.renderer = renderer;
		}

		public void enqueueRenderTask(Runnable task){
			renderTaskQueue.add(task);
		}

		public void setParent(Canvas parent){
			this.parent = parent;			
		}

		public int getFps() {
			return fps;
		}

		public boolean getFrameOverhead() {
			return tooLongFrame;
		}

		@Override
		public void run() {
			renderer.initRenderer(parent);
			int frames = 0;
			long lastFrameTime = System.currentTimeMillis();
			int targetFrame = (int)(1f / TARGET_FPS * 1000);
			int rate = (int) ((1f / TARGET_FPS) * 1000000000);

			long lastTick = System.nanoTime();

			long timeError = 0;
			long maxError = rate >> 2; // time error total limited to 0.25 seconds 

		while (!shutdown) {
			if (Display.isCloseRequested() || !client.isRendering()) {
				client.stop();
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
						Spout.severe("Interrupted while sleeping!");
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
		client.stopEngine();
		}
	}

	private class MainThread extends Thread {
		public MainThread() {
			super("MainThread");
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
					Spout.severe("Error while pulsing: {0}", ex.getMessage());
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
				Spout.info("Interrupted when waiting for render thread to end");
			}

			try {
				if (guiThread.isAlive()) {
					guiThread.join();
				}
			} catch (InterruptedException ie) {
				Spout.info("Interrupted when waiting for gui thread to end");
			}

			heavyLoad.set(false);

			asyncManagers.copySnapshot();
			try {
				copySnapshotWithLock(asyncManagers.get());
			} catch (InterruptedException ex) {
				Spout.severe("Interrupt while running final snapshot copy: {0}", ex.getMessage());
			}

			taskManager.heartbeat(PULSE_EVERY << 2);
			taskManager.shutdown(1L);

			long delay = 2000;
			while (!taskManager.waitForAsyncTasks(delay)) {
				List<Worker> workers = taskManager.getActiveWorkers();
				if (workers.size() == 0) {
					break;
				}
				Spout.info("Unable to shutdown due to async tasks still running");
				for (Worker w : workers) {
					Object owner = w.getOwner();
					if (owner instanceof CommonPlugin) {
						CommonPlugin p = (CommonPlugin) owner;
						Spout.info("Task with id of " + w.getTaskId() + " owned by " + p.getName() + " is still running");
					} else {
						Spout.info("Task with id of " + w.getTaskId() + " owned by " + w.getOwner() + " is still running");
					}
				}
				if (delay < 8000) {
					delay = delay << 1;
				}
			}

			runLastTickTasks();

			asyncManagers.copySnapshot();
			try {
				copySnapshotWithLock(asyncManagers.get());
			} catch (InterruptedException ex) {
				Spout.severe("Interrupt while running final snapshot copy: {0}", ex.getMessage());
			}

			asyncManagers.copySnapshot();

			try {
				copySnapshotWithLock(asyncManagers.get());
			} catch (InterruptedException ex) {
				Spout.severe("Error while halting all executors: {0}", ex.getMessage());
			}

			asyncManagers.copySnapshot();

			try {
				copySnapshotWithLock(asyncManagers.get());
			} catch (InterruptedException ex) {
				Spout.severe("Error while shutting down engine: {0}", ex.getMessage());
			}
			
			// Shutdown manager thread pool

			NetworkSendThreadPool.shutdown();

			runFinalTasks();

		}
	}

	private class GUIThread extends Thread {
		private final SpoutClient client;

		public GUIThread(SpoutClient client) {
			super("GUI Thread");
			this.client = client;
		}

		@Override
		public void run() {
			long targetPeriod = 1000/40;
			long lastTick = System.currentTimeMillis();
			long nextTick = lastTick + targetPeriod;
			float dt = (float) targetPeriod;

			ScreenStack stack;
			stack = client.getScreenStack();

			while (!shutdown) {
				try {
					stack.tick(dt);
				} catch (Exception ex) {
					Spout.severe("Error while pulsing: {0}", ex.getMessage());
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

	public SpoutRenderer startRenderThread(SpoutClient client, Vector2 resolution, boolean ccoverride, Canvas parent) {
		if (renderThread.isAlive()) {
			throw new IllegalStateException("Attempt was made to start the render thread twice");
		}
		SpoutRenderer renderer = new SpoutRenderer(client, resolution, ccoverride);
		renderThread.setRenderer(renderer);
		renderThread.setParent(parent);
		renderThread.start();
		return renderer;
	}
	
	public void enqueueRenderTask(Runnable task){
		renderThread.enqueueRenderTask(task);		
	}
	
	public void startGuiThread() {
		if (guiThread.isAlive()) {
			throw new IllegalStateException("Attempt was made to start the GUI thread twice");
		}
		guiThread.start();
	}

	/**
	 * Adds an async manager to the scheduler
	 */
	@DelayedWrite
	public boolean addAsyncManager(AsyncManager manager) {
		return asyncManagers.add(manager);
	}

	/**
	 * Removes an async manager from the scheduler
	 */
	@DelayedWrite
	public boolean removeAsyncManager(AsyncManager manager) {
		return asyncManagers.remove(manager);
	}

	/**
	 * Stops the scheduler
	 */
	public void stop() {
		shutdown = true;
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
		asyncManagers.copySnapshot();

		taskManager.heartbeat(delta);

		if (parallelTaskManager == null) {
			parallelTaskManager = ((SpoutParallelTaskManager)engine.getParallelTaskManager());
		}
		parallelTaskManager.heartbeat(delta);

		List<AsyncManager> managers = asyncManagers.get();

		TickStage.setStage(TickStage.STAGE1);

		for (int stage = 0; stage < this.startTickTask.length; stage++) {
			if (stage == 0) {
				TickStage.setStage(TickStage.STAGE1);
			} else {
				TickStage.setStage(TickStage.STAGE2P);
			}
			
			startTickTask[stage].setDelta(delta);
			
			int tickStage = stage == 0 ? TickStage.STAGE1 : TickStage.STAGE2P;
			
			runTasks(managers, startTickTask[stage], "Stage " + stage, tickStage);
		}

		lockSnapshotLock();

		try {
			int totalUpdates = -1;
			int lightUpdates = 0;
			int dynamicUpdates = 0;
			int physicsUpdates = 0;
			updates.set(1);
			int uD = 1;
			int uP = 1;
			while ((uD + uP) > 0 && totalUpdates < UPDATE_THRESHOLD) {
				if (SpoutConfiguration.DYNAMIC_BLOCKS.getBoolean()) {
					doDynamicUpdates(managers);
				}

				uD = updates.getAndSet(0);
				totalUpdates += uD;
				dynamicUpdates += uD;

				if (SpoutConfiguration.BLOCK_PHYSICS.getBoolean()) {
					doPhysics(managers);
				}
				
				uP = updates.getAndSet(0);
				totalUpdates += uP;
				physicsUpdates += uP;
			}

			updates.set(1);

			doLighting(managers);

			if (totalUpdates >= UPDATE_THRESHOLD) {
				Spout.getLogger().warning("Block updates per tick of " + totalUpdates + " exceeded the threshold " + UPDATE_THRESHOLD + "; " + dynamicUpdates + " dynamic updates, " + physicsUpdates + " block physics updates and " + lightUpdates + " lighting updates");
			}

			finalizeTick(managers);

			copySnapshot(managers);

			runCoreTasks();

			TickStage.setStage(TickStage.TICKSTART);
		} finally {
			unlockSnapshotLock();
		}
		return true;
	}

	private void doPhysics(List<AsyncManager> managers) throws InterruptedException {
		int passStartUpdates = updates.get() - 1;
		int startUpdates = updates.get();
		while (passStartUpdates < updates.get() && updates.get() < startUpdates + UPDATE_THRESHOLD) {
			passStartUpdates = updates.get();
			this.runTasks(managers, physicsTask, "Physics", TickStage.GLOBAL_PHYSICS, TickStage.PHYSICS);
		}
	}

	private void doDynamicUpdates(List<AsyncManager> managers) throws InterruptedException {
		int passStartUpdates = updates.get() - 1;
		int startUpdates = updates.get();

		TickStage.setStage(TickStage.GLOBAL_DYNAMIC_BLOCKS);

		long earliestTime = END_OF_THE_WORLD;

		for (AsyncManager e : managers) {
			long firstTime = e.getFirstDynamicUpdateTime();
			if (firstTime < earliestTime) {
				earliestTime = firstTime;
			}
		}

		while (passStartUpdates < updates.get() && updates.get() < startUpdates + UPDATE_THRESHOLD) {
			passStartUpdates = updates.get();

			long threshold = earliestTime + PULSE_EVERY - 1;
				
			dynamicUpdatesTask.setThreshold(threshold);

			this.runTasks(managers, dynamicUpdatesTask, "Dynamic Blocks", TickStage.GLOBAL_DYNAMIC_BLOCKS, TickStage.DYNAMIC_BLOCKS);
		}
	}

	private void doLighting(List<AsyncManager> managers) throws InterruptedException {
		this.runTasks(managers, lightingTask, "Lighting", TickStage.LIGHTING);
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

	private void finalizeTick(List<AsyncManager> managers) throws InterruptedException {
		this.runTasks(managers, finalizeTask, "Finalize", TickStage.FINALIZE);
	}

	private void copySnapshotWithLock(List<AsyncManager> managers) throws InterruptedException {
		lockSnapshotLock();
		try {
			copySnapshot(managers);

			TickStage.setStage(TickStage.TICKSTART);
		} finally {
			unlockSnapshotLock();
		}
	}

	private void copySnapshot(List<AsyncManager> managers) throws InterruptedException {

		this.runTasks(managers, preSnapshotTask, "Pre-snapshot", TickStage.PRESNAPSHOT);

		this.runTasks(managers, copySnapshotTask, "Copy-snapshot", TickStage.SNAPSHOT);
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
	
	private void runTasks(List<AsyncManager> managers, ManagerRunnableFactory taskFactory, String stageString, int tickStage) {
		runTasks(managers, taskFactory, stageString, tickStage, tickStage);
	}
	
	private void runTasks(List<AsyncManager> managers, ManagerRunnableFactory taskFactory, String stageString, int globalStage, int localStage) {
		int maxSequence = taskFactory.getMaxSequence();
		for (int s = taskFactory.getMinSequence(); s <= maxSequence; s++) {
			if (s == -1) {
				TickStage.setStage(localStage);
			} else {
				TickStage.setStage(globalStage);
			}
			List<Future<?>> futures = new ArrayList<Future<?>>(managers.size());
			for (AsyncManager manager : managers) {
				if (s == -1 || s == manager.getSequence()) {
					Runnable r = taskFactory.getTask(manager, s);
					if (r != null) {
						futures.add(executorService.submit(r));
					}
				}
			}
			forLoop:
			for (int i = 0; i < futures.size(); i++) {
				boolean done = false;
				while (!done) {
					try {
						Future<?> f = futures.get(i);
						if (!f.isDone()) {
							f.get(PULSE_EVERY << 4, TimeUnit.MILLISECONDS);
						}
						done = true;
					} catch (InterruptedException e) {
						Spout.getLogger().info("Warning: main thread interrupted while waiting on tick stage task, " + taskFactory.getClass().getName());
						break forLoop;
					} catch (ExecutionException e) {
						Spout.getLogger().info("Exception thrown when executing task, " + taskFactory.getClass().getName() + ", " + e.getMessage());
						e.printStackTrace();
						done = true;
					} catch (TimeoutException e) {
						if (((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
							logLongDurationTick(stageString, managers);
						}
					}
				}
			}
		}
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
	public SpoutSnapshotLock getSnapshotLock() {
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
	
	@Override
	public void safeRun(final Plugin plugin, final Runnable task) {
		SpoutSnapshotLock lock = getSnapshotLock();
		lock.readLock(plugin);
		try {
			try {
				task.run();
			} catch (Exception e) {
				Spout.getLogger().info("Exception throw when executing task from plugin " + plugin.getName() + ", " + e.getMessage());
			}
		} finally {
			lock.readUnlock(plugin);
		}
	}
	
	public void coreSafeRun(final String taskName, final Runnable task) {
		SpoutSnapshotLock lock = getSnapshotLock();
		lock.coreReadLock(taskName);
		try {
			try {
				task.run();
			} catch (Exception e) {
				Spout.getLogger().info("Exception throw when executing task, " + taskName + ", " + e.getMessage());
			}
		} finally {
			lock.coreReadUnlock(taskName);
		}
	}
	
	@Override
	public <T> T safeCall(final Plugin plugin, final Callable<T> task) {
		SpoutSnapshotLock lock = getSnapshotLock();
		lock.readLock(plugin);
		try {
			try {
				return task.call();
			} catch (Exception e) {
				Spout.getLogger().info("Exception throw when executing task from plugin " + plugin.getName() + ", " + e.getMessage());
				return null;
			}
		} finally {
			lock.readUnlock(plugin);
		}
	}
	
	public <T> T coreSafeCall(final String taskName, final Callable<T> task) {
		SpoutSnapshotLock lock = getSnapshotLock();
		lock.coreReadLock(taskName);
		try {
			try {
				return task.call();
			} catch (Exception e) {
				Spout.getLogger().info("Exception throw when executing task, " + taskName + ", " + e.getMessage());
				return null;
			}
		} finally {
			lock.coreReadUnlock(taskName);
		}
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

	private void logLongDurationTick(String stage, Iterable<AsyncManager> executors) {
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
