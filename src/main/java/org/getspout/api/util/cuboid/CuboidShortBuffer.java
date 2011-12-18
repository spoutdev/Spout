package org.getspout.api.util.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.point.Point;
import org.getspout.api.math.Vector3;

public class CuboidShortBuffer extends CuboidBuffer {

	private final short[] buffer;

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

	/**
	 * Copies the data contained within the given CuboidShortBuffer to this one. Any non-overlapping locations are ignored
	 * 
	 * @param the source CuboidShortBuffer
	 */
	public void write(CuboidShortBuffer source) {
		CuboidBufferCopyRun run = new CuboidBufferCopyRun(source, this);
		
		int sourceIndex = run.getBaseSource();
		int thisIndex = run.getBaseTarget();
		int runLength = run.getLength();
		int innerRepeats = run.getInnerRepeats();
		int outerRepeats = run.getOuterRepeats();
		
		short[] sourceBuffer = source.buffer;
		
		if (sourceIndex == -1 || thisIndex == -1) {
			return;
		} else {
			for (int x = 0; x < outerRepeats; x++) {
				int outerSourceIndex = sourceIndex;
				int outerThisIndex = thisIndex;
				for (int z = 0; z < innerRepeats; z++) {
					int innerSourceIndex = outerSourceIndex;
					int innerThisIndex = outerThisIndex;
					for (int y = 0; y < runLength; y++) {
						buffer[innerThisIndex++] = sourceBuffer[innerSourceIndex++];
					}
					outerSourceIndex += source.Zinc;
					outerThisIndex += Zinc;
				}
				sourceIndex += source.Xinc;
				thisIndex += Xinc;
			}
		}
	}
}
