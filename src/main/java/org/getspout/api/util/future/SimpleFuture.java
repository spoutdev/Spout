package org.getspout.api.util.future;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of a Future object that can pass one 
 * It is used when the result is known at creation time.
 */

public class SimpleFuture<T> implements Future<T> {
	
	private final ArrayBlockingQueue<T> result = new ArrayBlockingQueue<T>(1);
	private final AtomicInteger done = new AtomicInteger(0);
	
	public SimpleFuture() {
	}

	/**
	 * Attempt to cancel the task.
	 * 
	 * SimpleFutures can't be cancelled.
	 * 
	 * @param mayInterrupt true if the task may be interrupted even if started
	 * @return returns true if the task was successfully cancelled
	 */
	public boolean cancel(boolean mayInterrupt) {
		return false;
	}
	
	/**
	 * Sets the result
	 * 
	 * @param the result for the Future
	 */
	public void set(T result) {
		if (done.getAndIncrement() != 0) {
			throw new IllegalStateException("Attempting to set a SimpleFuture more than once");
		} else {
			this.result.add(result);
		}
	}

	/**
	 * Gets the result and waits if required.
	 * 
	 * @return returns the result
	 */
	public T get() throws InterruptedException {
		return result.take();
	}

	/**
	 * Gets the result and waits, up to the timeout, if required.
	 * 
	 * @return returns the result
	 */
	public T get(long timeout, TimeUnit units) throws InterruptedException, TimeoutException {
		return result.poll(timeout, units);
	}

	/**
	 * Indicates if the task was cancelled.
	 * 
	 * SimpleFutures can't be cancelled, so this returns false.
	 * 
	 * @return true if cancelled
	 */
	public boolean isCancelled() {
		return false;
	}

	/**
	 * Indicates if the task is completed.
	 * 
	 * @return true if the task is completed
	 */
	public boolean isDone() {
		return done.get() != 0;
	}

}
