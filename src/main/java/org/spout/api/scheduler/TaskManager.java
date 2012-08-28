/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import org.spout.api.util.thread.Threadsafe;

public interface TaskManager {
	/**
	 * Schedules a once off task to occur as soon as possible This task will be
	 * executed by the main server thread.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @return the task id of the task
	 */
	public int scheduleSyncDelayedTask(Object plugin, Runnable task);
	/**
	 * Schedules a once off task to occur as soon as possible This task will be
	 * executed by the main server thread.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param priority the priority of the task
	 * @return the task id of the task
	 */
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, TaskPriority priority);
	
	/**
	 * Schedules a once off task to occur after a delay.   This task will be
	 * executed by the main server thread
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @param priority the priority of the task
	 * @return the task id of the task
	 */
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority);

	/**
	 * Schedules a repeating task This task will be executed by the main server
	 * thread.  The repeat will not be started if the task until the previous repeat 
	 * has completed running.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @param period the repeat period, in ms, of the task, or <= 0 to indicate a single shot task
	 * @param priority the priority of the task
	 * @return the task id of the task
	 */
	public int scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period, TaskPriority priority);

	/**
	 * Schedules a once off task to occur as soon as possible.  This task will be
	 * executed by a thread managed by the scheduler
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @return the task id of the task
	 */
	public int scheduleAsyncTask(Object plugin, Runnable task);
	
	/**
	 * Schedules a once off task to occur after a delay.  This task will be
	 * executed by a thread managed by the scheduler.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @param priority the priority of the task
	 * @return the task id of the task
	 */
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay, TaskPriority priority);

	/**
	 * Schedules a repeating task This task will be executed by a thread managed
	 * by the scheduler.  The repeat will not be started if the task until the previous repeat 
	 * has completed running.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @param period the repeat period, in ms, of the task, or <= 0 to indicate a single shot task
	 * @param priority the priority of the task
	 * @return the task id of the task
	 */
	public int scheduleAsyncRepeatingTask(Object plugin, Runnable task, long delay, long period, TaskPriority priority);

	/**
	 * Calls a method on the main thread and returns a Future object This task
	 * will be executed by the main server thread <br/>
	 *
	 * <b>Note:</b> The Future.get() methods must NOT be called from the main
	 * thread<br/>
	 * <b>Note 2:</b> There is at least an average of 10ms latency until the
	 * isDone() method returns true<br/>
	 *
	 * @param plugin the owner of the task
	 * @param task the Callable to execute
	 * @param priority the priority of the task
	 * @return Future Future object related to the task
	 */
	public <T> Future<T> callSyncMethod(Object plugin, Callable<T> task, TaskPriority priority);

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
	 * Gets the up time for the scheduler.  This is the time since server started for the main schedulers and the age of the world for the Region based schedulers.<br>
	 * <br>
	 * It is updated once per tick.
	 * 
	 * @return the up time in milliseconds
	 */
	@Threadsafe
	public long getUpTime();

}
