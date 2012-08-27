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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.spout.api.util.list.concurrent.ConcurrentLongPriorityQueue;
import org.spout.api.util.list.concurrent.RedirectableConcurrentLinkedQueue;

public class TaskPriorityQueue extends ConcurrentLongPriorityQueue<SpoutTask> {

	private static final long serialVersionUID = 1L;
	
	private final Thread taskThread;

	public TaskPriorityQueue(long resolution) {
		this(Thread.currentThread(), resolution);
	}
	
	public TaskPriorityQueue(Thread t, long resolution) {
		super(resolution);
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
	public Queue<SpoutTask> getPendingTask(long currentTime) {
		if (Thread.currentThread() != taskThread) {
			throw new IllegalStateException("getPendingTask() may only be called from the thread that created the TaskPriorityQueue");
		}
		
		return super.poll(currentTime);
	}
	
	@Override
	public boolean add(SpoutTask task) {
		if (task != null) {
			if (!task.setQueued()) {
				throw new UnsupportedOperationException("Task was dead when adding to the queue");
			}
		}
		return super.add(task);
	}

	@Override
	public boolean remove(SpoutTask task) {
		task.remove();
		if (!super.remove(task)) {
			return false;
		}

		task.setUnqueued();

		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (SpoutTask t : getTasks()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append("{" + t.getTaskId() + ":" + t.getNextCallTime() + "}");
		}
		return sb.append("}").toString();
	}
	
	public List<SpoutTask> getTasks() {
		List<SpoutTask> list = new ArrayList<SpoutTask>();
		Iterator<RedirectableConcurrentLinkedQueue<SpoutTask>> iq = queueMap.values().iterator();
		while (iq.hasNext()) {
			Iterator<SpoutTask> i = iq.next().iterator();
			while (i.hasNext()) {
				SpoutTask t = i.next();
				list.add(t);
			}
		}
		return list;
	}
}

