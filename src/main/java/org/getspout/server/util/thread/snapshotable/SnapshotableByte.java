package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable object that supports primitive bytes
 */
public class SnapshotableByte implements Snapshotable {
	private volatile byte next;
	private byte snapshot;

	public SnapshotableByte(SnapshotManager manager, byte initial) {
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
	public void set(byte next) {
		this.next = next;
	}

	/**
	 * Gets the snapshot value for
	 *
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public byte get() {
		return snapshot;
	}

	/**
	 * Gets the live value
	 *
	 * @return the unstable Live "next" value
	 */
	@LiveRead
	public byte getLive() {
		return next;
	}

	/**
	 * Copies the next value to the snapshot value
	 */
	public void copySnapshot() {
		snapshot = next;
	}

}
