/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.engine.world.dynamic;

import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.util.hashing.ByteTripleHashed;

public class DynamicBlockUpdate implements Comparable<DynamicBlockUpdate>, DynamicUpdateEntry {
	
	private final static AtomicInteger idCounter = new AtomicInteger(0);

	private final int id;
	private final int packed;
	private final int chunkPacked;
	private final long nextUpdate;
	private final long queuedTime;
	private final int data;
	transient final Object hint;

	private DynamicBlockUpdate next;

	public DynamicBlockUpdate(int packed, long nextUpdate, long queuedTime, int data, Object hint) {
		this(unpackX(packed), unpackY(packed), unpackZ(packed), nextUpdate, queuedTime, data, hint);
	}

	public DynamicBlockUpdate(int x, int y, int z, long nextUpdate, long queuedTime, int data, Object hint) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		this.packed = getBlockPacked(x, y, z);
		this.chunkPacked = ByteTripleHashed.key(x >> Chunk.BLOCKS.BITS, y >> Chunk.BLOCKS.BITS, z >> Chunk.BLOCKS.BITS);
		this.nextUpdate = nextUpdate;
		this.queuedTime = queuedTime;
		this.hint = hint;
		this.data = data;
		this.id = idCounter.getAndIncrement();
	}

	public int getX() {
		return ByteTripleHashed.key1(packed) & 0xFF;
	}

	public int getY() {
		return ByteTripleHashed.key2(packed) & 0xFF;
	}

	public int getZ() {
		return ByteTripleHashed.key3(packed) & 0xFF;
	}

	public long getNextUpdate() {
		return nextUpdate;
	}

	public long getQueuedTime() {
		return queuedTime;
	}

	public int getPacked() {
		return packed;
	}
	
	public int getData() {
		return data;
	}

	public int getChunkPacked() {
		return chunkPacked;
	}
	
	public Object getHint() {
		return hint;
	}

	public boolean isInChunk(Chunk c) {
		int cx = c.getX() & Region.CHUNKS.MASK;
		int cy = c.getY() & Region.CHUNKS.MASK;
		int cz = c.getZ() & Region.CHUNKS.MASK;
		int bxShift = getX() >> Chunk.BLOCKS.BITS;
		int byShift = getY() >> Chunk.BLOCKS.BITS;
		int bzShift = getZ() >> Chunk.BLOCKS.BITS;
		return cx == bxShift && cy == byShift && cz == bzShift;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof DynamicBlockUpdate)) {
			return false;
		}

		return id == ((DynamicBlockUpdate)o).id;
	}

	
	@Override
	public int compareTo(DynamicBlockUpdate o) {
		if (nextUpdate != o.nextUpdate) {
			return subToInt(nextUpdate, o.nextUpdate);
		} else {
			return id - o.id;
		}
	}
	
	@Override
	public int hashCode() {
		return ((int) nextUpdate) + id;
	}
	
	private final int subToInt(long a, long b) {
		long result = a - b;
		int msbs = (int) (result >> 32);
		if (msbs == 0 || msbs == -1) {
			return (int) result;
		}

		if (result > 0) {
			return 1;
		}

		if (result < 0) {
			return -1;
		}

		return 0;
	}

	public DynamicBlockUpdate add(DynamicBlockUpdate update) {
		if (update == null) {
			return this;
		}

		if (update.next != null) {
			throw new IllegalArgumentException("Linked list error in dynamic block update, updates must not already be part of a list");
		}

		update.next = this;
		return update;
	}

	public DynamicBlockUpdate remove(DynamicBlockUpdate update) {
		if (next == null) {
			return this;
		}

		if (update == null) {
			return this;
		}

		if (update == this) {
			try {
				return this.next;
			} finally {
				this.next = null;
			}
		}

		DynamicBlockUpdate current = this;
		while (current != null) {
			if (current.next == update) {
				current.next = update.next;
				update.next = null;
				break;
			}
			current = current.next;
		}
		return this;
	}

	public DynamicBlockUpdate getNext() {
		return next;
	}

	@Override
	public String toString() {
		return "DynamicBlockUpdate{ id: + " + id + " packed: " + getPacked() + " chunkPacked: " + getChunkPacked() + 
				" nextUpdate: " + getNextUpdate() + " queuedTime: " + getQueuedTime() + 
				" pos: (" + getX() + ", " + getY() + ", " + getZ() + ")" +
				" data: " + data + " hint: " + hint + " }";
	}
	
	public static int getPointPacked(Point p) {
		return getBlockPacked(p.getBlockX(), p.getBlockY(), p.getBlockZ());
	}
		
	public static int getBlockPacked(int x, int y, int z) {
		return ByteTripleHashed.key(x & Region.BLOCKS.MASK, y & Region.BLOCKS.MASK, z & Region.BLOCKS.MASK);
	}

	public static int getChunkPacked(Chunk c) {
		return ByteTripleHashed.key(c.getX() & Chunk.BLOCKS.MASK, c.getY() & Chunk.BLOCKS.MASK, c.getZ() & Chunk.BLOCKS.MASK);
	}

	public static int unpackX(int packed) {
		return ByteTripleHashed.key1(packed);
	}

	public static int unpackY(int packed) {
		return ByteTripleHashed.key2(packed);
	}

	public static int unpackZ(int packed) {
		return ByteTripleHashed.key3(packed);
	}
}
