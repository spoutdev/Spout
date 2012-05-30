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
package org.spout.engine.util.thread;

import java.util.WeakHashMap;

import org.spout.api.Engine;
import org.spout.api.scheduler.Scheduler;
import org.spout.engine.SpoutEngine;
import org.spout.engine.scheduler.SpoutScheduler;

public abstract class AsyncManager {
	private final int maxStage;
	private final Engine engine; // null means that this AsyncManager is the Server
	private final AsyncExecutor executor;
	private final WeakHashMap<Managed, Boolean> managedSet = new WeakHashMap<Managed, Boolean>();
	private final ManagementTask[] singletonCache = new ManagementTask[ManagementTaskEnum.getMaxId()];

	public AsyncManager(int maxStage, AsyncExecutor executor) {
		this.executor = executor;
		engine = null;
		this.maxStage = maxStage;
		executor.setManager(this);
	}

	public AsyncManager(int maxStage, AsyncExecutor executor, Engine server) {
		this.executor = executor;
		this.engine = server;
		this.maxStage = maxStage;
		executor.setManager(this);
		registerWithScheduler(((SpoutEngine) server).getScheduler());
	}

	public void registerWithScheduler(Scheduler scheduler) {
		((SpoutScheduler) scheduler).addAsyncExecutor(executor);
	}

	public Engine getEngine() {
		if (engine == null) {
			if (!(this instanceof Engine)) {
				throw new IllegalStateException("Only the Server object itself should have a null server reference");
			} else {
				return (Engine) this;
			}
		} else {
			return engine;
		}
	}

	// TODO - these 2 methods are probably overly complex for requirements

	/**
	 * Gets a singleton task if available.
	 * <p/>
	 * Tasks should be returned to the cache after usage
	 * @param taskEnum the enum of the task
	 * @return an instance of task
	 */
	public ManagementTask getSingletonTask(ManagementTaskEnum taskEnum) {
		Thread current = Thread.currentThread();
		if (current instanceof AsyncExecutor) {
			AsyncExecutor executor = (AsyncExecutor) current;
			int taskId = taskEnum.getId();
			ManagementTask[] taskCache = executor.getManager().singletonCache;
			ManagementTask task = taskCache[taskId];
			if (task != null) {
				taskCache[taskId] = null;
				return task;
			}
		}
		return taskEnum.getInstance();
	}

	/**
	 * Returns a singleton task to the cache
	 * <p/>
	 * Tasks should be returned to the cache after usage
	 * @param task the enum of the taskß
	 * @return an instance of task
	 */
	public void returnSingletonTask(ManagementTask task) {
		if (!task.getFuture().isDone()) {
			throw new IllegalArgumentException("Tasks with active futures should not be returned to the cache");
		}
		Thread current = Thread.currentThread();
		if (current instanceof AsyncExecutor) {
			AsyncExecutor executor = (AsyncExecutor) current;
			ManagementTaskEnum e = task.getEnum();
			int taskId = e.getId();
			ManagementTask[] taskCache = executor.getManager().singletonCache;
			taskCache[taskId] = task;
		}
	}

	/**
	 * Sets this AsyncManager as manager for a given object
	 * @param managed the object to give responsibility for
	 */
	public final void addManaged(Managed managed) {
		synchronized (managedSet) {
			managedSet.put(managed, Boolean.TRUE);
		}
	}

	/**
	 * Gets the associated AsyncExecutor
	 * @return the executor
	 */
	public final AsyncExecutor getExecutor() {
		return executor;
	}

	/**
	 * This method is called directly before preSnapshot is called
	 */
	public abstract void finalizeRun() throws InterruptedException;

	/**
	 * This method is called directly before copySnapshotRun and is a MONITOR
	 * ONLY stage and no updates should be performed.<br>
	 * <br>
	 * It occurs after the finalize stage and before the copy snapshot stage.
	 */
	public abstract void preSnapshotRun() throws InterruptedException;

	/**
	 * This method is called in order to update the snapshot at the end of each
	 * tick
	 */
	public abstract void copySnapshotRun() throws InterruptedException;

	/**
	 * This method is called in order to start a new tick
	 * @param delta the time since the last tick
	 */
	public abstract void startTickRun(int stage, long delta) throws InterruptedException;

	/**
	 * This method is called when the associated executor is halted and occurs
	 * right after the copySnapshotRun() method call.
	 * <p/>
	 * This method is not called if the executor is halted before being started.
	 */
	public abstract void haltRun() throws InterruptedException;

	/**
	 * Gets the number of stages this manager requires per tick
	 * @return the number of stages
	 */
	public final int getStages() {
		return maxStage;
	}
}
