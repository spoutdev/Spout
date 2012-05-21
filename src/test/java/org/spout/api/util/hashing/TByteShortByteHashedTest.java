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
package org.spout.api.util.hashing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TByteShortByteHashedTest {

	public void testValue(byte x, short y, byte z) {
		int key = TByteShortByteHashed.key(x, y, z);
		assertEquals(x, TByteShortByteHashed.key1(key));
		assertEquals(y, TByteShortByteHashed.key2(key));
		assertEquals(z, TByteShortByteHashed.key3(key));
	}

	@Test
	public void testHashes() {
		testValue((byte) 231, (short) 6352, (byte) 65);
		testValue((byte) 23, (short) 23235, (byte) 85);
		testValue((byte) 45, (short) 2566, (byte) 214);
		testValue((byte) 128, (short) 128, (byte) 128);
		testValue((byte) 245, (short) 32, (byte) 21);
		testValue((byte) 255, (short) 30000, (byte) 255);
		testValue((byte) 0, (short) -30000, (byte) 0);
		testValue((byte) 231, (short) -24214, (byte) 12);
	}
}
