/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import org.spout.api.geo.World;
import org.spout.api.math.GenericMath;
import org.spout.api.math.Quaternion;
import org.spout.api.math.QuaternionMath;
import org.spout.api.math.Vector3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransformTest {
	@Test
	public void test() {
		World mock = PowerMockito.mock(World.class);
		Point p = new Point(mock, 0, 0, 0);
		Quaternion q = Quaternion.UNIT_X;
		Vector3 s = new Vector3(0, 0, 0);
		Transform transform = new Transform(p, q, s);
		assertEquals("Transform point values did not match", transform.getPosition(), p);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);
		assertEquals("Transform scale values did not match", transform.getScale(), s);

		p = p.add(1, 5, -3);
		transform.setPosition(p);
		assertEquals("Transform point values did not match", transform.getPosition(), p);

		q = q.rotate(45F, 0, 1, 0);
		transform.setRotation(q);
		assertEquals("Transform quaternion values did not match", transform.getRotation(), q);

		s = s.add(-3, 5, -13);
		transform.setScale(s);
		assertEquals("Transform scale values did not match", transform.getScale(), s);

		p = p.multiply(5);
		q = q.rotate(-45, 1, 0, 0);
		s = s.divide(0.85F);
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

	@Test
	public void sceneToPhysicsTest() {
		final World mock = PowerMockito.mock(World.class);
		//Test Scene -> Physics
		final Point p = new Point(mock, 0, 0, 0);
		final Quaternion q = Quaternion.UNIT_X;
		final Vector3 s = new Vector3(0, 0, 0);
		final Transform sceneTransform = new Transform(p, q, s);
		final com.bulletphysics.linearmath.Transform physicsTransform = GenericMath.toPhysicsTransform(sceneTransform);
		//Test Physics Space
		final Vector3f physicsSpace = physicsTransform.origin;
		assertTrue(physicsSpace.x == p.getX());
		assertTrue(physicsSpace.y == p.getY());
		assertTrue(physicsSpace.z == p.getZ());
		//Test Physics Rotation
		Quat4f physicsRotation = new Quat4f();
		physicsTransform.getRotation(physicsRotation);
		assertTrue(physicsRotation.w == q.getW());
		assertTrue(physicsRotation.x == q.getX());
		assertTrue(physicsRotation.y == q.getY());
		assertTrue(physicsRotation.z == q.getZ());
	}

	@Test
	public void physicsToSceneTest() {
		final World mock = PowerMockito.mock(World.class);
		final Vector3f physicsSpace = new Vector3f(0, 0, 0);
		final Quat4f physicsRotation = new Quat4f(QuaternionMath.toQuaternionf(Quaternion.UNIT_X));
		final Matrix4f physicsMatrix = new Matrix4f(physicsRotation, physicsSpace, 1);
		final com.bulletphysics.linearmath.Transform physicsTransform = new com.bulletphysics.linearmath.Transform(physicsMatrix);
		final Transform liveState = new Transform();
		liveState.setPosition(new Point(mock, 0, 0, 0)); //To purely set the world
		liveState.setScale(new Vector3(0, 0, 0)); //Physics has no scale but we still test conversion of it
		final Transform sceneTransform = GenericMath.toSceneTransform(liveState, physicsTransform);
		final Point sceneSpace = sceneTransform.getPosition();
		final Quaternion sceneRotation = sceneTransform.getRotation();
		final Vector3 sceneScale = sceneTransform.getScale();
		assertTrue(sceneSpace.world.equals(mock));
		assertTrue(sceneSpace.getX() == physicsSpace.x);
		assertTrue(sceneSpace.getY() == physicsSpace.y);
		assertTrue(sceneSpace.getZ() == physicsSpace.z);
		assertTrue(sceneRotation.getW() == physicsRotation.w);
		assertTrue(sceneRotation.getX() == physicsRotation.x);
		assertTrue(sceneRotation.getY() == physicsRotation.y);
		assertTrue(sceneRotation.getZ() == physicsRotation.z);
		assertTrue(sceneScale.equals(liveState.getScale()));
	}
}
