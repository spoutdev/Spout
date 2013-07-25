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
package org.spout.api.block;

import org.junit.Test;

import org.spout.api.material.block.BlockFace;

import static org.junit.Assert.assertEquals;

public class BlockFaceTest {
	@Test
	public void testFaceFromYaw() {
		assertEquals(BlockFace.fromYaw(-80f), BlockFace.NORTH);
		assertEquals(BlockFace.fromYaw(283f), BlockFace.NORTH);
		assertEquals(BlockFace.fromYaw(12f), BlockFace.WEST);
		assertEquals(BlockFace.fromYaw(87f), BlockFace.SOUTH);
		assertEquals(BlockFace.fromYaw(180f), BlockFace.EAST);
	}

	@Test
	public void testFaceFromOffset() {
		for (BlockFace face : BlockFace.values()) {
			assertEquals(face, BlockFace.fromOffset(face.getOffset()));
		}
	}
}
