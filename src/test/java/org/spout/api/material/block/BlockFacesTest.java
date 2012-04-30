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
package org.spout.api.material.block;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for the BlockFaces class
 */
public class BlockFacesTest {
	
	@Test
	public void testConstants() {
		assertArrayEquals(BlockFaces.NESW, new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST});
		assertArrayEquals(BlockFaces.NESWBT, new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.BOTTOM, BlockFace.TOP});
		assertArrayEquals(BlockFaces.NSEWB, new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.BOTTOM});
	}
	
	@Test
	public void testContains() {
		assertEquals(BlockFaces.contains(BlockFaces.NESW, BlockFace.NORTH), true);
		assertEquals(BlockFaces.contains(BlockFaces.NESW, BlockFace.THIS), false);
	}
	
	@Test
	public void testIndexOf() {
		assertEquals(BlockFaces.indexOf(BlockFaces.NESW, BlockFace.NORTH, -1), 0);
		assertEquals(BlockFaces.indexOf(BlockFaces.NESW, BlockFace.EAST, -1), 1);
		assertEquals(BlockFaces.indexOf(BlockFaces.NESW, BlockFace.WEST, -1), 3);
		assertEquals(BlockFaces.indexOf(BlockFaces.NESW, BlockFace.TOP, -1), -1);
		assertEquals(BlockFaces.indexOf(BlockFaces.NESW, BlockFace.TOP, 55), 55);
	}
	
	@Test
	public void testGet() {
		assertEquals(BlockFaces.get(BlockFaces.NESW, 0), BlockFace.NORTH);
		assertEquals(BlockFaces.get(BlockFaces.NESW, 3), BlockFace.WEST);
		assertEquals(BlockFaces.get(BlockFaces.NESW, 5), BlockFace.WEST);
		assertEquals(BlockFaces.get(BlockFaces.NESW, -4), BlockFace.NORTH);
		assertEquals(BlockFaces.get(BlockFaces.NESW, 6, BlockFace.THIS), BlockFace.THIS);
		assertEquals(BlockFaces.get(BlockFaces.NESW, 6, null), null);
	}
}
