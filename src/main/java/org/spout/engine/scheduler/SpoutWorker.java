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
package org.spout.engine.scheduler;

import org.spout.api.scheduler.Worker;

public class SpoutWorker implements Worker, Runnable {
	private final int id;
	private final Object owner;
	private final SpoutTask task;
	private Thread thread = null;
	private boolean shouldContinue = true;

	protected SpoutWorker(final SpoutTask task, final SpoutScheduler scheduler) {
		id = task.getTaskId();
		owner = task.getOwner();
		this.task = task;
		String name = "Spout Worker{Owner:" + ((owner != null) ? owner.getClass().getName() : "none") + ", id:" + id + "}";
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				task.pulse();
				scheduler.workerComplete(SpoutWorker.this);
			}
		}, name);
		thread.start();
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
	public Thread getThread() {
		return thread;
	}

	public SpoutTask getTask() {
		return task;
	}

	public boolean shouldContinue() {
		return shouldContinue;
	}

	public void cancel() {
		if (thread == null) {
			return;
		}
		if (!thread.isAlive()) {
			thread.interrupt();
			return;
		}
		task.stop();
	}

	@Override
	public void run() {
		shouldContinue = task.pulse();
	}
}
