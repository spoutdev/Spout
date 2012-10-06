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
package org.spout.api.datatable;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class DatatableStringTest {
	private static final int LENGTH = 1000;

	private Random r = new Random();

	@Test
	public void testString() {
		for (int x = 0; x < LENGTH; x++) {
			checkString("" + r.nextDouble());
		}

		checkString("test");

		checkString("");
	}

	private void checkString(String value) {
		int key = r.nextInt();

		StringData f = new StringData(key);

		f.set(value);

		checkString(f, key, value);

		byte[] compressed = f.compress();

		int key2 = r.nextInt();

		StringData b2 = new StringData(key2);

		b2.decompress(compressed);

		checkString(b2, key2, value);
	}

	private void checkString(StringData f, int key, String value) {
		assertTrue("Wrong key, got " + f.hashCode() + ", expected " + key, f.hashCode() == key);

		assertTrue("Wrong value", f.get().equals(value));
	}
}
