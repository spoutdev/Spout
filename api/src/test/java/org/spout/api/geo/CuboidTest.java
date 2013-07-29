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
package org.spout.api.geo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.math.vector.Vector3;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CuboidTest {
	private World world;
	private Cuboid instance;

	@Before
	public void setup() {
		world = mock(World.class);
		instance = new Cuboid(new Point(world, 0, 0, 0), Vector3.ONE.mul(16.0));
	}

	@After
	public void tearDown() {
		world = null;
		instance = null;
	}

	@Test
	public void testContains() {
		// test out
		Vector3 vec = new Vector3(-1, -0.5, -1);
		assertFalse(instance.contains(vec));

		// test in
		vec = new Vector3(1, 1, 1);
		assertTrue(instance.contains(vec));

		// Exclusive max
		vec = new Vector3(16, 16, 16);
		assertFalse(instance.contains(vec));

		// Exclusive max
		vec = new Vector3(16, 14, 5);
		assertFalse(instance.contains(vec));

		// Inclusive min
		vec = new Vector3(0, 0, 0);
		assertTrue(instance.contains(vec));

		// Inclusive min
		vec = new Vector3(0, 14, 5);
		assertTrue(instance.contains(vec));
	}
}
