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

import org.junit.Test;

import org.spout.math.vector.Vector3f;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CuboidBlockMaterialBufferTest {
	@Test
	public void testSetLayer() {
		CuboidBlockMaterialBuffer buffer = new CuboidBlockMaterialBuffer(5, 5, 5, 10, 22, 10);
		final int yStart = 6, height = 8;
		final short id = 12, data = 14;
		buffer.setHorizontalLayer(yStart, height, id, data);
		Vector3f base = buffer.getBase();
		Vector3f top = buffer.getTop();
		for (int x = base.getFloorX(); x < top.getFloorX(); x++) {
			for (int z = base.getFloorZ(); z < top.getFloorZ(); z++) {
				for (int y = base.getFloorZ(); y < top.getFloorY(); y++) {
					if (y >= yStart && y < (yStart + height)) {
						assertEquals(buffer.getId(x, y, z), id);
						assertEquals(buffer.getData(x, y, z), data);
					} else {
						assertNotEquals(buffer.getId(x, y, z), id);
						assertNotEquals(buffer.getData(x, y, z), data);
					}
				}
			}
		}
	}
}
