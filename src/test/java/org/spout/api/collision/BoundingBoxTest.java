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
package org.spout.api.collision;

import static org.junit.Assert.*;

import static org.spout.api.math.TestUtils.eps;

import org.spout.api.math.Vector3;

import org.junit.Test;

public class BoundingBoxTest {
	private void testValue(String name, Vector3 min1, Vector3 min2) {
		Vector3 diff = min1.subtract(min2).abs();
		if (diff.getX() >= eps || diff.getY() >= eps || diff.getZ() >= eps) {
			fail("Test Fail! Expected " + name + " " + min1 + " but got " + min2);
		}
	}

	private void testValue(BoundingBox b1, BoundingBox b2) {
		testValue("Min", b1.min, b2.min);
		testValue("Max", b1.max, b2.max);
	}

	/**
	 * Test of equals method, of class BoundingBox.
	 */
	@Test
	public void testEquals() {
		BoundingBox a = new BoundingBox(-1.5f, -1.44f, -0.002f, 1.544f, 1.111111f, 1.662f);
		BoundingBox b = new BoundingBox(-1.5f, -1.44f, -0.002f, 1.544f, 1.111111f, 1.662f);

		assertTrue(a.equals(b));
	}

	/**
	 * Test of scale method, of class BoundingBox.
	 */
	@Test
	public void testScale() {
		BoundingBox a = new BoundingBox(-0.3f, -0.5f, -0.7f, 0.12f, 0.43f, 0.1f);
		BoundingBox b = new BoundingBox(-4.5f, -7.5f, -10.5f, 1.8f, 6.45f, 1.5f);
		a.scale(1.5f);
		b.scale(0.1f);


		testValue(a, b);

		a.scale(new Vector3(0.6f, 0.6f, 0.6f));
		b.scale(-0.3f, -0.3f, -0.3f).scale(-2.0f);

		testValue(a, b);
	}

	/**
	 * Test of size method, of class BoundingBox.
	 */
	@Test
	public void testSize() {
		BoundingBox a = new BoundingBox(-2f, -3f, -4f, 6f, 8f, 4f);
		Vector3 b = new Vector3(8f, 11f, 8f);

		testValue("Size", a.getSize(), b);
	}

	@Test
	public void testIntersects() {
		BoundingBox a = new BoundingBox(-2f, -3f, -4f, 6f, 8f, 4f);
		BoundingBox b = new BoundingBox(-2f, -3f, -4f, 6f, 10f, 4f);
		assertTrue(a.intersects(b));

		BoundingBox c = new BoundingBox(1, 1, 1, 1, 1, 1);
		BoundingBox d = new BoundingBox(-10, -10, -10, -10, -10, -10);
		d.offset(11, 11, 11);
		assertTrue(c.intersects(d));
	}
}
