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

import org.spout.api.Engine;
import org.spout.api.scheduler.Scheduler;
import org.spout.engine.SpoutEngine;
import org.spout.engine.scheduler.SpoutScheduler;

public abstract class AsyncManager {
	private final int maxStage;
	private final Engine engine; // null means that this AsyncManager is the Server
	private final AsyncExecutor executor;

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
		if (engine != null) {
			return engine;
		}

		if (!(this instanceof Engine)) {
			throw new IllegalStateException("Only the Engine object itself should have a null engine reference");
		}

		return (Engine) this;
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
	 * This method is called to execute physics for blocks local to the Region.  
	 * It might be called multiple times per tick
	 * @param sequence -1 for local, 0 - 26 for which sequence
	 * @throws InterruptedException
	 */
	public abstract void runPhysics(int sequence) throws InterruptedException;
	
	/**
	 * This method is called to execute dynamic updates for blocks in the Region.  
	 * It might be called multiple times per tick, the sequence number indicates
	 * which lists to check
	 * 
	 * @param sequence -1 for local, 0 - 26 for which sequence
	 * @param time the time to use for the updates
	 * @throws InterruptedException
	 */
	public abstract void runDynamicUpdates(long time, int sequence) throws InterruptedException;
	
	/**
	 * This method is called to update lighting. 
	 * It might be called multiple times per tick
	 * @param sequence -1 for local, 0 - 26 for which sequence
	 * @throws InterruptedException
	 */
	public abstract void runLighting(int sequence) throws InterruptedException;

	/**
	 * Gets the sequence number associated with this manager
	 * 
	 * @return the sequence number, of -1 for none
	 */
	public int getSequence() {
		return -1;
	}
	
	/**
	 * This method is called to determine the earliest available dynamic update time
	 * 
	 * @return the earliest pending dynamic block update
	 */
	public abstract long getFirstDynamicUpdateTime();

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
