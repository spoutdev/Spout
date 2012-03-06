/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.util.cuboid;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

/**
 * This class implements a Cuboid common methods for a one dimensional array
 * Cuboid Buffer
 *
 * Elements are stored in column order and each column is +1 on the Z dimension
 * relative to the previous one.
 *
 * Each YZ plane is followed by the plane corresponding to +1 on the X
 * dimension.
 *
 * It is assumed that the Cuboid has dimensions (SX, SY, SZ) and the base is set
 * at the origin.
 *
 * buffer[0] = data(0, 0, 0 ) buffer[1] = data(0, 1, 0 ) ..... buffer[SY-1] =
 * data(0, SY-1, 0 ) buffer[SY] = data(0, 0 1 ) .... buffer[SZ*SY - 1] = data(0,
 * SY-1, SZ-1) buffer[SZ*SY] = data(1, 0, 0 ) .... buffer[SZ*SY*SX -1] =
 * data(SX-1, SY-1, SZ-1)
 *
 * TODO is this the best package to put this?
 */
public abstract class CuboidBuffer {
	private final World world;

	private final int sizeX;
	private final int sizeY;
	private final int sizeZ;

	private final int baseX;
	private final int baseY;
	private final int baseZ;

	// Note: These values are not actually within the cuboid
	//       The cuboid goes from baseX to baseX + sizeX - 1
	//       top* = base* + size*
	private final int topX;
	private final int topY;
	private final int topZ;

	protected final int Xinc;
	protected final int Yinc;
	protected final int Zinc;

	protected CuboidBuffer(World world, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this.world = world;

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.baseX = baseX;
		this.baseY = baseY;
		this.baseZ = baseZ;

		topX = baseX + sizeX;
		topY = baseY + sizeY;
		topZ = baseZ + sizeZ;

		Yinc = sizeY * (Zinc = sizeZ * (Xinc = 1));
	}

	protected CuboidBuffer(World world, double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this(world, (int) baseX, (int) baseY, (int) baseZ, (int) sizeX, (int) sizeY, (int) sizeZ);
	}

	protected CuboidBuffer(Point base, Vector3 size) {
		this(base.getWorld(), base.getX(), base.getY(), base.getZ(), size.getX(), size.getY(), size.getZ());
	}

	/**
	 * Gets a Point representing the base of this CuboidBuffer
	 */
	public Point getBase() {
		return new Point(world, baseX, baseY, baseZ);
	}

	/**
	 * Gets a World the CuboidBuffer is located in
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Gets the size of the CuboidBuffer
	 */
	public Vector3 getSize() {
		return new Vector3(sizeX, sizeY, sizeZ);
	}

	/**
	 * Copies the data contained within the given CuboidShortBuffer to this one.
	 * Any non-overlapping locations are ignored
	 *
	 * @param the source CuboidShortBuffer
	 */
	public void write(CuboidBuffer source) {
		CuboidBufferCopyRun run = new CuboidBufferCopyRun(source, this);

		int sourceIndex = run.getBaseSource();
		int thisIndex = run.getBaseTarget();
		int runLength = run.getLength();
		int innerRepeats = run.getInnerRepeats();
		int outerRepeats = run.getOuterRepeats();

		setSource(source);

		if (!(sourceIndex == -1 || thisIndex == -1)) {
			for (int x = 0; x < outerRepeats; x++) {
				int outerSourceIndex = sourceIndex;
				int outerThisIndex = thisIndex;
				for (int z = 0; z < innerRepeats; z++) {
					copyElement(outerThisIndex, outerSourceIndex, runLength);

					outerSourceIndex += source.Zinc;
					outerThisIndex += Zinc;
				}
				sourceIndex += source.Xinc;
				thisIndex += Xinc;
			}
		}
	}

	protected int getIndex(int x, int y, int z) {
		return getIndex(this, x, y, z);
	}

	protected static int getIndex(CuboidBuffer source, int x, int y, int z) {
		if (x < source.baseX || x >= source.topX || y < source.baseY || y >= source.topY || z < source.baseZ || z >= source.topZ) {
			return -1;
		} else {
			return (y - source.baseY) * source.Yinc + (z - source.baseZ) * source.Zinc + (x - source.baseX) * source.Xinc;
		}
	}

	protected CuboidBufferCopyRun getCopyRun(CuboidBuffer other) {
		return new CuboidBufferCopyRun(this, other);
	}

	public abstract void copyElement(int thisIndex, int sourceIndex, int runLength);

	public abstract void setSource(CuboidBuffer source);

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{Buffer Size=" + sizeX * sizeY * sizeZ + ", Base=(" + world.getName() + ", " + baseX + ", " + baseY + ", " + baseZ + "}, Size=(" + sizeX + ", " + sizeY + ", " + sizeZ + "), " + "Increments=(" + Xinc + ", " + Yinc + ", " + Zinc + "), Top=(" + topX + ", " + topY + ", " + topZ + ")}";
	}

	protected static class CuboidBufferCopyRun {

		private int overlapBaseX;
		private int overlapBaseY;
		private int overlapBaseZ;

		private int overlapSizeX;
		private int overlapSizeY;
		private int overlapSizeZ;

		private int sourceIndex;
		private int targetIndex;

		public CuboidBufferCopyRun(CuboidBuffer source, CuboidBuffer target) {
			overlapBaseX = Math.max(source.baseX, target.baseX);
			overlapBaseY = Math.max(source.baseY, target.baseY);
			overlapBaseZ = Math.max(source.baseZ, target.baseZ);

			overlapSizeX = Math.min(source.topX, target.topX) - overlapBaseX;
			overlapSizeY = Math.min(source.topY, target.topY) - overlapBaseY;
			overlapSizeZ = Math.min(source.topZ, target.topZ) - overlapBaseZ;

			if (overlapSizeX < 0 || overlapSizeY < 0 || overlapSizeZ < 0) {
				sourceIndex = -1;
				targetIndex = -1;
				return;
			}

			sourceIndex = getIndex(source, overlapBaseX, overlapBaseY, overlapBaseZ);
			targetIndex = getIndex(target, overlapBaseX, overlapBaseY, overlapBaseZ);
		}

		public int getBaseSource() {
			return sourceIndex;
		}

		public int getBaseTarget() {
			return targetIndex;
		}

		public int getLength() {
			return overlapSizeY;
		}

		public int getInnerRepeats() {
			return overlapSizeZ;
		}

		public int getOuterRepeats() {
			return overlapSizeX;
		}
	}
}
