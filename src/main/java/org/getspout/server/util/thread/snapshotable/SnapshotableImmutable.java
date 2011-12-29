package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports basic class types.
 * 
 * This class should be used for immutable types that are updated by replacing with a new immutable object
 * 
 * @param <T> the underlying type
 */
public class SnapshotableImmutable<T> implements Snapshotable {
	private volatile T next;
	private T snapshot;
	
	public SnapshotableImmutable(SnapshotManager manager, T initial) {
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
	@LiveRead
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
