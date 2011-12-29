package org.getspout.server.util.thread.snapshotutils;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;

public class Snapshotable<T> {
	private volatile T next;
	private T snapshot;
	
	public Snapshotable(SnapshotManager manager, T initial) {
		next = initial;
		snapshot = initial;
		manager.add(this);
	}
	
	/**
	 * Sets the next value for the Snapshotable
	 * 
	 * @param next
	 */
	@DelayedWrite
	public void set(T next) {
		this.next = next;
	}
	
	/**
	 * Gets the snapshot value for 
	 * 
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public T get() {
		return snapshot;
	}
	
	/**
	 * Gets the live value
	 * 
	 * @return the unstable Live "next" value
	 */
	@SnapshotRead
	public T getLive() {
		return next;
	}
	
	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot() {
		snapshot = next;
	}

}
