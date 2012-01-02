package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable array of type byte
 */
public class SnapshotableByteArray implements Snapshotable {
	
	private final byte[] snapshot;
	private final byte[] live;
	
	public SnapshotableByteArray(SnapshotManager manager, byte[] initial) {
		this.snapshot = new byte[initial.length];
		this.live = new byte[initial.length];
		for (int i = 0; i < initial.length; i++) {
			this.snapshot[i] = initial[i];
			this.live[i] = initial[i];
		}
	}
	
	/**
	 * Gets the snapshot value in the array
	 * 
	 * @param index to lookup
	 * @return snapshot value
	 */
	@SnapshotRead
	public byte get(int index) {
		return snapshot[index];
	}
	
	/**
	 * Gets the live value in the array
	 * 
	 * @param index to lookup
	 * @return live value
	 */
	@LiveRead
	public byte getLive(int index) {
		return live[index];
	}
	
	/**
	 * Sets the value for the next snapshot
	 * 
	 * @param index to set at
	 * @param value to set to
	 */
	@DelayedWrite
	public byte set(int index, byte value) {
		live[index] = value;
		return snapshot[index];
	}

	/**
	 * Copies the next value to the snapshot value
	 */
	@Override
	public void copySnapshot() {
		for (int i = 0; i < live.length; i++) {
			this.snapshot[i] = live[i];
		}
	}

}
