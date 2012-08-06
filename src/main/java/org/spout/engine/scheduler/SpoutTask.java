/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.geo.cuboid.Region;
import org.spout.api.scheduler.ParallelRunnable;
import org.spout.api.scheduler.Scheduler;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.util.Named;
import org.spout.api.util.list.concurrent.LongPrioritized;
import org.spout.engine.scheduler.parallel.ParallelTaskInfo;
import org.spout.engine.world.SpoutRegion;

/**
 * Represents a task which is executed periodically.
 */
public class SpoutTask implements Task, LongPrioritized {
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
	private final AtomicReference<QueueState> queueState = new AtomicReference<QueueState>(QueueState.UNQUEUED);
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
	 * Info about sub-tasks
	 */
	private ParallelTaskInfo parallelInfo;
	
	/**
	 * Creates a new task with the specified number of ticks between consecutive
	 * calls to {@link #execute()}.
	 * @param ticks The number of ticks.
	 */
	public SpoutTask(TaskManager manager, Scheduler scheduler, Object owner, Runnable task, boolean sync, long delay, long period, TaskPriority priority) {
		this.taskId = nextTaskId.getAndIncrement();
		this.nextCallTime = new AtomicLong(manager.getUpTime() + delay);
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
	 * @param region the region
	 * @return the new task instance
	 */
	public SpoutTask getRegionTask(SpoutRegion region) {
		Runnable runnable = task;
		if (runnable instanceof ParallelRunnable) {
			runnable = ((ParallelRunnable) runnable).newInstance(region, this);
		}

		return new SpoutTask(region.getTaskManager(), scheduler, owner, runnable, sync, delay, period, priority);
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
		return !queueState.get().isDead();
	}
	
	public long getNextCallTime() {
		return nextCallTime.get();
	}
	
	protected long getPeriod() {
		return this.period;
	}
	
	protected long getDelay() {
		return this.delay;
	}

	/**
	 * Stops this task.
	 */
	public void stop() {
		remove();
	}

	/**
	 * Executes the task.  The task will fail to execute if it is no longer running, if it is called early, or if it is already executing.
	 * 
	 * @return The task successfully executed.
	 */
	boolean pulse() {
		if (queueState.get().isDead()) {
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
				queueState.set(QueueState.DEAD);
			}
		} finally {
			executing.set(false);
		}

		return true;
	}
	
	public void remove() {
		queueState.set(QueueState.DEAD);
	}
	
	public boolean setQueued() {
		if (!queueState.compareAndSet(QueueState.UNQUEUED, QueueState.QUEUED)) {
			boolean success = false;
			while (!success) {
				QueueState oldState = queueState.get();
				switch (oldState) {
					case DEAD: 
						return false;
					case QUEUED: 
						throw new IllegalStateException("Task added in the queue twice without being removed");
					case UNQUEUED:
						success = queueState.compareAndSet(QueueState.UNQUEUED, QueueState.QUEUED); 
						break;
					default:
						throw new IllegalStateException("Unknown queue state " + oldState);
				}
			}
		}
		return true;
	}
	
	public boolean setUnqueued() {
		if (!queueState.compareAndSet(QueueState.QUEUED, QueueState.UNQUEUED)) {
			boolean success = false;
			while (!success) {
				QueueState oldState = queueState.get();
				switch (oldState) {
					case DEAD: 
						return false;
					case UNQUEUED: 
						throw new IllegalStateException("Task set as unqueued before being set as queued");
					case QUEUED:
						success = queueState.compareAndSet(QueueState.QUEUED, QueueState.UNQUEUED); 
						break;
					default:
						throw new IllegalStateException("Unknown queue state " + oldState);
				}
			}
		}
		return true;
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
		}
		if (deferBegin < 0) {
			deferBegin = manager.getUpTime();
			return true;
		}

		if (manager.getUpTime() - deferBegin > priority.getMaxDeferred()) {
			deferBegin = -1;
			return false;
		}

		return true;
	}

	
	private void updateCallTime() {
		updateCallTime(period);
	}
	
	private boolean updateCallTime(long offset) {
		boolean success = setQueued();
		if (!success) {
			return false;
		}
		try {
			long now = manager.getUpTime();
			if (nextCallTime.addAndGet(offset) <= now) {
				nextCallTime.set(now + 1);
			}		
		} finally {
			setUnqueued();
		}
		return true;
	}

	@Override
	public Task getChildTask(Region region) {
		if (parallelInfo == null) {
			throw new UnsupportedOperationException("This methods is only supported for parent parallel tasks");
		}

		return parallelInfo.getTask(region);
	}
	
	public void setParallelInfo(ParallelTaskInfo info) {
		this.parallelInfo = info;
	}

	@Override
	public long getPriority() {
		return nextCallTime.get();
	}
	
	private static enum QueueState {
		QUEUED, UNQUEUED, DEAD;
		
		public boolean isDead() {
			return this == DEAD;
		}
		
		public boolean isQueued() {
			return this == QUEUED;
		}
		
		public boolean isUnQueued() {
			return this == UNQUEUED;
		}
	}

}
