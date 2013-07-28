/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.api.util.list.concurrent;

import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentSkipListMap;

import org.spout.api.math.GenericMath;

public class ConcurrentLongPriorityQueue<T extends LongPrioritized> {
	private final long keyMask;
	private final long keyStep;
	protected final ConcurrentSkipListMap<Long, RedirectableConcurrentLinkedQueue<T>> queueMap = new ConcurrentSkipListMap<>();

	public ConcurrentLongPriorityQueue(long resolution) {
		if (resolution < 1) {
			resolution = 1;
		}
		long mask = GenericMath.roundUpPow2(resolution);
		while (mask > resolution) {
			mask >>= 1;
		}
		this.keyMask = ~(mask - 1);
		this.keyStep = mask;
	}

	/**
	 * Adds a prioritized element to the queue
	 */
	public boolean add(T o) {
		return addRaw(o);
	}

	/**
	 * This method is used to add elements to the queue when redirecting.  It should not be used for other purposes.
	 */
	public boolean redirect(T o) {
		return addRaw(o);
	}

	private boolean addRaw(T o) {
		Long key = getKey(o.getPriority());
		RedirectableConcurrentLinkedQueue<T> queue = queueMap.get(key);
		if (queue == null) {
			queue = new RedirectableConcurrentLinkedQueue<>(key);
			RedirectableConcurrentLinkedQueue<T> previous = queueMap.putIfAbsent(key, queue);
			if (previous != null) {
				queue = previous;
			}
		}
		return queue.add(o);
	}

	/**
	 * Removes a prioritized element from the queue
	 */
	public boolean remove(T o) {
		Long key = getKey(o.getPriority());
		RedirectableConcurrentLinkedQueue<T> queue = queueMap.get(key);
		if (queue == null) {
			return false;
		}
		return queue.remove(o);
	}

	/**
	 * Polls the queue for entries with a priority before or equal to the given threshold.<br> The sub-queue returned may have some entries that occur after the threshold and may not include all entries
	 * that occur before the threshold.  The method returns null if there are no sub-queues before the threshold
	 */
	public Queue<T> poll(long threshold) {
		Entry<Long, RedirectableConcurrentLinkedQueue<T>> first = queueMap.firstEntry();
		if (first == null || first.getKey() > threshold) {
			return null;
		} else {
			return first.getValue();
		}
	}

	/**
	 * This method must be called for every sub-queue that is returned by the poll method.
	 *
	 * @param queue the queue that is returned
	 * @return true if the threshold was covered by this sub-queue, so no further calls to poll() are required
	 */
	public boolean complete(Queue<T> queue, long threshold) {
		RedirectableConcurrentLinkedQueue<T> q = (RedirectableConcurrentLinkedQueue<T>) queue;
		boolean empty = q.isEmpty();
		if (empty) {
			queueMap.remove(q.getPriority(), q);
			q.setRedirect(this);
			q.dumpToRedirect(this);
		}
		return q.getPriority() + keyStep > threshold;
	}

	/**
	 * Returns true if the given queue is completely below the threshold
	 */
	public boolean isFullyBelowThreshold(Queue<T> queue, long threshold) {
		RedirectableConcurrentLinkedQueue<T> q = (RedirectableConcurrentLinkedQueue<T>) queue;
		return q.getPriority() + keyStep <= threshold;
	}

	private Long getKey(long priority) {
		return priority & keyMask;
	}
}
