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

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CuboidNibbleLightBufferTest {
	private final int SIZE = 16;
	private final int HALF_SIZE = 16;
	private final int LOOPS = 10;

	@Test
	public void copyTest() {

		Random r = new Random();

		for (int c = 0; c < LOOPS; c++) {
			int bx = r.nextInt();
			int by = r.nextInt();
			int bz = r.nextInt();

			int sx = r.nextInt(SIZE) + SIZE;
			int sy = r.nextInt(SIZE) + SIZE;
			int sz = r.nextInt(SIZE) + SIZE;

			int vol = sx * sy * sz;
			if ((vol | 1) == vol) {
				sx &= (~1);
			}
			short destId = (short) r.nextInt();
			CuboidNibbleLightBuffer dest = new CuboidNibbleLightBuffer(null, destId, bx, by, bz, sx, sy, sz);

			assertTrue("Id not set correctly", destId == dest.getManagerId());

			int bx2 = bx + r.nextInt(HALF_SIZE);
			int by2 = by + r.nextInt(HALF_SIZE);
			int bz2 = bz + r.nextInt(HALF_SIZE);

			int sx2 = r.nextInt(HALF_SIZE) + HALF_SIZE;
			int sy2 = r.nextInt(HALF_SIZE) + HALF_SIZE;
			int sz2 = r.nextInt(HALF_SIZE) + HALF_SIZE;

			int vol2 = sx2 * sy2 * sz2;
			if ((vol2 | 1) == vol2) {
				sx2 &= (~1);
			}

			short srcId = (short) r.nextInt();
			CuboidNibbleLightBuffer src = new CuboidNibbleLightBuffer(null, srcId, bx2, by2, bz2, sx2, sy2, sz2);

			assertTrue("Id not set correctly", srcId == src.getManagerId());

			byte[][][] values = new byte[sx2][sy2][sz2];

			for (int x = bx2; x < bx2 + sx2; x++) {
				for (int y = by2; y < by2 + sy2; y++) {
					for (int z = bz2; z < bz2 + sz2; z++) {
						byte value = (byte) (r.nextInt() & 0xf);
						values[x - bx2][y - by2][z - bz2] = value;
						src.set(x, y, z, value);
					}
				}
			}

			for (int x = bx2; x < bx2 + sx2; x++) {
				for (int y = by2; y < by2 + sy2; y++) {
					for (int z = bz2; z < bz2 + sz2; z++) {
						byte value = values[x - bx2][y - by2][z - bz2];
						assertTrue("value mismatch in setting up src buffer " + (x - bx2) + ", " + (y - by2) + ", " + (z - bz2) + ", got " + src.get(x, y, z) + ", exp " + value, value == src.get(x, y, z));
					}
				}
			}

			dest.write(src);

			for (int x = bx2; x < bx2 + sx2; x++) {
				for (int y = by2; y < by2 + sy2; y++) {
					for (int z = bz2; z < bz2 + sz2; z++) {
						if (x >= (bx + sx) || y >= (by + sy) || z >= (bz + sz)) {
							continue;
						}
						byte value = values[x - bx2][y - by2][z - bz2];
						assertTrue("value mismatch after copy " + (x - bx2) + ", " + (y - by2) + ", " + (z - bz2) + ", got " + dest.get(x, y, z) + ", exp " + value, value == dest.get(x, y, z));
					}
				}
			}

			for (int x = bx; x < bx + sx; x++) {
				for (int y = by; y < by + sy; y++) {
					for (int z = bz; z < bz + sz; z++) {
						if (x < (bx2 + sx2) && x >= bx2 && y < (by2 + sy2) && y >= by2 && z < (bz2 + sz2) && z >= bz2) {
							continue;
						}
						assertTrue("Dest buffer changed outside source buffer " + (x - bx) + ", " + (y - by) + ", " + (z - bz) + ", got " + dest.get(x, y, z) + ", exp " + 0, 0 == dest.get(x, y, z));
					}
				}
			}
		}
	}

	@Test
	public void rowZTest() {

		AlignedCuboidNibbleLightBuffer buffer = new AlignedCuboidNibbleLightBuffer(null, 0, 128, -1024, 256, 64, 32, 16);

		Random r = new Random();

		for (int x = buffer.baseX; x < buffer.topX; x++) {
			for (int y = buffer.baseX; y < buffer.topX; y++) {
				for (int z = buffer.baseX; z < buffer.topX; z++) {
					buffer.set(x, y, z, (byte) r.nextInt());
				}
			}
		}

		for (int i = 0; i < 100; i++) {
			int relStartX = r.nextInt(buffer.sizeX);
			int relStartY = r.nextInt(buffer.sizeY);
			int relStartZ = r.nextInt(buffer.sizeZ);

			int startX = relStartX + buffer.baseX;
			int startY = relStartY + buffer.baseY;
			int startZ = relStartZ + buffer.baseZ;

			int length = r.nextInt(buffer.topZ - startZ);

			int arrayStart = r.nextInt(20);
			int arrayEnd = arrayStart + length;

			int[] array = new int[arrayStart + length + r.nextInt(20)];

			int[] oldRow = new int[buffer.sizeZ];

			for (int j = 0; j < buffer.sizeZ; j++) {
				oldRow[j] = buffer.get(startX, startY, j + buffer.baseZ);
			}

			for (int j = 0; j < array.length; j++) {
				array[j] = r.nextInt() & 0xF;
			}

			buffer.copyZRow(startX, startY, startZ, arrayStart, arrayEnd, array);

			for (int j = 0; j < buffer.sizeZ; j++) {
				int absZ = j + buffer.baseZ;
				if (absZ >= startZ && absZ < (startZ + length)) {
					assertEquals("Data was not successfully copied at index " + j, array[absZ - startZ + arrayStart], buffer.get(startX, startY, absZ));
				} else {
					assertEquals("Data was overwriten at index " + j, oldRow[j], buffer.get(startX, startY, absZ));
				}
			}
		}
	}
}
