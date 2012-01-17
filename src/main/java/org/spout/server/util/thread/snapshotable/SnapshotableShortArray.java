/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.util.thread.snapshotable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * A snapshotable array of type short
 */
public class SnapshotableShortArray implements Snapshotable {

	private final short[] snapshot;
	private final AtomicIntegerArray live;
	private final AtomicIntegerArray dirtyArray;
	private final int dirtySize;
	private final AtomicInteger dirtyIndex = new AtomicInteger(0);

	public SnapshotableShortArray(SnapshotManager manager, short[] initial) {
		this(manager, initial, 50);
	}

	public SnapshotableShortArray(SnapshotManager manager, short[] initial, int dirtySize) {
		this.snapshot = new short[initial.length];
		this.live = new AtomicIntegerArray(initial.length>>1);
		this.dirtySize = dirtySize;
		this.dirtyArray = new AtomicIntegerArray(dirtySize);
		for (int i = 0; i < initial.length; i++) {
			this.snapshot[i] = initial[i];
			set(i, initial[i]);
		}
	}

	/**
	 * Gets a copy of the snapshot short array
	 * 
	 * @return copy of the snapshot short array
	 */
	public short[] get() {
		return Arrays.copyOf(snapshot, snapshot.length);
	}

	/**
	 * Gets a copy of the live short array
	 * 
	 * @return copy of the live short array
	 */
	public short[] getLive() {
		short[] live = new short[snapshot.length];
		for (int i = 0; i < this.live.length(); i++) {
			int value = this.live.get(i);
			live[(i << 1)] = (short)(value & 0xFFFF);
			live[(i << 1) + 1] = (short)((value >> 16) & 0xFFFF);
		}
		return live;
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
		int packed = live.get(index >> 1);
		if ((index & 0x1) == 0) {
			return unpackZero(packed);
		} else {
			return unpackOne(packed);
		}
	}

	/**
	 * Sets the value for the next snapshot
	 *
	 * @param index to set at
	 * @param value to set to
	 * @return the old value
	 */
	@DelayedWrite
	public short set(int index, short value) {
		boolean success = false;
		int divIndex = index >> 1;
		boolean isZero = (index & 0x1) == 0;
		short one;
		short zero;
		short old = 0;

		while (!success) {
			int packed = live.get(divIndex);
			if (isZero) {
				old = unpackZero(packed);
				one = unpackOne(packed);
				zero = value;
			} else {
				old = unpackOne(packed);
				one = value;
				zero = unpackZero(packed);
			}
			success = live.compareAndSet(divIndex, packed, pack(zero, one));
		}
		markDirty(index);
		return old;
	}

	private void markDirty(int index) {
		int localDirtyIndex = dirtyIndex.getAndIncrement();
		if (localDirtyIndex < dirtySize) {
			dirtyArray.set(localDirtyIndex, index);
		}
	}

	/**
	 * Copies the next value to the snapshot value
	 */
	@Override
	public void copySnapshot() {
		int length = dirtyIndex.get();
		if (length <= dirtySize) {
			for (int i = 0; i < length; i++) {
				int index = dirtyArray.get(i);
				this.snapshot[index] = getLive(i);
			}
		} else {
			for (int i = 0; i < snapshot.length; i++) {
				this.snapshot[i] = getLive(i);
			}
		}
	}

	private int pack(short zero, short one) {
		return (one & 0xFFFF) << 16 | (zero & 0xFFFF);
	}

	private short unpackZero(int value) {
		return (short)value;
	}

	private short unpackOne(int value) {
		return (short)(value >> 16);
	}

}
