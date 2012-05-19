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
package org.spout.api.util.hashing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TNibbleDualHashedTest {

	public void testValue(byte x, byte y) {
		byte key = TNibbleDualHashed.key(x, y);
		assertEquals(x, TNibbleDualHashed.key1(key));
		assertEquals(y, TNibbleDualHashed.key2(key));
	}

	@Test
	public void testHashes() {
		testValue((byte) 0, (byte) 0);
		testValue((byte) 15, (byte) 15);
		testValue((byte) 12, (byte) 14);
		testValue((byte) 1, (byte) 15);
		testValue((byte) 13, (byte) 2);
	}
}
