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
package org.spout.api.util.future;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of a Future object that can store one result.
 *
 * This Future is intended for transferring of a result from a source thread to
 * a receiver thread.
 *
 * It should only be reused by the calling thread.
 */

public class SimpleFuture<T> implements Future<T> {
	private final AtomicBoolean done = new AtomicBoolean(false);
	private final AtomicReference<T> result = new AtomicReference<T>();
	private final AtomicInteger waiting = new AtomicInteger(0);

	public SimpleFuture() {
	}

	public SimpleFuture(T result) {
		set(result);
	}

	/**
	 * Gets the waiting monitor for this Future.
	 *
	 * The monitor is notified when the future is set.
	 *
	 * @returns the monitor
	 */
	public Object getMonitor() {
		return waiting;
	}

	/**
	 * Waits until the future's monitor is notified.
	 *
	 * The monitor is notified when the future is set.
	 */
	public void waitForMonitor() throws InterruptedException {
		synchronized (waiting) {
			waiting.incrementAndGet();
			try {
				waiting.wait();
			} finally {
				waiting.decrementAndGet();
			}
		}
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
	 * Sets the result.
	 *
	 * This method should only be called by the thread responsible for answering
	 * the future.
	 *
	 * Calling this method from more than 1 thread is not threadsafe.
	 *
	 * @param the result for the Future
	 */
	public void set(T result) {
		this.result.set(result);
		if (!done.compareAndSet(false, true)) {
			throw new IllegalStateException("A SimpleFuture can not store more than 1 element");
		}
		if (waiting.get() > 0) {
			synchronized (waiting) {
				waiting.notifyAll();
			}
		}
	}

	/**
	 * Gets the result and waits if required.
	 *
	 * The get methods for this class should only be called by a dedicated read
	 * thread
	 *
	 * Calling this method from multiple threads is not threadsafe
	 *
	 * @return returns the result
	 */

	public T get() throws InterruptedException {
		T result = null;
		while (true) {
			if (!done.getAndSet(false)) {
				waitForMonitor();
			} else {
				result = this.result.getAndSet(null);
				return result;
			}
		}
	}

	/**
	 * Gets the result and waits, up to the timeout, if required.
	 *
	 * The get methods for this class should only be called from a single read
	 * thread.
	 *
	 * Calling this method from multiple threads is not threadsafe
	 *
	 * @return returns the result
	 */

	public T get(long timeout, TimeUnit units) throws InterruptedException, TimeoutException {
		if (timeout == 0) {
			return get();
		} else {
			long timeoutMillis = TimeUnit.MILLISECONDS.convert(timeout, units);

			if (timeoutMillis == 0) {
				timeoutMillis = 1;
			}

			long currentTime = System.currentTimeMillis();
			long endTime = currentTime + timeoutMillis;

			T result = null;

			while (endTime > currentTime) {
				if (!done.getAndSet(false)) {
					synchronized (waiting) {
						waiting.incrementAndGet();
						try {
							waiting.wait();
						} finally {
							waiting.decrementAndGet();
						}
					}
				} else {
					result = this.result.getAndSet(null);
					return result;
				}
				currentTime = System.currentTimeMillis();
			}
			throw new TimeoutException("SimpleFuture timed out");
		}
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
		return done.get();
	}
}
