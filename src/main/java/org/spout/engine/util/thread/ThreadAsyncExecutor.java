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
package org.spout.engine.util.thread;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.util.concurrent.AtomicIntegerHelper;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.thread.coretasks.CopySnapshotTask;
import org.spout.engine.util.thread.coretasks.DynamicUpdatesTask;
import org.spout.engine.util.thread.coretasks.FinalizeTask;
import org.spout.engine.util.thread.coretasks.LightingTask;
import org.spout.engine.util.thread.coretasks.PhysicsTask;
import org.spout.engine.util.thread.coretasks.PreSnapshotTask;
import org.spout.engine.util.thread.coretasks.StartTickTask;

/**
 * This is a thread that executes tasks
 */
public final class ThreadAsyncExecutor extends PulsableThread implements AsyncExecutor {
	private final ConcurrentLinkedQueue<ManagementRunnable> taskQueue = new ConcurrentLinkedQueue<ManagementRunnable>();
	private final CopySnapshotTask copySnapshotTask = new CopySnapshotTask();
	private final StartTickTask startTickTask = new StartTickTask();
	private final DynamicUpdatesTask dynamicUpdatesTask = new DynamicUpdatesTask();
	private final PhysicsTask physicsTask = new PhysicsTask();
	private final LightingTask lightingTask = new LightingTask();
	private final PreSnapshotTask preSnapshotTask = new PreSnapshotTask();
	private final FinalizeTask finalizeTask = new FinalizeTask();
	private AsyncManager manager = null;
	private final AtomicReference<ExecutorState> state = new AtomicReference<ExecutorState>(ExecutorState.CREATED);
	private final AtomicInteger wakeCounter = new AtomicInteger(0);
	private final int sequence;
	private static final int wakePulsing = 1;
	private static final int wakePending = 2;
	private static final int wakeDelta = 4;

	public ThreadAsyncExecutor(String name) {
		this(name, Integer.MIN_VALUE);
	}
	
	public ThreadAsyncExecutor(String name, int sequence) {
		super(name);
		this.sequence = sequence;
	}

	@Override
	public void setManager(AsyncManager manager) {
		if (this.manager != null) {
			throw new IllegalStateException("The manager for an AsyncExecutor may not be set more than once");
		}
		this.manager = manager;
	}

	@Override
	public boolean startExecutor() {
		if (!state.compareAndSet(ExecutorState.CREATED, ExecutorState.STARTED)) {
			// Not coming from CREATED => fail
			return false;
		}

		super.start();
		return true;
	}

	@Override
	public boolean haltExecutor() {
		if (state.compareAndSet(ExecutorState.CREATED, ExecutorState.HALTED)) {
			// Coming from CREATED => success
			return true;
		}

		if (state.compareAndSet(ExecutorState.STARTED, ExecutorState.HALTING)) {
			// Coming from STARTED => success
			return true;
		}

		// Not coming from CREATED or STARTED => fail
		return false;
	}

	@Override
	public void haltCheck() throws InterruptedException {
		if (state.compareAndSet(ExecutorState.HALTING, ExecutorState.HALTED)) {
			getManager().haltRun();
			((SpoutScheduler) getManager().getEngine().getScheduler()).removeAsyncExecutor(this);
			throw new InterruptedException("Executor halted");
		}
	}

	@Override
	public final void addToQueue(ManagementRunnable task) throws InterruptedException {
		if (Thread.currentThread() == this) {
			executeTask(task);
		} else {
			taskQueue.add(task);
			pulse();
		}
	}

	/**
	 * Executes all tasks on the queue
	 * @throws InterruptedException
	 */
	private final void executeAllTasks() throws InterruptedException {
		ThreadsafetyManager.checkCurrentThread(this);
		ManagementRunnable task;
		while ((task = taskQueue.poll()) != null) {
			executeTask(task);
		}
	}

	private final void executeTask(ManagementRunnable task) throws InterruptedException {
		task.run(this);
	}

	@Override
	public final boolean copySnapshot() {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(copySnapshotTask);
		return pulse();
	}

	@Override
	public final boolean finalizeTick() {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(finalizeTask);
		return pulse();
	}

	@Override
	public final boolean preSnapshot() {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(preSnapshotTask);
		return pulse();
	}
	
	@Override
	public final boolean doPhysics(int sequence) {
		if (sequence == -1 || sequence == this.sequence) {
			ThreadsafetyManager.checkMainThread();
			physicsTask.setSequence(sequence);
			taskQueue.add(physicsTask);
			return pulse();
		} else {
			return true;
		}
	}
	
	@Override
	public final boolean doDynamicUpdates(long time, int sequence) {
		if (sequence == -1 || sequence == manager.getSequence()) {
			ThreadsafetyManager.checkMainThread();
			dynamicUpdatesTask.setTime(time);
			dynamicUpdatesTask.setSequence(sequence);
			taskQueue.add(dynamicUpdatesTask);
			return pulse();
		} else {
			return true;
		}
	}
	
	@Override
	public final boolean doLighting(int sequence) {
		if (sequence == -1 || sequence == this.sequence) {
			ThreadsafetyManager.checkMainThread();
			lightingTask.setSequence(sequence);
			taskQueue.add(lightingTask);
			return pulse();
		} else {
			return true;
		}
	}
	
	@Override
	public final boolean startTick(int stage, long delta) {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(startTickTask.setStageDelta(stage, delta));
		return pulse();
	}

	@Override
	public final boolean isPulseFinished() {
		try {
			disableWake();
			return (wakeCounter.getAndAdd(0) & wakePulsing) == 0 && !isPulsing() && taskQueue.isEmpty() || !isAlive();
		} finally {
			enableWake();
		}
	}

	@Override
	public final void disableWake() {
		wakeCounter.addAndGet(wakeDelta);
	}

	@Override
	public final void enableWake() {
		boolean success = false;
		while (!success) {
			int current = wakeCounter.get();
			if (current < wakeDelta) {
				throw new IllegalStateException("Wake counter should never be negative: " + (current - wakeDelta));
			}
			if (current == wakePending + wakeDelta) {
				if (wakeCounter.compareAndSet(wakePending + wakeDelta, wakePulsing)) {
					try {
						super.pulse();
					} finally {
						if (!AtomicIntegerHelper.clearBit(wakeCounter, wakePulsing)) {
							throw new IllegalStateException("Bit zero of wake counter set to 0 while pulse was being triggered");
						}
						success = true;
					}		
				} else {
					success = false;
				}
			} else {
				success = wakeCounter.compareAndSet(current, current - wakeDelta);
			}
		}
	}

	@Override
	public boolean pulse() {
		if (wakeCounter.compareAndSet(0, wakePulsing)) {
			try {
				return super.pulse();
			} finally {
				if (!AtomicIntegerHelper.clearBit(wakeCounter, wakePulsing)) {
					throw new IllegalStateException("Bit zero of wake counter set to 0 while pulse was being triggered");
				}
			}
		} else if (AtomicIntegerHelper.setField(wakeCounter, wakePending | wakePulsing, 0, wakePending)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * All tasks on the queue are executed whenever the thread is pulsed
	 */
	@Override
	protected final void pulsedRun() throws InterruptedException {
		ThreadsafetyManager.checkCurrentThread(this);
		executeAllTasks();
	}

	@Override
	public AsyncManager getManager() {
		return manager;
	}

	@Override
	public void syncKill() throws InterruptedException {
		executeAllTasks();
		throw new InterruptedException("Executor killed");
	}

	private static enum ExecutorState {
		CREATED,
		STARTED,
		HALTING,
		HALTED
	}
}
