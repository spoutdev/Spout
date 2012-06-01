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

import java.io.Serializable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.spout.engine.util.thread.future.ManagedFuture;

public interface AsyncExecutor {
	/**
	 * Sets the AsyncManager for this executor.
	 * <p/>
	 * This method may only be called once
	 * @param manager the manager
	 */
	public void setManager(AsyncManager manager);

	/**
	 * Gets the AsyncManager for this executor.
	 * @return the manager
	 */
	public AsyncManager getManager();

	/**
	 * Adds a task to this executor's queue
	 * @param task the runnable to execute
	 */
	public Future<?> addToQueue(ManagementTask task) throws InterruptedException;

	/**
	 * Waits until a future is done.
	 * <p/>
	 * This method should be called instead of waiting on the future directly.
	 * <p/>
	 * It will wait execute other tasks on the queue while waiting.
	 * @param future the future
	 */
	public void waitForFuture(ManagedFuture<Serializable> future) throws InterruptedException;

	/**
	 * This is called as the last stage prior to the snapshot being taken.<br>
	 * <br>
	 * It is not considered part of the stable snapshot.
	 * @return false if the executor was active
	 */
	public boolean finalizeTick();

	/**
	 * This is called after finalizeTick stage and before the copy snapshot
	 * stage.<br>
	 * <br>
	 * This is intended as a MONITOR only stage and is used for sending network
	 * updates.<br>
	 * <br>
	 * All data must remain stable for this stage.
	 * @return false if the executor was active
	 */
	public boolean preSnapshot();

	/**
	 * Instructs the executor to copy all updated data to its snapshot
	 * @return false if the executor was active
	 */
	public boolean copySnapshot();

	/**
	 * Instructs the executor to start a new tick or stage.
	 * <p/>
	 * The first stage in a tick is stage zero
	 * @param stage the stage number for the tick
	 * @param delta the time since the last tick in ms (only relevant for stage
	 *              0)
	 * @return false if the executor was active
	 */
	public boolean startTick(int stage, long delta);

	/**
	 * Instructs the executor to complete all pending tasks and then throws an
	 * InterruptedException.
	 * <p/>
	 * This method is internally called by the executor as a response to the
	 * kill() instruction.
	 */
	public void syncKill() throws InterruptedException;

	/**
	 * Returns if this executor has completed its pulse and all submitted tasks
	 * associated with it
	 * @return true if the pulse was completed
	 */
	public boolean isPulseFinished();

	/**
	 * Puts the current thread to sleep until the current pulse operation has
	 * completed
	 */
	public void pulseJoin() throws InterruptedException;

	/**
	 * Puts the current thread to sleep until the current pulse operation has
	 * completed
	 * @param millis the time in milliseconds to wait before throwing a
	 *               TimeoutException
	 */
	public void pulseJoin(long millis) throws InterruptedException, TimeoutException;

	/**
	 * Prevents this executor from being woken up.
	 * <p/>
	 * This functionality is implemented using a counter, so every call to
	 * disableWake must be matched by a call to enableWake.
	 */
	public void disableWake();

	/**
	 * Allows this executor to be woken up.
	 * <p/>
	 * This functionality is implemented using a counter, so every call to
	 * enableWake must be matched by a call to disableWake.
	 */
	public void enableWake();

	/**
	 * Starts the executor. An executor may only be started once.
	 * @return false if the executor was already started
	 */
	public boolean startExecutor();

	/**
	 * Halts the executor. An executor may only be halted once.
	 * <p/>
	 * Halting happens after the next snapshot copy.
	 * @return false if the executor was already halted
	 */
	public boolean haltExecutor();

	/**
	 * Checks if the executor should halt. This is called after every snapshot
	 * update.
	 */
	public void haltCheck() throws InterruptedException;
}
