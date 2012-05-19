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

public class TInt21TripleHashedTest {

	public void testValue(int x, int y, int z) {
		long key = TInt21TripleHashed.key(x, y, z);
		assertEquals(x, TInt21TripleHashed.key1(key));
		assertEquals(y, TInt21TripleHashed.key2(key));
		assertEquals(z, TInt21TripleHashed.key3(key));
	}

	@Test
	public void testHashes() {
		testValue(-1048575, -1048575, -1048575);
		testValue(0, 0, 0);
		testValue(1048575, 1048575, 1048575);
		testValue(1048575, -1048575, 1048575);
		testValue(-1048575, 1048575, -1048575);
		testValue(32423, 14144, 24114);
		testValue(10475, 104865, 104835);
		testValue(128, 512, 1024);
		testValue(-34, 2421, -4452);
	}
}
