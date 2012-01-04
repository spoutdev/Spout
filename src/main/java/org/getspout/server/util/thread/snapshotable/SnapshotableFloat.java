package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports primitive floats
 */
public class SnapshotableFloat implements Snapshotable {
	private volatile float next;
	private float snapshot;

	public SnapshotableFloat(SnapshotManager manager, float initial) {
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
	public void set(float next) {
		this.next = next;
	}

	/**
	 * Gets the snapshot value for
	 *
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public float get() {
		return snapshot;
	}

	/**
	 * Gets the live value
	 *
	 * @return the unstable Live "next" value
	 */
	@LiveRead
	public float getLive() {
		return next;
	}

	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot() {
		snapshot = next;
	}

}
