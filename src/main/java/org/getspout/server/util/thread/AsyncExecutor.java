package org.getspout.server.util.thread;

import java.util.concurrent.TimeoutException;

public interface AsyncExecutor {

	/**
	 * Sets this thread as manager for a given object
	 *
	 * @param managed the object to give responsibility for
	 */
	public void addManaged(Managed managed);

	/**
	 * Adds a task to this thread's queue
	 *
	 * @param task the runnable to execute
	 */
	public void addToQueue(ManagementTask task);

	/**
	 * Adds a task to this thread's queue and wakes it if necessary
	 *
	 * @param task the runnable to execute
	 */
	public void addToQueueAndWake(ManagementTask task);

	/**
	 * Instructs the thread to copy all updated data to its snapshot
	 *
	 * @return false if the thread was already pulsing
	 */
	public boolean copySnapshot();

	/**
	 * Instructs the thread to start a new tick
	 *
	 * @return false if the thread was already pulsing
	 */
	public boolean startTick(long ticks);

	/**
	 * Returns if this thread has completed its pulse and all submitted tasks
	 * associated with it
	 *
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
	 *
	 * @param millis the time in milliseconds to wait before throwing a
	 *            TimeoutException
	 */

	public void pulseJoin(long millis) throws InterruptedException, TimeoutException;

	/**
	 * Prevents this thread from being woken up.
	 *
	 * This functionality is implemented using a counter, so every call to
	 * disableWake must be matched by a call to enableWake.
	 */
	public void disableWake();

	/**
	 * Allows this thread to be woken up.
	 *
	 * This functionality is implemented using a counter, so every call to
	 * enableWake must be matched by a call to disableWake.
	 */
	public void enableWake();

	/**
	 * This method is called once at the end of every tick
	 *
	 * This method should be overridden.
	 */
	public void copySnapshotRun() throws InterruptedException;

	/**
	 * This method is called once at the start of every tick
	 *
	 * This method should be overridden.
	 *
	 * @param tick the number of ticks since server start
	 */
	public void startTickRun(long tick) throws InterruptedException;

}
