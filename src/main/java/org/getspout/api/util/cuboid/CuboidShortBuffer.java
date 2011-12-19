package org.getspout.api.util.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.math.Vector3;

public class CuboidShortBuffer extends CuboidBuffer {

	private final short[] buffer;
	private CuboidShortBuffer source;

	protected CuboidShortBuffer(World world, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		super(world, baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		buffer = new short[sizeX * sizeY * sizeZ];
	}

	protected CuboidShortBuffer(World world, double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		super(world, baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		buffer = new short[(int)(sizeX * sizeY * sizeZ)];
	}

	protected CuboidShortBuffer(Point base, Vector3 size) {
		super(base, size);
		buffer = new short[(int)(size.getX() * size.getY() * size.getZ())];
	}

	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		int end = thisIndex + runLength;
		for (; thisIndex < end; thisIndex++) {
			buffer[thisIndex++] = source.buffer[sourceIndex++];
		}
	}

	public void setSource(CuboidBuffer source) {
		if (source instanceof CuboidShortBuffer) {
			this.source = (CuboidShortBuffer)source;
		} else {
			throw new IllegalArgumentException("Only CuboidShortBuffers may be used as the data source when copying to a CuboidShortBuffer");
		}
	}
	
}
