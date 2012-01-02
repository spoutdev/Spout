package org.getspout.server.util.thread.snapshotable;

import java.util.concurrent.atomic.AtomicInteger;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable array of type byte
 */
public class SnapshotableByteArray implements Snapshotable {
	
	private final byte[] snapshot;
	private byte[] live;
	private int[] dirtyArray;
	private AtomicInteger dirtyIndex = new AtomicInteger(0);
	
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
		synchronized(live) {
			return live[index];
		}
	}
	
	/**
	 * Sets the value for the next snapshot
	 * 
	 * @param index to set at
	 * @param value to set to
	 */
	@DelayedWrite
	public byte set(int index, byte value) {
		synchronized(live) {
			live[index] = value;
		}
		int localDirtyIndex = dirtyIndex.getAndIncrement();
		if (localDirtyIndex < dirtyArray.length) {
			dirtyArray[localDirtyIndex] = index;
		}
		return snapshot[index];
	}

	/**
	 * Copies the next value to the snapshot value
	 */
	@Override
	public void copySnapshot() {
		int length = dirtyIndex.get();
		if (length <= dirtyArray.length) {
			for (int i = 0; i < length; i++) {
				int index = dirtyArray[i];
				this.snapshot[index] = live[index];
			}
		} else {
			for (int i = 0; i < live.length; i++) {
				this.snapshot[i] = live[i];
			}
		}
	}

}
