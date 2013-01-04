package org.spout.api.util.list.concurrent.setqueue;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A concurrent queue where adding an element that is already in the queue has no effect.<br>
 * <br>
 * Elements should be added to the queue using the add() method of SetQueueElement.
 *
 * @param <T>
 */
public class SetQueue<T> {
	
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

	private void checkQueueElements() {
		Iterator<SetQueueElement<T>> itr = queue.iterator();
		while (itr.hasNext()) {
			if (!itr.next().isValid()) {
				itr.remove();
			}
		}
	}

}
