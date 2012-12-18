/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.scheduler.Worker;
import org.spout.api.util.future.SimpleFuture;

public class SpoutWorker implements Worker, Runnable {
	@SuppressWarnings("rawtypes")
	private static final Future<?> NOT_SUBMITED = new SimpleFuture();
	@SuppressWarnings("rawtypes")
	private static final Future<?> CANCELLED = new SimpleFuture();
	
	private final int id;
	private final Object owner;
	private final SpoutTask task;
	private final Thread thread;
	private final Runnable r;
	private AtomicReference<Future<?>> futureRef = new AtomicReference<Future<?>>(NOT_SUBMITED);
	private boolean shouldContinue = true;
	private final SpoutTaskManager taskManager;

	protected SpoutWorker(final SpoutTask task, final SpoutTaskManager taskManager) {
		id = task.getTaskId();
		owner = task.getOwner();
		this.task = task;
		String name = "Spout Worker{Owner:" + ((owner != null) ? owner.getClass().getName() : "none") + ", id:" + id + "}";
		r = new Runnable() {
			@Override
			public void run() {
				task.pulse();
				taskManager.removeWorker(SpoutWorker.this, task);
				taskManager.repeatSchedule(task);
			}
		};
		if (task.isLongLived()) {
			thread = new Thread(r, name);
		} else {
			thread = null;
		}
		this.taskManager = taskManager;
	}
	
	public void start(ExecutorService pool) {
		if (thread != null) {
			thread.start();
		} else {
			Future<?> future = pool.submit(r);
			if (!this.futureRef.compareAndSet(NOT_SUBMITED, future)) {
				future.cancel(true);
			}
		}
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public int getTaskId() {
		return id;
	}

	@Override
	public Object getOwner() {
		return owner;
	}

	@Override
	public SpoutTask getTask() {
		return task;
	}

	public boolean shouldContinue() {
		return shouldContinue;
	}

	@Override
	public void cancel() {
		taskManager.cancelTask(task);
	}
	
	public void interrupt() {
		if (thread != null) {
			thread.interrupt();
		} else {
			if (!this.futureRef.compareAndSet(NOT_SUBMITED, CANCELLED)) {
				Future<?> future = futureRef.get();
				future.cancel(true);
			}
		}
	}

	@Override
	public void run() {
		shouldContinue = task.pulse();
	}
}
