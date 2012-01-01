package org.getspout.server.util.thread;

import java.io.Serializable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.getspout.server.util.thread.future.ManagedFuture;

public interface AsyncExecutor {
	
	/**
	 * Sets the AsyncManager for this executor.
	 * 
	 * This method may only be called once
	 * 
	 * @param manager the manager
	 */
	public void setManager(AsyncManager manager);
	
	/**
	 * Gets the AsyncManager for this executor.
	 * 
	 * @return the manager
	 */
	public AsyncManager getManager();
	
	/**
	 * Adds a task to this executor's queue
	 *
	 * @param task the runnable to execute
	 */
	public Future<?> addToQueue(ManagementTask task) throws InterruptedException;
	
	/**
	 * Waits until a future is done.
	 * 
     * This method should be called instead of waiting on the future directly.
     * 
     * It will wait execute other tasks on the queue while waiting.
	 * 
	 * @param future the future
	 */
	public void waitForFuture(ManagedFuture<Serializable> future) throws InterruptedException;
	
	/**
	 * Instructs the executor to copy all updated data to its snapshot
	 *
	 * @return false if the executor was active
	 */
	public boolean copySnapshot();

	/**
	 * Instructs the executor to start a new tick or stage.
	 * 
	 * The first stage in a tick is stage zero
	 *
	 * @param stage the stage number for the tick
	 * @param delta the time since the last tick in ms (only relevant for stage 0)
	 * @return false if the executor was active
	 */
	public boolean startTick(int stage, long delta);
	
	/**
	 * Instructs the executor to complete all pending tasks and then shutdown
	 * 
	 * @return false if the executor was active
	 */
	public boolean kill();
	
	/**
	 * Instructs the executor to complete all pending tasks and then throws an InterruptedException.
	 * 
	 * This method is internally called by the executor as a response to the kill() instruction.
	 */
	public void syncKill() throws InterruptedException;

	/**
	 * Returns if this executor has completed its pulse and all submitted tasks
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
	 * Puts the current thread to sleep until the current pulse operation has completed
	 *
	 * @param millis the time in milliseconds to wait before throwing a TimeoutException
	 */

	public void pulseJoin(long millis) throws InterruptedException, TimeoutException;

	/**
	 * Prevents this executor from being woken up.
	 *
	 * This functionality is implemented using a counter, so every call to
	 * disableWake must be matched by a call to enableWake.
	 */
	public void disableWake();

	/**
	 * Allows this executor to be woken up.
	 *
	 * This functionality is implemented using a counter, so every call to
	 * enableWake must be matched by a call to disableWake.
	 */
	public void enableWake();
	
	/**
	 * Starts the executor.  An executor may only be started once.
	 * 
	 * @return false if the executor was already started
	 */
	public boolean startExecutor();
	
	/**
	 * Halts the executor.  An executor may only be halted once.
	 * 
	 * @return false if the executor was already halted
	 */
	public boolean haltExecutor();
}
