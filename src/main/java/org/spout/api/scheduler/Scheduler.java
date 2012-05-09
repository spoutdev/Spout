/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.scheduler;

import org.spout.api.util.thread.Threadsafe;

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
	public boolean isServerLoaded();
}
