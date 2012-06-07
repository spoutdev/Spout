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
package org.spout.api.util.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleFuture<T> implements Future<T> {

	private static Object THROWABLE = new Object();
	private static Object CANCEL = new Object();
	private static Object NULL = new Object();

	private AtomicReference<T> resultRef = new AtomicReference<T>(null);
	private AtomicReference<Throwable> throwable = new AtomicReference<Throwable>(null);

	@SuppressWarnings("unchecked")
	public boolean setThrowable(Throwable t) {
		if (!throwable.compareAndSet(null, t)) {
			return false;
		}

		if (!resultRef.compareAndSet(null, (T)THROWABLE)) {
			return false;
		}

		synchronized (resultRef) {
			resultRef.notifyAll();
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean setResult(T result) {
		if (result == null) {
			result = (T)NULL;
		}
		
		if (!resultRef.compareAndSet(null, result)) {
			return false;
		}
		
		synchronized (resultRef) {
			resultRef.notifyAll();
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return resultRef.compareAndSet(null, (T)CANCEL);
	}

	@Override
	public boolean isCancelled() {
		return resultRef.get() == CANCEL;
	}

	@Override
	public boolean isDone() {
		return resultRef.get() != null;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		try {
			return get(0, TimeUnit.MILLISECONDS);
		} catch (TimeoutException toe) {
			throw new IllegalStateException("Attempting to get with an infinite timeout should not cause a timeout exception", toe);
		}
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean noTimeout = timeout <= 0;

		long timeoutInMS = unit.toMillis(timeout);

		if (!noTimeout && timeoutInMS <= 0) {
			timeoutInMS = 1;
		}
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + timeoutInMS;

		while (noTimeout || currentTime < endTime) {
			synchronized (resultRef) {
				T result = resultRef.get();
				if (result != null) {
					if (result == NULL || result == CANCEL) {
						return null;
					}

					if (result == THROWABLE) {
						Throwable t = throwable.get();
						throw new ExecutionException("Exception occured when trying to retrieve the result of this future", t);
					}

					return result;
				}

				if (noTimeout) {
					resultRef.wait();
				} else {
					resultRef.wait(endTime - currentTime);
				}
			}
			currentTime = System.currentTimeMillis();
		}
		throw new TimeoutException("Wait duration of " + (currentTime - (endTime - timeoutInMS)) + "ms exceeds timeout of " + timeout + unit.toString());
	}
}
