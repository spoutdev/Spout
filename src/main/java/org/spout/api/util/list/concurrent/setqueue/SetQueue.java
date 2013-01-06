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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A concurrent queue where adding an element that is already in the queue has no effect.<br>
 * <br>
 * Elements should be added to the queue using the add() method of SetQueueElement.
 *
 * @param <T>
 */
public class SetQueue<T> implements Iterable<T> {
	
	private final int MAX_ATTEMPTS = 10;
	
	private final Queue<SetQueueElement<T>> queue;

	public SetQueue(int capacity) {
		 queue = new ArrayBlockingQueue<SetQueueElement<T>>(capacity);
	}
	
	protected void add(SetQueueElement<T> e) {
		int count = 0;
		while (!queue.offer(e)) {
			if (count >= MAX_ATTEMPTS) {
				throw new SetQueueFullException("Capacity Exceeded");
			} else if (count > 2) {
				if (queue.contains(e)) {
					// This can only happen if a DirtyQueueFullException has been previously thrown, 
					// since the queued flag in the element is cleared in a non-thread-safe way
					return;
				}
			}
			checkQueueElements();
			count++;
		}
	}
	
	public T poll() {
		SetQueueElement<T> e;
		while (true) {
			e = queue.poll();
			if (e == null) {
				return null;
			}
			e.removed();
			if (!e.isValid()) {
				continue;
			}
			return e.getValue();
		}
	}
	
	public void clear() {
		while (poll() != null) {
		}
	}
	
	public Iterator<T> iterator() {
		return new SetQueueIterator(queue.iterator());
	}

	private void checkQueueElements() {
		Iterator<SetQueueElement<T>> itr = queue.iterator();
		while (itr.hasNext()) {
			if (!itr.next().isValid()) {
				itr.remove();
			}
		}
	}
	
	private class SetQueueIterator implements Iterator<T> {
		
		private final Iterator<SetQueueElement<T>> parent;
		private T next;
		
		public SetQueueIterator(Iterator<SetQueueElement<T>> parent) {
			this.parent = parent;
			next = getNext();
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public T next() {
			if (next == null) {
				throw new NoSuchElementException("No more elements");
			}
			T value = next;
			next = getNext();
			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("The queue may not be modified by the iterator");
		}
		
		private T getNext() {
			while (parent.hasNext()) {
				SetQueueElement<T> nextElement = parent.next();
				if (nextElement.isValid()) {
					return nextElement.getValue();
				}
			}
			return null;
		}
		
	}

}
