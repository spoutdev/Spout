package org.spout.engine.world.dynamic;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.util.hashing.ByteTripleHashed;

public class DynamicBlockUpdate implements Comparable<DynamicBlockUpdate> {
	private static final int chunkMask = Region.REGION_SIZE - 1;
	private static final int regionMask = (Region.REGION_SIZE * Chunk.CHUNK_SIZE) - 1;
	private final int packed;
	private final int chunkPacked;
	private final long nextUpdate;
	private final long lastUpdate;

	public DynamicBlockUpdate(int packed, long nextUpdate, long lastUpdate) {
		this(unpackX(packed), unpackY(packed), unpackZ(packed), nextUpdate, lastUpdate);
	}

	public DynamicBlockUpdate(int x, int y, int z, long nextUpdate, long lastUpdate) {
		x &= regionMask;
		y &= regionMask;
		z &= regionMask;
		this.packed = ByteTripleHashed.key(x, y, z);
		this.chunkPacked = ByteTripleHashed.key(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS);
		this.nextUpdate = nextUpdate;
		this.lastUpdate = lastUpdate;
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

	public boolean isInChunk(Chunk c) {
		int cx = c.getX() & chunkMask;
		int cy = c.getY() & chunkMask;
		int cz = c.getZ() & chunkMask;
		int bxShift = getX() >> Chunk.CHUNK_SIZE_BITS;
		int byShift = getY() >> Chunk.CHUNK_SIZE_BITS;
		int bzShift = getZ() >> Chunk.CHUNK_SIZE_BITS;
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
		if (msbs != 0 && msbs != -1) {
			if (result > 0) {
				return 1;
			} else if (result < 0) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return (int) result;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else {
			if (o instanceof DynamicBlockUpdate) {
				DynamicBlockUpdate other = (DynamicBlockUpdate) o;
				return nextUpdate == other.nextUpdate && packed == other.packed && lastUpdate == other.lastUpdate;
			} else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		return (packed << 8) + ((int) nextUpdate);
	}

	@Override
	public String toString() {
		return "DynamicBlockUpdate{ packed: " + getPacked() + " chunkPacked: " + getChunkPacked() +
				" nextUpdate: " + getNextUpdate() + " lastUpdate: " + getLastUpdate() +
				" pos: (" + getX() + ", " + getY() + ", " + getZ() + ") }";
	}

	public static int getChunkPacked(Chunk c) {
		int cx = c.getX() & chunkMask;
		int cy = c.getY() & chunkMask;
		int cz = c.getZ() & chunkMask;
		return ByteTripleHashed.key(cx, cy, cz);
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
