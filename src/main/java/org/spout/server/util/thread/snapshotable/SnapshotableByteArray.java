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

import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * A snapshotable array of type byte
 */
public class SnapshotableByteArray implements Snapshotable {

	private final byte[] snapshot;
	private final byte[] live;
	private final int[] dirtyArray;
	private final AtomicInteger dirtyIndex = new AtomicInteger(0);

	public SnapshotableByteArray(SnapshotManager manager, byte[] initial) {
		this(manager, initial, 100);
	}

	public SnapshotableByteArray(SnapshotManager manager, byte[] initial, int dirtySize) {
		this.snapshot = new byte[initial.length];
		this.live = new byte[initial.length];
		this.dirtyArray = new int[dirtySize];
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
		synchronized (live) {
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
		synchronized (live) {
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
