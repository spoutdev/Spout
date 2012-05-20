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
	 * Schedules a once off task to occur after a delay.   This task will be
	 * executed by the main server thread
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @return the task id of the task
	 */
	public int scheduleSyncDelayedTask(Object plugin, Runnable task, long delay);

	/**
	 * Schedules a repeating task This task will be executed by the main server
	 * thread.  The repeat will not be started if the task until the previous repeat 
	 * has completed running.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @param period the repeat period, in ms, of the task, or <= 0 to indicate a single shot task
	 * @return the task id of the task
	 */
	public int scheduleSyncRepeatingTask(Object plugin, Runnable task, long delay, long period);

	/**
	 * Schedules a once off task to occur as soon as possible.  This task will be
	 * executed by a thread managed by the scheduler
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @return the task id of the task
	 */
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task);
	
	/**
	 * Schedules a once off task to occur after a delay.  This task will be
	 * executed by a thread managed by the scheduler.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @return the task id of the task
	 */
	public int scheduleAsyncDelayedTask(Object plugin, Runnable task, long delay);

	/**
	 * Schedules a repeating task This task will be executed by a thread managed
	 * by the scheduler.  The repeat will not be started if the task until the previous repeat 
	 * has completed running.
	 * 
	 * @param plugin the owner of the task
	 * @param task the task to execute
	 * @param delay the delay, in ms, before the task starts
	 * @param period the repeat period, in ms, of the task, or <= 0 to indicate a single shot task
	 * @return the task id of the task
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
	 * Gets the up time for the scheduler.  This is the time since server started for the main schedulers and the age of the world for the Region based schedulers.<br>
	 * <br>
	 * It is updated once per tick.
	 * 
	 * @return the up time in milliseconds
	 */
	@Threadsafe
	public long getUpTime();

}
