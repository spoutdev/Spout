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
package org.spout.server.scheduler;

import org.spout.api.plugin.Plugin;
import org.spout.api.scheduler.Task;

/**
 * Represents a task which is executed periodically.
 *
 */
public class SpoutTask implements Task {
	/**
	 * The next task ID pending.
	 */
	private static Integer nextTaskId = 0;

	/**
	 * A lock to use when getting the next task ID.
	 */
	private final static Object nextTaskIdLock = new Object();

	/**
	 * The ID of this task.
	 */
	private final int taskId;

	/**
	 * The Runnable this task is representing.
	 */
	private final Runnable task;

	/**
	 * The Plugin that owns this task
	 */
	private final Plugin owner;

	/**
	 * The number of ticks before the call to the Runnable.
	 */
	private final long delay;

	/**
	 * The number of ticks between each call to the Runnable.
	 */
	private final long period;

	/**
	 * The current number of ticks since last initialization.
	 */
	private long counter;

	/**
	 * A flag which indicates if this task is running.
	 */
	private boolean running = true;

	private final boolean sync;

	/**
	 * Creates a new task with the specified number of ticks between consecutive
	 * calls to {@link #execute()}.
	 *
	 * @param ticks The number of ticks.
	 */
	public SpoutTask(Plugin owner, Runnable task, boolean sync, long delay, long period) {
		synchronized (nextTaskIdLock) {
			taskId = nextTaskId++;
		}
		this.owner = owner;
		this.task = task;
		this.delay = delay;
		this.period = period;
		counter = 0;
		this.sync = sync;
	}

	/**
	 * Gets the ID of this task.
	 */
	@Override
	public int getTaskId() {
		return taskId;
	}

	@Override
	public boolean isSync() {
		return sync;
	}

	@Override
	public Plugin getOwner() {
		return owner;
	}

	/**
	 * Stops this task.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Called every 'pulse' which is around 200ms in Minecraft. This method
	 * updates the counters and calls {@link #execute()} if necessary.
	 *
	 * @return The {@link #isRunning()} flag.
	 */
	boolean pulse() {
		if (!running) {
			return false;
		}

		++counter;
		if (counter >= delay) {
			if (period == -1) {
				task.run();
				running = false;
			} else if ((counter - delay) % period == 0) {
				task.run();
			}
		}

		return running;
	}
	
	public String toString() {
		Plugin owner = this.getOwner();
		String ownerName = owner == null ? "null" : owner.getDescription().getName();
		return this.getClass().getSimpleName() + "{" + this.getTaskId() + ", " + ownerName + "}";
	}
}
