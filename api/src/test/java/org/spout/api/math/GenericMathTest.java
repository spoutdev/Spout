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
package org.spout.api.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static org.spout.api.math.GenericMath.mod;

public final class GenericMathTest {
	@Test
	public void testMod() {

		for (int i = -15; i <= 15; i++) {
			modCheck(i, 5);
		}
	}

	private void modCheck(final int a, final int d) {

		int newA = a;

		while (newA <= 0) {
			newA += d;
		}

		assertEquals("Mod check failed for " + a + " mod " + d, (newA % d), mod(a, d));

		assertEquals("Mod check failed for " + a + " mod " + d, (newA % d), mod((float) a, (float) d), 0.001F);

		assertEquals("Mod check failed for " + a + " mod " + d, (newA % d), mod((double) a, (double) d), 0.001D);
	}
}
