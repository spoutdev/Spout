package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports primitive longs
 */
public class SnapshotableLong implements Snapshotable {
	private volatile long next;
	private long snapshot;
	
	public SnapshotableLong(SnapshotManager manager, long initial) {
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
	public void set(long next) {
		this.next = next;
	}
	
	/**
	 * Gets the snapshot value for 
	 * 
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public long get() {
		return snapshot;
	}
	
	/**
	 * Gets the live value
	 * 
	 * @return the unstable Live "next" value
	 */
	@LiveRead
	public long getLive() {
		return next;
	}
	
	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot() {
		snapshot = next;
	}

}
