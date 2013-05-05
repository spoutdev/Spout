/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.util.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

import org.spout.api.util.future.SimpleFuture;

/**
 * A simple thread pool that uses Daemon threads.  Tasks that are submitted may be 
 * be partially completed when the JVM shuts down.
 */
public class DaemonThreadPool {
	
	private final ArrayBlockingQueue<RunnableFuture<?>> queue;
	
	private final Thread[] threads;
	
	public DaemonThreadPool(String type, int threads, int queueSize) {
		this.queue = new ArrayBlockingQueue<RunnableFuture<?>>(queueSize);
		this.threads = new Thread[threads];
		for (int i = 0; i < threads; i++) {
			this.threads[i] = new DaemonThread(type, i);
		}
	}
	
	public Future<?> add(Runnable r) {
		@SuppressWarnings("rawtypes")
		RunnableFuture<?> future = new RunnableFutureImpl(r);
		if (!queue.offer(future)) {
			future.run();
		}
		return future;
	}
	
	public void interrupt() {
		for (int i = 0; i < threads.length; i++) {
			threads[i].interrupt();
		}
	}
	
	public void waitUntilDone(Future<?> ... futures) {
		boolean interrupted = false;
		for (int i = 0; i < futures.length; i++) {
			if (futures[i] != null) {
				try {
					futures[i].get();
				} catch (InterruptedException ie) {
					interrupted = true;
					i--;
					continue;
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		}
		if (interrupted) {
			Thread.currentThread().interrupt();
		}
	}

	private static class RunnableFutureImpl<T> extends SimpleFuture<T> implements RunnableFuture<T> {

		private final Runnable r;
		
		public RunnableFutureImpl(Runnable r) {
			this.r = r;
		}
		
		@Override
		public void run() {
			try {
				r.run();
			} catch (Throwable t) {
				t.printStackTrace();
				super.setThrowable(t);
				return;
			}
			super.setResult(null);
		}
		
	}
	
	private class DaemonThread extends Thread {
		public DaemonThread(String type, int i) {
			super("DaemonThread{" + type + "}-" + i);
			super.setDaemon(true);
			super.start();
		}
		
		public void run() {
			while (!isInterrupted()) {
				try {
					Runnable r = queue.take();
					r.run();
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

}
