package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports primitive shorts
 */
public class SnapshotableShort implements Snapshotable {
	private volatile short next;
	private short snapshot;
	
	public SnapshotableShort(SnapshotManager manager, short initial) {
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
	public void set(short next) {
		this.next = next;
	}
	
	/**
	 * Gets the snapshot value for 
	 * 
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public short get() {
		return snapshot;
	}
	
	/**
	 * Gets the live value
	 * 
	 * @return the unstable Live "next" value
	 */
	@LiveRead
	public short getLive() {
		return next;
	}
	
	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot() {
		snapshot = next;
	}

}
