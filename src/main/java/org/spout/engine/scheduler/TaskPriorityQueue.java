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

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

public class TaskPriorityQueue extends PriorityBlockingQueue<SpoutTask> {

	private static final long serialVersionUID = 1L;
	
	private final Thread taskThread;

	public TaskPriorityQueue() {
		this(Thread.currentThread());
	}
	
	public TaskPriorityQueue(Thread t) {
		super(20, new Comparator<SpoutTask>() {
			@Override
			public int compare(SpoutTask task1, SpoutTask task2) {
				long diff = task1.getNextCallTime() - task2.getNextCallTime();
				return diff < 0 ? -1 :
					   diff > 0 ? +1 :
						           0;
			}
			
		});
		taskThread = t;
	}
	
	/**
	 * Gets the first pending task on the queue.  A task is considered pending if its next call time is less than or equal to the given current time.<br>
	 * <br>
	 * NOTE: This method should only be called from a single thread.  
	 * 
	 * @param currentTime the current time
	 * @return the first pending task, or null if no task is pending
	 */
	public SpoutTask getPendingTask(long currentTime) {
		if (Thread.currentThread() != taskThread) {
			throw new IllegalStateException("getPendingTask() may only be called from the thread that created the TaskPriorityQueue");
		}
		SpoutTask task = peek();
		if (task == null || task.getNextCallTime() > currentTime) {
			return null;
		} else {
			task = poll();
			if (task.getNextCallTime() > currentTime) {
				add(task);
				return null;
			} else {
				return task;
			}
		}
	}
	
	/**
	 * Indicates if there are any pending tasks on the queue.  A task is considered pending if its next call time is less than or equal to the given current time.<br>
	 * 
	 * @param currentTime the current time
	 * @return true if there are any pending tasks
	 */
	public boolean hasPendingTasks(long currentTime) {
		SpoutTask task = peek();
		if (task == null || task.getNextCallTime() > currentTime) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean add(SpoutTask task) {
		if (task != null) {
			task.lockNextCallTime();
		}
		return super.add(task);
	}
	
	@Override
	public SpoutTask poll() {
		SpoutTask task = super.poll();
		if (task != null) {
			task.unlockNextCallTime();
		}
		return task;
	}
	
	@Override
	public boolean remove(Object task) {
		if (super.remove(task)) {
			if (task instanceof SpoutTask) {
				((SpoutTask)task).unlockNextCallTime();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException("Not supported");
	}
	
	@Override
	public String toString() {
		Iterator<SpoutTask> i = iterator();
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		while (i.hasNext()) {
			SpoutTask t = i.next();
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append("{" + t.getTaskId() + ":" + t.getNextCallTime() + "}");
		}
		return sb.append("}").toString();
	}
}

