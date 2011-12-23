package org.getspout.api.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.getspout.api.plugin.Plugin;

public interface Scheduler {

	/**
	 * Schedules a once off task to occur after a delay This task will be
	 * executed by the main server thread
	 */
	public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay);

	/**
	 * Schedules a once off task to occur as soon as possible This task will be
	 * executed by the main server thread
	 */
	public int scheduleSyncDelayedTask(Plugin plugin, Runnable task);

	/**
	 * Schedules a repeating task This task will be executed by the main server
	 * thread
	 */
	public int scheduleSyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period);

	/**
	 * Schedules a once off task to occur after a delay This task will be
	 * executed by a thread managed by the scheduler
	 */
	public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task, long delay);

	/**
	 * Schedules a once off task to occur as soon as possible This task will be
	 * executed by a thread managed by the scheduler
	 */
	public int scheduleAsyncDelayedTask(Plugin plugin, Runnable task);

	/**
	 * Schedules a repeating task This task will be executed by a thread managed
	 * by the scheduler
	 */
	public int scheduleAsyncRepeatingTask(Plugin plugin, Runnable task, long delay, long period);

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
	public <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> task);

	/**
	 * Removes task from scheduler
	 */
	public void cancelTask(int taskId);

	/**
	 * Removes all tasks associated with a particular plugin from the scheduler
	 */
	public void cancelTasks(Plugin plugin);

	/**
	 * Removes all tasks from the scheduler
	 */
	public void cancelAllTasks();

	/**
	 * Returns a list of all active workers.
	 *
	 * This list contains asynch tasks that are being executed by separate threads.
	 *
	 * @return Active workers
	 */
	public List<Worker> getActiveWorkers();

	/**
	 * Returns a list of all pending tasks. The ordering of the tasks is not related to their order of execution.
	 *
	 * @return Active workers
	 */
	public List<Task> getPendingTasks();
}
