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
package org.spout.server.util.thread;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.server.scheduler.SpoutScheduler;
import org.spout.server.util.thread.coretasks.CopySnapshotTask;
import org.spout.server.util.thread.coretasks.FinalizeTask;
import org.spout.server.util.thread.coretasks.PreSnapshotTask;
import org.spout.server.util.thread.coretasks.StartTickTask;
import org.spout.server.util.thread.future.ManagedFuture;

/**
 * This is a thread that executes tasks
 */
public final class ThreadAsyncExecutor extends PulsableThread implements AsyncExecutor {
	private ConcurrentLinkedQueue<ManagementTask> taskQueue = new ConcurrentLinkedQueue<ManagementTask>();
	private AtomicBoolean wakePending = new AtomicBoolean(false);
	private AtomicInteger wakeCounter = new AtomicInteger(0);
	private AtomicReference<Object> waitingMonitor = new AtomicReference<Object>();
	private CopySnapshotTask copySnapshotTask = new CopySnapshotTask();
	private StartTickTask startTickTask = new StartTickTask();
	private PreSnapshotTask preSnapshotTask = new PreSnapshotTask();
	private FinalizeTask finalizeTask = new FinalizeTask();
	private AsyncManager manager = null;
	private AtomicReference<ExecutorState> state = new AtomicReference<ExecutorState>(ExecutorState.CREATED);

	public void setManager(AsyncManager manager) {
		if (this.manager != null) {
			throw new IllegalStateException("The manager for an AsyncExecutor may not be set more than once");
		}
		this.manager = manager;
	}

	public boolean startExecutor() {
		if (state.compareAndSet(ExecutorState.CREATED, ExecutorState.STARTED)) {
			super.start();
			return true;
		} else {
			return false;
		}
	}

	public boolean haltExecutor() {
		if (state.compareAndSet(ExecutorState.CREATED, ExecutorState.HALTED)) {
			return true;
		} else if (state.compareAndSet(ExecutorState.STARTED, ExecutorState.HALTING)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void haltCheck() throws InterruptedException {
		if (state.compareAndSet(ExecutorState.HALTING, ExecutorState.HALTED)) {
			getManager().haltRun();
			((SpoutScheduler) (getManager().getServer().getScheduler())).removeAsyncExecutor(this);
			throw new InterruptedException("Executor halted");
		}
	}

	public final Future<Serializable> addToQueue(ManagementTask task) throws InterruptedException {
		if (Thread.currentThread() == this) {
			executeTask(task);
		} else {
			taskQueue.add(task);
			pulse();
		}
		ManagedFuture<Serializable> future = task.getFuture();
		if (future != null) {
			future.setManager(manager);
		}
		return future;
	}

	public final void waitForFuture(ManagedFuture<Serializable> future) throws InterruptedException {
		ThreadsafetyManager.checkCurrentThread(this);
		checkFuture(future);

		Object monitor = future.getMonitor();
		waitingMonitor.set(monitor);

		while (!future.isDone()) {
			executeAllTasks();
			synchronized (monitor) {
				if (!taskQueue.isEmpty()) {
					continue;
				}
				future.waitForMonitor();
			}
		}

		waitingMonitor.set(null);
	}

	/**
	 * Checks if the future is associated with this executor
	 *
	 * @param future the future
	 */
	private void checkFuture(ManagedFuture<Serializable> future) {
		if (future.getManager().getExecutor() != this) {
			throw new IllegalArgumentException("AsyncExecutors may only wait on futures linked with the executor");
		}
	}

	/**
	 * Executes all tasks on the queue
	 *
	 * @throws InterruptedException
	 */
	private final void executeAllTasks() throws InterruptedException {
		ThreadsafetyManager.checkCurrentThread(this);
		ManagementTask task;
		while ((task = taskQueue.poll()) != null) {
			executeTask(task);
		}
	}

	private final void executeTask(ManagementTask task) throws InterruptedException {
		Serializable returnValue = task.call(this);
		ManagedFuture<Serializable> future = task.getFuture();
		if (future != null) {
			future.set(returnValue);
		}
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
	public final boolean startTick(int stage, long delta) {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(startTickTask.setStageDelta(stage, delta));
		return pulse();
	}

	@Override
	public final boolean isPulseFinished() {
		try {
			disableWake();
			return !isPulsing() && taskQueue.isEmpty();
		} finally {
			enableWake();
		}
	}

	@Override
	public final void disableWake() {
		wakeCounter.incrementAndGet();
	}

	@Override
	public final void enableWake() {
		int localCounter = wakeCounter.decrementAndGet();
		if (localCounter == 0 && wakePending.compareAndSet(true, false)) {
			pulse();
		}
	}

	@Override
	public boolean pulse() {
		Object monitor = waitingMonitor.get();
		if (monitor != null) {
			synchronized (monitor) {
				monitor.notifyAll();
			}
		}
		return super.pulse();
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
