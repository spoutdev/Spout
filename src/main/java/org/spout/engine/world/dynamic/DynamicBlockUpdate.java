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

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.util.hashing.ByteTripleHashed;

public class DynamicBlockUpdate implements Comparable<DynamicBlockUpdate> {

	private final int packed;
	private final int chunkPacked;
	private final long nextUpdate;
	private final long lastUpdate;
	transient final Object hint;

	private DynamicBlockUpdate next;

	public DynamicBlockUpdate(int packed, long nextUpdate, long lastUpdate, Object hint) {
		this(unpackX(packed), unpackY(packed), unpackZ(packed), nextUpdate, lastUpdate, hint);
	}

	public DynamicBlockUpdate(int x, int y, int z, long nextUpdate, long lastUpdate, Object hint) {
		x &= Region.BLOCKS.MASK;
		y &= Region.BLOCKS.MASK;
		z &= Region.BLOCKS.MASK;
		this.packed = getBlockPacked(x, y, z);
		this.chunkPacked = ByteTripleHashed.key(x >> Chunk.BLOCKS.BITS, y >> Chunk.BLOCKS.BITS, z >> Chunk.BLOCKS.BITS);
		this.nextUpdate = nextUpdate;
		this.lastUpdate = lastUpdate;
		this.hint = hint;
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

	public long getLastUpdate() {
		return lastUpdate;
	}

	public int getPacked() {
		return packed;
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
	public int compareTo(DynamicBlockUpdate o) {
		if (nextUpdate != o.nextUpdate) {
			return subToInt(nextUpdate, o.nextUpdate);
		} else if (packed != o.packed) {
			return packed - o.packed;
		} else {
			return subToInt(lastUpdate, o.lastUpdate);
		}
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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof DynamicBlockUpdate)) {
			return false;
		}

		DynamicBlockUpdate other = (DynamicBlockUpdate) o;
		return nextUpdate == other.nextUpdate && packed == other.packed && lastUpdate == other.lastUpdate;
	}

	public DynamicBlockUpdate add(DynamicBlockUpdate update) {
		if (update == null) {
			return this;
		}

		if (update.next != null) {
			throw new IllegalArgumentException("Linked list error in dynamic block update, updates must not already be part of a list");
		}

		update.next = this.next;
		this.next = update;
		return this;
	}

	public DynamicBlockUpdate remove(DynamicBlockUpdate update) {
		if (next == null) {
			return this;
		}

		if (update == null) {
			return this;
		}

		if (update == this) {
			return this.next;
		}

		DynamicBlockUpdate current = this;
		while (current != null) {
			if (current.next == update) {
				current.next = update.next;
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
	public int hashCode() {
		return (packed << 8) + ((int) nextUpdate);
	}

	@Override
	public String toString() {
		return "DynamicBlockUpdate{ packed: " + getPacked() + " chunkPacked: " + getChunkPacked() + " nextUpdate: " + getNextUpdate() + " lastUpdate: " + getLastUpdate()
				+ " pos: (" + getX() + ", " + getY() + ", " + getZ() + ") }";
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
