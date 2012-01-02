package org.getspout.server.util.thread.snapshotable;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable array of type short
 */
public class SnapshotableShortArray implements Snapshotable {
	
	private final short[] snapshot;
	private final short[] live;
	
	public SnapshotableShortArray(SnapshotManager manager, short[] initial) {
		this.snapshot = new short[initial.length];
		this.live = new short[initial.length];
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
	public short get(int index) {
		return snapshot[index];
	}
	
	/**
	 * Gets the live value in the array
	 * 
	 * @param index to lookup
	 * @return live value
	 */
	@LiveRead
	public short getLive(int index) {
		return live[index];
	}
	
	/**
	 * Sets the value for the next snapshot
	 * 
	 * @param index to set at
	 * @param value to set to
	 */
	@DelayedWrite
	public short set(int index, short value) {
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
