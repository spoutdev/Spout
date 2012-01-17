/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.server.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StorageQueue extends Thread {
	private final BlockingQueue<StorageOperation> pending = new LinkedBlockingQueue<StorageOperation>();
	private final List<ParallelTaskThread> active = new ArrayList<ParallelTaskThread>();
	private boolean running = false;

	@Override
	public void run() {
		StorageOperation op;
		while (!isInterrupted()) {
			try {
				if ((op = pending.take()) != null) {
					op.run();
				}
			} catch (InterruptedException e) {
				break;
			}
		}
		running = false;
	}

	public synchronized void queue(StorageOperation op) {
		if (!running) {
			throw new IllegalStateException("Cannot queue tasks while thread is not running");
		}
		if (op.isParallel()) {
			synchronized (active) {
				ParallelTaskThread thread = new ParallelTaskThread(op);
				if (!active.contains(thread)) {
					thread.start();
				} else if (op.queueMultiple()) {
					active.get(active.indexOf(thread)).addOperation(op);
				}
			}
		} else {
			synchronized (pending) {
				if (op.queueMultiple() || !pending.contains(op)) {
					pending.add(op);
				}
			}
		}
	}

	public void end() {
		// TODO - is this a good plan?  Write operations shouldn't be cancelled on exit
		interrupt();
		running = false;
		pending.clear();
		synchronized (active) {
			for (ParallelTaskThread thread : active) {
				if (thread != null) {
					thread.interrupt();
					try {
						thread.join(500);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	@Override
	public void start() {
		running = true;
		super.start();
	}

	class ParallelTaskThread extends Thread {
		private final Queue<StorageOperation> ops = new LinkedList<StorageOperation>();

		public ParallelTaskThread(StorageOperation op) {
			setDaemon(false);
			ops.add(op);
		}

		@Override
		public void run() {
			active.add(this);
			try {
				StorageOperation op;
				while (!isInterrupted() && (op = ops.poll()) != null) {
					op.run();
				}
			} finally {
				active.remove(this);
			}
		}

		public void addOperation(StorageOperation op) {
			if (!isAlive() || isInterrupted()) {
				throw new IllegalStateException("Thread is not running");
			}
			ops.offer(op);
		}

		@Override
		public synchronized boolean equals(Object other) {
			if (!(other instanceof ParallelTaskThread)) {
				return false;
			}
			StorageOperation op = ops.peek();
			return op != null && op.equals(((ParallelTaskThread) other).ops.peek());
		}
	}
}
