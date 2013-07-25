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

import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;

import static org.junit.Assert.assertTrue;

public class ReactConverterTest {
	@Test
	public void testVector3() {
		final Vector3 spoutVector3 = new Vector3(5, 6, 7);
		final org.spout.physics.math.Vector3 reactVector3 = ReactConverter.toReactVector3(spoutVector3);
		assertTrue(reactVector3.getX() == 5);
		assertTrue(reactVector3.getY() == 6);
		assertTrue(reactVector3.getZ() == 7);
		final Vector3 spoutConvertedVector3 = ReactConverter.toSpoutVector3(reactVector3);
		assertTrue(spoutConvertedVector3.getX() == 5);
		assertTrue(spoutConvertedVector3.getY() == 6);
		assertTrue(spoutConvertedVector3.getZ() == 7);
	}

	@Test
	public void testQuaternion() {
		final Quaternion spoutQuaternion = new Quaternion(5, 6, 7, 8, true);
		final org.spout.physics.math.Quaternion reactQuaternion = ReactConverter.toReactQuaternion(spoutQuaternion);
		assertTrue(reactQuaternion.getX() == 5);
		assertTrue(reactQuaternion.getY() == 6);
		assertTrue(reactQuaternion.getZ() == 7);
		assertTrue(reactQuaternion.getW() == 8);
		final Quaternion spoutConvertedQuaternion = ReactConverter.toSpoutQuaternion(reactQuaternion);
		assertTrue(spoutConvertedQuaternion.getX() == 5);
		assertTrue(spoutConvertedQuaternion.getY() == 6);
		assertTrue(spoutConvertedQuaternion.getZ() == 7);
		assertTrue(spoutConvertedQuaternion.getW() == 8);
	}

	@Test
	public void testTransform() {
		final Transform spoutTransform = new Transform(new Point(null, 5, 6, 7), new Quaternion(5, 6, 7, 8, true), new Vector3(1, 2, 3));
		final org.spout.physics.math.Transform reactTransform = ReactConverter.toReactTransform(spoutTransform);
		assertTrue(reactTransform.getPosition().getX() == 5);
		assertTrue(reactTransform.getPosition().getY() == 6);
		assertTrue(reactTransform.getPosition().getZ() == 7);
		assertTrue(reactTransform.getOrientation().getX() == 5);
		assertTrue(reactTransform.getOrientation().getY() == 6);
		assertTrue(reactTransform.getOrientation().getZ() == 7);
		assertTrue(reactTransform.getOrientation().getW() == 8);
		final Transform spoutConvertedTransform = ReactConverter.toSpoutTransform(reactTransform, null, new Vector3(1, 2, 3));
		assertTrue(spoutConvertedTransform.getPosition().getWorld() == null);
		assertTrue(spoutConvertedTransform.getPosition().getX() == 5);
		assertTrue(spoutConvertedTransform.getPosition().getY() == 6);
		assertTrue(spoutConvertedTransform.getPosition().getZ() == 7);
		assertTrue(spoutConvertedTransform.getRotation().getX() == 5);
		assertTrue(spoutConvertedTransform.getRotation().getY() == 6);
		assertTrue(spoutConvertedTransform.getRotation().getZ() == 7);
		assertTrue(spoutConvertedTransform.getRotation().getW() == 8);
		assertTrue(spoutConvertedTransform.getScale().getX() == 1);
		assertTrue(spoutConvertedTransform.getScale().getY() == 2);
		assertTrue(spoutConvertedTransform.getScale().getZ() == 3);
	}
}