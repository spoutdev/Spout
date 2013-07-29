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
package org.spout.api.geo.discrete;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import org.spout.api.geo.World;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.vector.Vector3;

import static org.junit.Assert.assertEquals;

public class TransformTest {
	@Test
	public void test() {
		World mock = PowerMockito.mock(World.class);
		Point p = new Point(mock, 0, 0, 0);
		Quaternion q = new Quaternion(1, 0, 0, 0);
		Vector3 s = new Vector3(0, 0, 0);
		Transform transform = new Transform(p, q, s);
		assertEquals("Transform point values did not match", transform.getPosition(), p);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);
		assertEquals("Transform scale values did not match", transform.getScale(), s);

		p = p.add(1, 5, -3);
		transform.setPosition(p);
		assertEquals("Transform point values did not match", transform.getPosition(), p);

		q = Quaternion.fromAngleDegAxis(45.0f, 0, 1, 0);
		transform.setRotation(q);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);

		s = s.add(-3, 5, -13);
		transform.setScale(s);
		assertEquals("Transform scale values did not match", transform.getScale(), s);

		p = p.mul(5);
		q = Quaternion.fromAngleDegAxis(-45.0f, 1, 0, 0);
		s = s.div(0.85F);
		transform.set(p, q, s);
		assertEquals("Transform point values did not match", transform.getPosition(), p);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);
		assertEquals("Transform scale values did not match", transform.getScale(), s);

		Transform copy = new Transform(new Point(p), new Quaternion(q), new Vector3(s));
		assertEquals("Copy of transform failed equals test", transform, copy);
		assertEquals("Copy of transform failed reverse-equals test", copy, transform);
		assertEquals("Copy of transform hashcodes do not match", transform.hashCode(), copy.hashCode());

		copy = transform.copy();
		assertEquals("Copy of transform failed equals test", transform, copy);
		assertEquals("Copy of transform failed reverse-equals test", copy, transform);
		assertEquals("Copy of transform hashcodes do not match", transform.hashCode(), copy.hashCode());
	}
}
