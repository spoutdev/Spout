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
package org.spout.api.chat.completion;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.Validate;
import org.spout.api.event.object.Eventable;
import org.spout.api.event.object.EventableBase;
import org.spout.api.event.object.EventableListener;
import org.spout.api.event.object.ObjectEvent;

/**
 * Represents a completion that is in process which may need to be sent over the network for a response
 */
public class CompletionFuture extends ObjectEvent<CompletionFuture> implements Future<CompletionResponse>, Eventable<CompletionFuture, CompletionFuture> {
	private volatile boolean cancelled;
	private final Eventable<CompletionFuture, CompletionFuture> eventableBase = new EventableBase<CompletionFuture, CompletionFuture>();
	protected final AtomicReference<CompletionResponse> result = new AtomicReference<CompletionResponse>();

	public CompletionFuture() {
		super(null);
	}

	@Override
	public CompletionFuture getAssociatedObject() {
		return this;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (isCancelled() || isDone() || !mayInterruptIfRunning) {
			return false;
		}
		cancelled = true;
		return true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return cancelled || result != null;
	}

	@Override
	public CompletionResponse get() throws InterruptedException, ExecutionException {
		if (cancelled) {
			return result.get();
		}
		while (result.get() == null) {
			wait();
		}
		return result.get();
	}

	@Override
	public CompletionResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (cancelled) {
			return result.get();
		}
		timeout = unit.toMillis(timeout);
		long startTime = System.currentTimeMillis();
		while (result.get() == null && System.currentTimeMillis() - timeout < startTime) { // Not completely exact, but should be close enough unless the server is running stupidly slowly.
			wait(timeout);
		}

		return result.get();
	}

	/**
	 * Returns the response to this future. Will be null if the future is not yet completed.
	 *
	 * @return The response, or null if there is no response.
	 */
	public CompletionResponse getImmediately() {
		return result.get();
	}

	/**
	 * Complete the future with a response. This method will set the result, call
	 * If this future already has a response, this method will do nothing.
	 *
	 * @param result The result to set
	 * @return Whether the result was actually set.
	 */
	public boolean complete(CompletionResponse result) {
		Validate.notNull(result);
		if (this.result.compareAndSet(null, result)) {
			callEvent(this);
			unregisterAllListeners();
			return true;
		}
		return false;
	}

	/**
	 * Register a listener to be called on completion of this future. The result of get() is guaranteed to return immediately.
	 *
	 * @param listener The listener to register.
	 */
	@Override
	public void registerListener(EventableListener<CompletionFuture> listener) {
		eventableBase.registerListener(listener);
	}

	@Override
	public void unregisterAllListeners() {
		eventableBase.unregisterAllListeners();
	}

	@Override
	public void unregisterListener(EventableListener<CompletionFuture> listener) {
		eventableBase.unregisterListener(listener);
	}

	@Override
	public void callEvent(CompletionFuture event) {
		if (event != this) {
			throw new IllegalArgumentException("event must be the same ChannelFuture");
		}
		eventableBase.callEvent(event);
	}
}
