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
package org.spout.api.util.set;

import org.junit.Test;
import static org.junit.Assert.*;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;

public class TInt21HashSetTest {
	@Test
	public void testKey() {
		assertEquals(0x0, TInt21HashSet.key(0, 0, 0));
		assertEquals(0x0, TInt21HashSet.key(0x7FF00000, 0x0FF00000, 0x7F000000));
		assertEquals(0x0000000000000001L, TInt21HashSet.key(0, 0, 1));
		assertEquals(0x0000000000200000L, TInt21HashSet.key(0, 1, 0));
		assertEquals(0x0000000000200001L, TInt21HashSet.key(0, 1, 1));
		assertEquals(0x0000040000000000L, TInt21HashSet.key(1, 0, 0));
		assertEquals(0x0000040000000001L, TInt21HashSet.key(1, 0, 1));
		assertEquals(0x0000040000200000L, TInt21HashSet.key(1, 1, 0));
		assertEquals(0x0000040000200001L, TInt21HashSet.key(1, 1, 1));
		assertEquals(0x00000000001FFFFFL, TInt21HashSet.key(0, 0, -1));
		assertEquals(0x000003FFFFE00000L, TInt21HashSet.key(0, -1, 0));
		assertEquals(0x000003FFFFFFFFFFL, TInt21HashSet.key(0, -1, -1));
		assertEquals(0x7FFFFC0000000000L, TInt21HashSet.key(-1, 0, 0));
		assertEquals(0x7FFFFC00001FFFFFL, TInt21HashSet.key(-1, 0, -1));
		assertEquals(0x7FFFFFFFFFE00000L, TInt21HashSet.key(-1, -1, 0));
		assertEquals(0x7FFFFFFFFFFFFFFFL, TInt21HashSet.key(-1, -1, -1));
		assertEquals(0x4000020000100000L, TInt21HashSet.key(-2147483648, -2147483648, -2147483648));

		int shifts = Region.REGION_SIZE_BITS + Chunk.CHUNK_SIZE_BITS;
		assertEquals(0x4000020000100000L, TInt21HashSet.key(-2147483648 >> shifts, -2147483648 >> shifts, -2147483648 >> shifts));
	}
}
