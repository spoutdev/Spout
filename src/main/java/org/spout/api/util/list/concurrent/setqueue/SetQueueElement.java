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
	
	public void add() {
		if (queued.compareAndSet(false, true)) {
			try {
				queue.add(this);
			} catch (SetQueueFullException e) {
				removed(); // not thread safe, could cause the element to be added to the queue twice
				throw e;
			}
		}
	}
	
	protected abstract boolean isValid();

	public String toString() {
		return "DirtyQueueElement{" + value + "}";
	}
	
}
