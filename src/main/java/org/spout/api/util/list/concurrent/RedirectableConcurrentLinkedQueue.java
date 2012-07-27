/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.util.list.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class RedirectableConcurrentLinkedQueue<T extends LongPrioritized> extends ConcurrentLinkedQueue<T> implements LongPrioritized {

	private static final long serialVersionUID = 1L;
	private final AtomicReference<ConcurrentLongPriorityQueue<T>> redirect = new AtomicReference<ConcurrentLongPriorityQueue<T>>();
	private final long priority;
	
	public RedirectableConcurrentLinkedQueue(long priority) {
		this.priority = priority;
	}
	
	@Override
	public boolean add(T e) {
		super.add(e);
		ConcurrentLongPriorityQueue<T> r = redirect.get();
		if (r != null) {
			dumpToRedirect(r);
		}
		return true;
	}
	
	public void dumpToRedirect(ConcurrentLongPriorityQueue<T> target) {
		T next;
		while ((next = poll()) != null) {
			target.add(next);
		}
	}
	
	public void setRedirect(ConcurrentLongPriorityQueue<T> target) {
		if (!redirect.compareAndSet(null, target)) {
			throw new IllegalStateException("Redirect may not be set more than once per redirectable queue");
		}
	}

	@Override
	public long getPriority() {
		return priority;
	}
	
}
