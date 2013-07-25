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
package org.spout.engine.world.dynamic;

import java.util.Random;

import org.junit.Test;

import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.engine.faker.ChunkFaker;

import static org.junit.Assert.assertTrue;

public class DynamicBlockUpdateTest {
	private final static int CHECKS = 1000;

	@Test
	public void test() throws Exception {

		Random r = new Random();

		for (int i = 0; i < CHECKS; i++) {
			int x = r.nextInt(256);
			int y = r.nextInt(256);
			int z = r.nextInt(256);
			long nextUpdate = (Math.abs(r.nextLong()) % 1000000L) + 1;
			int data = r.nextInt();

			DynamicBlockUpdate update = new DynamicBlockUpdate(x, y, z, nextUpdate, data);

			assertTrue("X value mismatch, exp " + x + ", got " + update.getX(), x == update.getX());
			assertTrue("Y value mismatch, exp " + y + ", got " + update.getY(), y == update.getY());
			assertTrue("Z value mismatch, exp " + z + ", got " + update.getZ(), z == update.getZ());
			assertTrue("nextUpdate value mismatch, exp " + nextUpdate + ", got " + update.getNextUpdate(), nextUpdate == update.getNextUpdate());
			assertTrue("data value mismatch, exp " + data + ", got " + update.getData(), data == update.getData());

			int chunkPacked = update.getChunkPacked();

			Chunk c = ChunkFaker.getChunk(x >> Region.CHUNKS.BITS, y >> Region.CHUNKS.BITS, z >> Region.CHUNKS.BITS);

			assertTrue("Unable to generate fake chunk", c != null);

			int chunkPackedFromChunk = DynamicBlockUpdate.getChunkPacked(c);

			assertTrue("Chunk packed calculation mismatch between static method and internal calculation", chunkPacked == chunkPackedFromChunk);
		}
	}
}
