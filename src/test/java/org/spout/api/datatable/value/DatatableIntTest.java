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
package org.spout.api.datatable.value;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class DatatableIntTest {
	private static final int LENGTH = 1000;

	private Random r = new Random();

	@Test
	public void testInt() {
		for (int x = 0; x < LENGTH; x++) {
			checkInt(r.nextInt());
		}

		checkInt(0);

		checkInt(1);

		checkInt(-1);
	}

	private void checkInt(int value) {
		int key = r.nextInt();

		DatatableInt i = new DatatableInt(key);

		i.set(value);

		checkInt(i, key, value);

		byte[] compressed = i.compress();

		assertTrue("Compressed array wrong length", compressed.length == 4);

		int key2 = r.nextInt();

		DatatableInt b2 = new DatatableInt(key2);

		b2.decompress(compressed);

		checkInt(b2, key2, value);
	}

	private void checkInt(DatatableInt i, int key, int value) {
		assertTrue("Wrong key, got " + i.hashCode() + ", expected " + key, i.hashCode() == key);

		assertTrue("Wrong value", i.get().equals(new Integer(value)));

		assertTrue("Wrong value as bool", i.asBool() == (value != 0));

		assertTrue("Wrong value as float", i.asFloat() == (float)value);

		assertTrue("Wrong value as int", i.asInt() == value);
	}
}
