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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.spout.api.plugin.Plugin;
import org.spout.api.util.thread.Threadsafe;

public interface Scheduler {
	/**
	 * Schedules a once off task to occur after a delay This task will be
	 * executed by the main server thread
	 */
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, long delay);

	/**
	 * Schedules a once off task to occur as soon as possible This task will be
	 * executed by the main server thread
	 */
	public int scheduleSyncDelayedTask(Object plugin, Runnable task);

	/**
	 * Schedules a repeating task This task will be executed by the main server
	 * thread
	 */
	public int scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period);

	/**
	 * Schedules a once off task to occur after a delay This task will be
	 * executed by a thread managed by the scheduler
	 */
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay);

	/**
	 * Schedules a once off task to occur as soon as possible This task will be
	 * executed by a thread managed by the scheduler
	 */
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task);

	/**
	 * Schedules a repeating task This task will be executed by a thread managed
	 * by the scheduler
	 */
	public int scheduleAsyncRepeatingTask(Object plugin, Runnable task, long delay, long period);

	/**
	 * Calls a method on the main thread and returns a Future object This task
	 * will be executed by the main server thread <br/>
	 *
	 * <b>Note:</b> The Future.get() methods must NOT be called from the main
	 * thread<br/>
	 * <b>Note 2:</b> There is at least an average of 10ms latency until the
	 * isDone() method returns true<br/>
	 *
	 * @return Future Future object related to the task
	 */
	public <T> Future<T> callSyncMethod(Object plugin, Callable<T> task);

	/**
	 * Removes task from scheduler
	 */
	public void cancelTask(int taskId);

	/**
	 * Removes all tasks associated with a particular object from the scheduler
	 */
	public void cancelTasks(Object plugin);

	/**
	 * Removes all tasks from the scheduler
	 */
	public void cancelAllTasks();

	/**
	 * Returns a list of all active workers.
	 *
	 * This list contains asynch tasks that are being executed by separate
	 * threads.
	 *
	 * @return Active workers
	 */
	public List<Worker> getActiveWorkers();

	/**
	 * Returns a list of all pending tasks. The ordering of the tasks is not
	 * related to their order of execution.
	 *
	 * @return Active workers
	 */
	public List<Task> getPendingTasks();

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
}
