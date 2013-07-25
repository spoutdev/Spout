/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.engine.util.thread;

public interface AsyncManager {
	/**
	 * This method is called directly before preSnapshot is called
	 */
	public void finalizeRun();

	/**
	 * This method is called directly before copySnapshotRun and is a MONITOR ONLY stage and no updates should be performed.<br> <br> It occurs after the finalize stage and before the copy snapshot
	 * stage.
	 */
	public void preSnapshotRun();

	/**
	 * This method is called in order to update the snapshot at the end of each tick
	 */
	public void copySnapshotRun();

	/**
	 * This method is called in order to start a new tick
	 *
	 * @param delta the time since the last tick
	 */
	public void startTickRun(int stage, long delta);

	/**
	 * This method is called to execute physics for blocks local to the Region. It might be called multiple times per tick
	 *
	 * @param sequence -1 for local, 0 - 26 for which sequence
	 */
	public void runPhysics(int sequence);

	/**
	 * This method is called to execute dynamic updates for blocks in the Region. It might be called multiple times per tick, the sequence number indicates which lists to check
	 *
	 * @param sequence -1 for local, 0 - 26 for which sequence
	 */
	public void runDynamicUpdates(long threshold, int sequence);

	/**
	 * This method is called to update lighting. It might be called multiple times per tick
	 *
	 * @param sequence -1 for local, 0 - 26 for which sequence
	 */
	public void runLighting(int sequence);

	/**
	 * Gets the sequence number associated with this manager
	 *
	 * @return the sequence number, of -1 for none
	 */
	public int getSequence();

	/**
	 * This method is called to determine the earliest available dynamic update time
	 *
	 * @return the earliest pending dynamic block update
	 */
	public long getFirstDynamicUpdateTime();

	/**
	 * Gets the execution thread associated with this manager
	 */
	public Thread getExecutionThread();

	/**
	 * Sets the execution thread associated with this manager
	 */
	public void setExecutionThread(Thread t);

	/**
	 * Gets the highest stage for the start tick task
	 */
	public int getMaxStage();
}
