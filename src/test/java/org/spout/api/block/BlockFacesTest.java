/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.block;

import org.junit.Assert;
import org.junit.Test;

import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the BlockFaces class
 */
public class BlockFacesTest {
	@Test
	public void testContains() {
		Assert.assertEquals(BlockFaces.NESW.contains(BlockFace.NORTH), true);
		assertEquals(BlockFaces.NESW.contains(BlockFace.THIS), false);
	}

	@Test
	public void testIndexOf() {
		assertEquals(BlockFaces.NESW.indexOf(BlockFace.NORTH, -1), 0);
		assertEquals(BlockFaces.NESW.indexOf(BlockFace.EAST, -1), 1);
		assertEquals(BlockFaces.NESW.indexOf(BlockFace.WEST, -1), 3);
		assertEquals(BlockFaces.NESW.indexOf(BlockFace.TOP, -1), -1);
		assertEquals(BlockFaces.NESW.indexOf(BlockFace.TOP, 55), 55);
	}

	@Test
	public void testGet() {
		assertEquals(BlockFaces.NESW.get(0), BlockFace.NORTH);
		assertEquals(BlockFaces.NESW.get(3), BlockFace.WEST);
		assertEquals(BlockFaces.NESW.get(5), BlockFace.WEST);
		assertEquals(BlockFaces.NESW.get(-4), BlockFace.NORTH);
		assertEquals(BlockFaces.NESW.get(6, BlockFace.THIS), BlockFace.THIS);
		assertEquals(BlockFaces.NESW.get(6, null), null);
	}
}
