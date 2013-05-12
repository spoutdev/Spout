/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.scheduler;

import java.util.concurrent.Callable;

import org.spout.api.plugin.Plugin;
import org.spout.api.util.thread.annotation.Threadsafe;

public interface Scheduler extends TaskManager {

	/**
	 * Gets the snapshot lock. This lock can be used by async threads to
	 * readlock stable snapshot data.
	 *
	 * @return the snapshot lock
	 */
	public SnapshotLock getSnapshotLock();
	
	/**
	 * Gets the amount of time since the beginning of the current tick.
	 *
	 * @return the time in ms since the start of the current tick
	 */
	@Threadsafe
	public long getTickTime();

	/**
	 * Gets the amount of time remaining until the tick should end.  A negative time indicates that the tick has gone over the target time.
	 *
	 * @return the time in ms since the start of the current tick
	 */
	@Threadsafe
	public long getRemainingTickTime();
	
	/**
	 * Determines if the server is under heavy load.<br>
	 * <br>
	 * The server is considered under heavy load if the previous tick went over time, or if the current tick has gone over time.
	 *
	 * @return true if the server is under heavy load
	 */
	@Threadsafe
	public boolean isServerOverloaded();
	
	/**
	 * Runs a Runnable during a safe moment in the tick.  This method locks the snapshot lock while running the task.
	 * 
	 * @param plugin
	 * @param task
	 */
	@Threadsafe
	public void safeRun(final Plugin plugin, final Runnable task);
	
	/**
	 * Calls a Callable during a safe moment in the tick.  This method locks the snapshot lock while running the task.
	 * 
	 * @param plugin
	 * @param task
	 * @return
	 */
	@Threadsafe
	public <T> T safeCall(final Plugin plugin, final Callable<T> task);
}
