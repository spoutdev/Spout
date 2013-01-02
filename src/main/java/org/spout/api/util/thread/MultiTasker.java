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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Uses multiple threads to process tasks asynchronously<br>
 * Once started, it runs forever, and needs to be stopped manually
 * 
 * @param <T>
 */
public class MultiTasker<T extends Runnable> {
	private final AtomicInteger size = new AtomicInteger();
	private final BlockingQueue <T> queue = new LinkedBlockingQueue <T>();
	private final WorkerThread<?>[] threads;
	private final String rootThreadName;
	private volatile boolean active = false;
	private volatile boolean finish = false;

	/**
	 * Constructs a new MultiTasker with the specified amount of threads to handle incoming tasks
	 * 
	 * @param threadCount to use
	 */
	public MultiTasker(int threadCount) {
		if (threadCount <= 0) {
			throw new IllegalArgumentException("Amount of threads can not be lower than 1");
		}
		// Create the threads that will process the tasks
		this.threads = new WorkerThread[threadCount];
		this.rootThreadName = getClass().getSimpleName() + "_Thread";
		for (int i = 0; i < this.threads.length; i++) {
			this.threads[i] = new WorkerThread<T>(this, this.rootThreadName + (i + 1));
		}
	}

	/**
	 * Called (from another thread) when a task has been handled
	 * 
	 * @param task that is handled
	 * @param remainingTasks to be handled
	 */
	protected void onHandled(T task, int remainingTasks) {
	}

	/**
	 * Adds a single task to be scheduled
	 * 
	 * @param task to add
	 */
	public void addTask(T task) {
		if (task == null) {
			throw new IllegalArgumentException("Task can not be null");
		}
		try {
			// First increment the size to avoid different size and queue size 
			this.size.incrementAndGet();
			this.queue.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
			// Undo size change
			this.size.decrementAndGet();
		}
	}

	/**
	 * Gets the remaining amount of tasks that still has to be handled
	 * 
	 * @return Remaining task count
	 */
	public int getRemaining() {
		return this.size.get();
	}

	/**
	 * Gets whether tasks are scheduled
	 * 
	 * @return True if there are tasks, False if not
	 */
	public boolean hasTasks() {
		return this.getRemaining() > 0;
	}

	/**
	 * Tests whether threads are ready to handle tasks
	 * 
	 * @return True if threads are running or are ready, False if not
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Starts all the threads to begin processing
	 */
	public void start() {
		// Ignore this method if already running
		if (this.active) {
			return;
		}
		this.active = true;
		this.finish = false;
		for (int i = 0; i < this.threads.length; i++) {
			if (this.threads[i].isAlive()) {
				// Create a new thread object, interrupt the old one to stop it
				this.threads[i].interrupt();
				this.threads[i] = new WorkerThread<T>(this, this.rootThreadName + (i + 1));
			}
			try {
				this.threads[i].start();
			} catch (IllegalThreadStateException ex) {
				// The thread was not alive yet failed to start. It started right 
				// after the isAlive check, and we can assume that the thread was
				// already started in a previous call to start()
			}
		}
	}

	/**
	 * Tells all threads to finish all remaining tasks in the queue, does not instantly abort<br>
	 * Use the {@link join()} method to wait until processing of all the tasks has finished<br>
	 * <br>
	 * To only finish the current tasks and not the remaining ones in the queue, use the {@link stop()} method instead
	 */
	public void finish() {
		this.finish = true;
		this.stop();
	}

	/**
	 * Tells all threads to finish processing their current tasks, does not instantly abort<br>
	 * Use the {@link join()} method to wait until processing of current tasks has finished<br>
	 * <br>
	 * To finish processing all remaining tasks and then stop, use the {@link finish()} method instead
	 */
	public void stop() {
		this.active = false;
		// Interrupt all threads to stop processing
		for (int i = 0; i < this.threads.length; i++) {
			this.threads[i].interrupt();
		}
	}

	/**
	 * Waits until all threads finished processing their current tasks
	 * 
	 * @throws InterruptedException
	 */
	public void join() throws InterruptedException {
		for (int i = 0; i < this.threads.length; i++) {
			this.threads[i].join();
		}
	}

	private static class WorkerThread<T extends Runnable> extends Thread {
		private final MultiTasker<T> owner;
		private int remaining;

		public WorkerThread(MultiTasker<T> owner, String name) {
			super(name);
			this.owner = owner;
		}

		private void handleTask(T task) {
			remaining = owner.size.decrementAndGet();
			// Handle the task
			task.run();
			owner.onHandled(task, remaining);
		}

		@Override
		public void run() {
			T next;
			while (!interrupted()) {
				try {
					next = owner.queue.take();
				} catch (InterruptedException ex) {
					break;
				}
				handleTask(next);
			}
			// Finish all the other tasks?
			while (owner.finish && (next = owner.queue.poll()) != null) {
				handleTask(next);
			}
		}
	}
}