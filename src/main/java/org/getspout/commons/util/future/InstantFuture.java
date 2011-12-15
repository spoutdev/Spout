package org.getspout.commons.util.future;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a Future object that doesn't need syncing.
 * It is used when the result is known at creation time.
 */

public class InstantFuture<T> implements Future<T> {
	
	private final T result;
	
	public InstantFuture(T result) {
		this.result = result;
	}

	/**
	 * Attempt to cancel the task.
	 * 
	 * This can't be done, since the result is known at construction time.
	 * 
	 * @param mayInterrupt true if the task may be interrupted even if started
	 * @return returns true if the task was successfully cancelled
	 */
	public boolean cancel(boolean mayInterrupt) {
		return false;
	}

	/**
	 * Gets the result and waits if required.
	 * 
	 * This never causes a wait, since the result is known at construction time.
	 * 
	 * @return returns the result
	 */
	public T get() {
		return result;
	}

	/**
	 * Gets the result and waits, up to the timeout, if required.
	 * 
	 * This never causes a wait, since the result is known at construction time.
	 * 
	 * @return returns the result
	 */
	public T get(long timeout, TimeUnit units) {
		return get();
	}

	/**
	 * Indicates if the task was cancelled.
	 * 
	 * This can't happen since the result is known at construction time.
	 * 
	 * @return true if cancelled
	 */
	public boolean isCancelled() {
		return false;
	}

	/**
	 * Indicates if the task is completed.
	 * 
	 * This is always true since the result is known at construction time.
	 * 
	 * @return true if the task is completed
	 */
	public boolean isDone() {
		return true;
	}

}
