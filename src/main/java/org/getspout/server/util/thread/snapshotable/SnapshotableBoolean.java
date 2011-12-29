package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports primitive booleans
 */
public class SnapshotableBoolean implements Snapshotable {
	private volatile boolean next;
	private boolean snapshot;
	
	public SnapshotableBoolean(SnapshotManager manager, boolean initial) {
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
	public void set(boolean next) {
		this.next = next;
	}
	
	/**
	 * Gets the snapshot value for 
	 * 
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public boolean get() {
		return snapshot;
	}
	
	/**
	 * Gets the live value
	 * 
	 * @return the unstable Live "next" value
	 */
	@LiveRead
	public boolean getLive() {
		return next;
	}
	
	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot() {
		snapshot = next;
	}

}
