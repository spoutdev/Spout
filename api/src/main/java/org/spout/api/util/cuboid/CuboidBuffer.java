/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.util.cuboid;

import org.spout.math.vector.Vector3;

/**
 * This class implements a Cuboid common methods for a one dimensional array Cuboid Buffer
 *
 * Elements are stored in column order and each column is +1 on the Z dimension relative to the previous one.
 *
 * Each YZ plane is followed by the plane corresponding to +1 on the X dimension.
 *
 * It is assumed that the Cuboid has dimensions (SX, SY, SZ) and the base is set at the origin.
 *
 * buffer[0] = data(0, 0, 0 ) buffer[1] = data(0, 1, 0 ) ..... buffer[SY-1] = data(0, SY-1, 0 ) buffer[SY] = data(0, 0 1 ) .... buffer[SZ*SY - 1] = data(0, SY-1, SZ-1) buffer[SZ*SY] = data(1, 0, 0 )
 * .... buffer[SZ*SY*SX -1] = data(SX-1, SY-1, SZ-1)
 *
 * TODO is this the best package to put this?
 */
public abstract class CuboidBuffer {
	protected final Vector3 size;
	protected final int sizeX;
	protected final int sizeY;
	protected final int sizeZ;
	protected final Vector3 base;
	protected final int baseX;
	protected final int baseY;
	protected final int baseZ;
	/*
	 * Note: These values are not actually within the cuboid The cuboid goes
	 * from baseX to baseX + sizeX - 1 top = base + size
	 */
	protected final int topX;
	protected final int topY;
	protected final int topZ;
	protected final int Xinc;
	protected final int Yinc;
	protected final int Zinc;

	protected CuboidBuffer(int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;

		this.size = new Vector3(sizeX, sizeY, sizeZ);

		this.baseX = baseX;
		this.baseY = baseY;
		this.baseZ = baseZ;

		this.base = new Vector3(baseX, baseY, baseZ);

		topX = baseX + sizeX;
		topY = baseY + sizeY;
		topZ = baseZ + sizeZ;

		Yinc = sizeZ * (Zinc = sizeX * (Xinc = 1));
	}

	protected CuboidBuffer(double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this((int) baseX, (int) baseY, (int) baseZ, (int) sizeX, (int) sizeY, (int) sizeZ);
	}

	protected CuboidBuffer(Vector3 base, Vector3 size) {
		this(base.getX(), base.getY(), base.getZ(), size.getX(), size.getY(), size.getZ());
	}

	/**
	 * Gets a Point representing the base of this CuboidBuffer
	 */
	public Vector3 getBase() {
		return base;
	}

	/**
	 * Gets the size of the CuboidBuffer
	 */
	public Vector3 getSize() {
		return size;
	}

	/**
	 * Gets the volume of the CuboidBuffer
	 */
	public int getVolume() {
		return sizeX * sizeY * sizeZ;
	}

	/**
	 * Gets the top-coordinates of the CuboidBuffer, these are outside this buffer<br> These coordinates are an addition of base and size
	 */
	public Vector3 getTop() {
		return new Vector3(topX, topY, topZ);
	}

	/**
	 * Return true if the coordinates are inside the buffer.
	 *
	 * @param x The x coordinate to check.
	 * @param y The y coordinate to check.
	 * @param z The Z coordinate to check.
	 * @return True if the coordinate are in the buffer, false if not.
	 */
	public boolean isInside(int x, int y, int z) {
		return getIndex(x, y, z) >= 0;
	}

	/**
	 * Copies the data contained within the given CuboidShortBuffer to this one. Any non-overlapping locations are ignored
	 *
	 * @param source The CuboidShortBuffer source from which to copy the data.
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
				sourceIndex += source.Yinc;
				thisIndex += Yinc;
			}
		}
	}

	protected int getIndex(int x, int y, int z) {
		return getIndex(this, x, y, z);
	}

	protected static int getIndex(CuboidBuffer source, int x, int y, int z) {
		if (x < source.baseX || x >= source.topX || y < source.baseY || y >= source.topY || z < source.baseZ || z >= source.topZ) {
			return -1;
		}

		return (y - source.baseY) * source.Yinc + (z - source.baseZ) * source.Zinc + (x - source.baseX) * source.Xinc;
	}

	protected CuboidBufferCopyRun getCopyRun(CuboidBuffer other) {
		return new CuboidBufferCopyRun(this, other);
	}

	public abstract void copyElement(int thisIndex, int sourceIndex, int runLength);

	public abstract void setSource(CuboidBuffer source);

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{Buffer Size=" + sizeX * sizeY * sizeZ + ", Base=(" + baseX + ", " + baseY + ", " + baseZ + "}, Size=(" + sizeX + ", " + sizeY + ", " + sizeZ + "), " + "Increments=(" + Xinc + ", " + Yinc + ", " + Zinc + "), Top=(" + topX + ", " + topY + ", " + topZ + ")}";
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
			return overlapSizeX;
		}

		public int getInnerRepeats() {
			return overlapSizeZ;
		}

		public int getOuterRepeats() {
			return overlapSizeY;
		}
	}
}
