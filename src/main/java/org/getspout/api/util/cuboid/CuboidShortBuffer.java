package org.getspout.api.util.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.math.Vector3;

public class CuboidShortBuffer extends CuboidBuffer {

	private final short[] buffer;
	private CuboidShortBuffer source;
	
	public CuboidShortBuffer(World world, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, short[] buffer) {
		super(world, baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		this.buffer = buffer;
	}

	public CuboidShortBuffer(World world, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this(world, baseX, baseY, baseZ, sizeX, sizeY, sizeZ, new short[sizeX * sizeY * sizeZ]);
	}

	public CuboidShortBuffer(World world, double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this(world, (int) baseX, (int) baseY, (int) baseZ, (int) sizeX, (int) sizeY, (int) sizeZ, new short[(int) (sizeX * sizeY * sizeZ)]);
	}

	public CuboidShortBuffer(Point base, Vector3 size) {
		this(base.getWorld(), (int) base.getX(), (int) base.getY(), (int) base.getZ(), (int) size.getX(), (int) size.getY(), (int) size.getZ(), new short[(int) (size.getX() * size.getY() * size.getZ())]);
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		int end = thisIndex + runLength;
		for (; thisIndex < end; thisIndex++) {
			buffer[thisIndex++] = source.buffer[sourceIndex++];
		}
	}

	@Override
	public void setSource(CuboidBuffer source) {
		if (source instanceof CuboidShortBuffer) {
			this.source = (CuboidShortBuffer) source;
		} else {
			throw new IllegalArgumentException("Only CuboidShortBuffers may be used as the data source when copying to a CuboidShortBuffer");
		}
	}
	
	public void set(int x, int y, int z, short data) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		} else {
			buffer[index] = data;
		}
	}
	
	public short get(int x, int y, int z) {
		int index = getIndex(x, y, z);
		if (index < 0) {
			throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ", " + z + ") is outside the buffer");
		} else {
			return buffer[index];
		}
	}
	
	public short[] getRawArray() {
		return buffer;
	}
	
	public void flood(short id) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = id;
		}
	}

}
