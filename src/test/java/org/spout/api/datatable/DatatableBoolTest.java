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
import org.spout.api.datatable.BooleanData;

public class DatatableBoolTest {
	private Random r = new Random();

	@Test
	public void testBoolean() {
		checkBool(true);

		checkBool(false);
	}

	private void checkBool(boolean value) {
		int key = r.nextInt();

		BooleanData b = new BooleanData(key);

		b.set(value);

		checkBool(b, key, value);

		byte[] compressed = b.compress();

		assertTrue("Compressed array wrong length", compressed.length == 1);

		int key2 = r.nextInt();

		BooleanData b2 = new BooleanData(key2);

		b2.decompress(compressed);

		checkBool(b2, key2, value);
	}

	private void checkBool(BooleanData b, int key, boolean value) {
		assertTrue("Wrong key, got " + b.hashCode() + ", expected " + key, b.hashCode() == key);

		assertTrue("Wrong value", b.get().equals(new Boolean(value)));
	}
}
