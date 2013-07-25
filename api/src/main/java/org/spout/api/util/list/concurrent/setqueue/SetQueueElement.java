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
package org.spout.api.util.list.concurrent.setqueue;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An element for a SetQueue.<br>
 * <br>
 * Calls to the add() method have no effect if the element is already in the queue.
 * 
 * @param <T>
 */
public abstract class SetQueueElement<T> {
	
	private final SetQueue<T> queue;
	private final T value;
	private final AtomicBoolean queued = new AtomicBoolean(false);
	
	public SetQueueElement(SetQueue<T> queue, T value) {
		this.queue = queue;
		if (value == null) {
			throw new IllegalArgumentException("The value may not be set to null");
		}
		this.value = value;
	}
	
	protected T getValue() {
		return value;
	}
	
	protected void removed() {
		queued.set(false);
	}
	
	public SetQueue<T> getQueue() {
		return queue;
	}
	
	public boolean add() {
		if (queued.compareAndSet(false, true)) {
			try {
				queue.add(this);
			} catch (SetQueueFullException e) {
				removed(); // not thread safe, could cause the element to be added to the queue twice
				throw e;
			}
			return true;
		}
		return false;
	}
	
	protected abstract boolean isValid();

	public String toString() {
		return "DirtyQueueElement{" + value + "}";
	}
	
}
