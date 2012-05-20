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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.Spout;
import org.spout.api.scheduler.ParallelRunnable;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.util.Named;
import org.spout.engine.world.SpoutRegion;

/**
 * Represents a task which is executed periodically.
 */
public class SpoutTask implements Task {
	/**
	 * The next task ID pending.
	 */
	private final static AtomicInteger nextTaskId = new AtomicInteger(0);
	/**
	 * The ID of this task.
	 */
	private final int taskId;
	/**
	 * The task priority
	 */
	private final TaskPriority priority;
	/**
	 * The Runnable this task is representing.
	 */
	private final Runnable task;
	/**
	 * The Plugin that owns this task
	 */
	private final Object owner;
	/**
	 * The number of ticks before the call to the Runnable.
	 */
	private final long delay;
	/**
	 * The number of ticks between each call to the Runnable.
	 */
	private final long period;
	/** 
	 * Indicates if the task is a synchronous task or an async task
	 */
	private final boolean sync;
	/**
	 * Indicates the next scheduled time for the task to be called
	 */
	private final AtomicLong nextCallTime;
	private final AtomicBoolean nextCallTimeLock = new AtomicBoolean(false);
	/**
	 * A flag which indicates if this task is alive.
	 */
	private final AtomicBoolean alive;
	/**
	 * A flag indicating if the task is actually executing
	 */
	private final AtomicBoolean executing;
	
	/**
	 * Indicates if the task is being deferred and when it started
	 */
	private long deferBegin = -1;
	
	/**
	 * The manager associated with this task
	 */
	private final TaskManager manager;
	
	/**
	 * The scheduler for the engine
	 */
	private final Scheduler scheduler;
	
	/**
	 * Creates a new task with the specified number of ticks between consecutive
	 * calls to {@link #execute()}.
	 * @param ticks The number of ticks.
	 */
	public SpoutTask(TaskManager manager, Scheduler scheduler, Object owner, Runnable task, boolean sync, long delay, long period, TaskPriority priority) {
		this.taskId = nextTaskId.getAndIncrement();
		this.nextCallTime = new AtomicLong(manager.getUpTime() + delay);
		this.alive = new AtomicBoolean(true);
		this.executing = new AtomicBoolean(false);
		this.owner = owner;
		this.task = task;
		this.delay = delay;
		this.period = period;
		this.sync = sync;
		this.priority = priority;
		this.manager = manager;
		this.scheduler = scheduler;
	}
	
	/**
	 * Creates a copy of this task for a particular Region
	 * 
	 * @param r the region
	 * @return the new task instance
	 */
	public SpoutTask getRegionTask(SpoutRegion r) {
		if (task instanceof ParallelRunnable) {
			ParallelRunnable newRunnable = ((ParallelRunnable) task).newInstance(r);
			return new SpoutTask(r.getTaskManager(), scheduler, owner, newRunnable, sync, delay, period, priority);
		} else {
			return new SpoutTask(r.getTaskManager(), scheduler, owner, task, sync, delay, period, priority);
		}
	}

	/**
	 * Gets the ID of this task.
	 */
	@Override
	public int getTaskId() {
		return taskId;
	}

	@Override
	public boolean isSync() {
		return sync;
	}
	
	@Override
	public boolean isExecuting() {
		return executing.get();
	}

	@Override
	public Object getOwner() {
		return owner;
	}
	
	@Override
	public boolean isAlive() {
		return alive.get();
	}
	
	public long getNextCallTime() {
		return nextCallTime.get();
	}
	
	protected long getPeriod() {
		return this.period;
	}

	/**
	 * Stops this task.
	 */
	public void stop() {
		alive.set(false);
	}

	/**
	 * Executes the task.  The task will fail to execute if it is no longer running, if it is called early, or if it is already executing.
	 * 
	 * @return The task successfully executed.
	 */
	boolean pulse() {
		if (!alive.get()) {
			return false;
		}

		if (scheduler.isServerLoaded()) {
			if (attemptDefer()) {
				updateCallTime(SpoutScheduler.PULSE_EVERY);
				return false;
			}
		}
		
		if (!executing.compareAndSet(false, true)) {
			return false;
		}

		try {
			task.run();

			updateCallTime();

			if (period <= 0) {
				alive.set(false);
			}
		} finally {
			executing.set(false);
		}

		return true;
	}
	
	public void lockNextCallTime() {
		if (!nextCallTimeLock.compareAndSet(false, true)) {
			throw new IllegalStateException("Task added in the queue twice without being removed");
		}
	}
	
	public void unlockNextCallTime() {
		if (!nextCallTimeLock.compareAndSet(true, false)) {
			throw new IllegalStateException("Task removed from the queue before being added");
		}
	}

	@Override
	public String toString() {
		Object owner = getOwner();
		String ownerName = owner == null || !(owner instanceof Named) ? "null" : ((Named) owner).getName();
		return this.getClass().getSimpleName() + "{" + getTaskId() + ", " + ownerName + "}";
	}
	
	@Override
	public int hashCode() {
		return taskId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof SpoutTask) {
			SpoutTask other = (SpoutTask)o;
			return other.taskId == taskId;
		} else {
			return false;
		}
	}
	
	private boolean attemptDefer() {
		if (priority.getMaxDeferred() <= 0) {
			return false;
		} else if (deferBegin < 0) {
			deferBegin = manager.getUpTime();
			return true;
		} else {
			if (manager.getUpTime() - deferBegin > priority.getMaxDeferred()) {
				deferBegin = -1;
				return false;
			} else {
				return true;
			}
		}
	}

	
	private void updateCallTime() {
		updateCallTime(period);
	}
	
	private void updateCallTime(long offset) {
		if (nextCallTimeLock.compareAndSet(false, true)) {
			long now = manager.getUpTime();
			if (nextCallTime.addAndGet(offset) <= now) {
				nextCallTime.set(now + 1);
			}			
			nextCallTimeLock.set(false);
		} else {
			throw new IllegalStateException("Attempt made to modify next call time when the task was in the queue.");
		}
	}

}
