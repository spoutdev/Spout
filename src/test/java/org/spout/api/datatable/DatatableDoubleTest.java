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

public class DatatableDoubleTest {
	private static final int LENGTH = 1000;

	private Random r = new Random();

	@Test
	public void testDouble() {
		for (int x = 0; x < LENGTH; x++) {
			checkDouble(r.nextDouble());
		}

		checkDouble(0.0D);

		checkDouble(1.0D);

		checkDouble(-1.0D);
	}

	private void checkDouble(double value) {
		int key = r.nextInt();

		DoubleData f = new DoubleData(key);

		f.set(value);

		checkDouble(f, key, value);

		byte[] compressed = f.compress();

		assertTrue("Compressed array wrong length", compressed.length == 8);

		int key2 = r.nextInt();

		DoubleData b2 = new DoubleData(key2);

		b2.decompress(compressed);

		checkDouble(b2, key2, value);
	}

	private void checkDouble(DoubleData f, int key, double value) {
		assertTrue("Wrong key, got " + f.hashCode() + ", expected " + key, f.hashCode() == key);

		assertTrue("Wrong value", f.get().equals(new Double(value)));
	}
}
