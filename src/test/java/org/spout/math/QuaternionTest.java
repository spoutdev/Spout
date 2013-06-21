/*
 * This file is part of Math.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Math is licensed under the Spout License Version 1.
 *
 * Math is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Math is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static org.spout.math.TestUtils.eps;

public class QuaternionTest {
	private void testValues(Quaternion q, float x, float y, float z, float w) {
		if (Math.abs(q.getX() - x) > eps || Math.abs(q.getY() - y) > eps || Math.abs(q.getZ() - z) > eps || Math.abs(q.getW() - w) > eps) {
			fail("Quaternion Wrong! Expected: {" + x + "," + y + "," + z + "," + w + "} got " + q);
		}
	}

	@Test
	public void testQuaternionDoubleDoubleDoubleDouble() {
		Quaternion q = new Quaternion(1, 0, 0, 0);
		testValues(q, 1, 0, 0, 0);
		q = new Quaternion(4, 2, 6, 8);
		testValues(q, 4, 2, 6, 8);
	}

	@Test
	public void testQuaternionDoubleVector3() {
		Quaternion rot = Quaternion.fromAngleDegAxis(0, new Vector3(1, 0, 0));
		float qx = 1.f * (float) Math.sin(0);
		float qy = 0.f * (float) Math.sin(0);
		float qz = 0.f * (float) Math.sin(0);
		float qw = (float) Math.cos(0);
		testValues(rot, qx, qy, qz, qw);

		rot = Quaternion.fromAngleDegAxis(40, new Vector3(3, 2, 1));
		float length = (float) Math.sqrt(3 * 3 + 2 * 2 + 1 * 1);
		qx = 3.f / length * (float) Math.sin((Math.toRadians(40) / 2));
		qy = 2.f / length * (float) Math.sin((Math.toRadians(40) / 2));
		qz = 1.f / length * (float) Math.sin((Math.toRadians(40) / 2));
		qw = (float) Math.cos((Math.toRadians(40) / 2));
		testValues(rot, qx, qy, qz, qw);

		rot = Quaternion.fromAngleDegAxis(120, new Vector3(6, -3, 2));
		length = (float) Math.sqrt(6 * 6 + -3 * -3 + 2 * 2);
		qx = 6.f / length * (float) Math.sin((Math.toRadians(120) / 2));
		qy = -3.f / length * (float) Math.sin((Math.toRadians(120) / 2));
		qz = 2.f / length * (float) Math.sin((Math.toRadians(120) / 2));
		qw = (float) Math.cos((Math.toRadians(120) / 2));
		testValues(rot, qx, qy, qz, qw);
	}

	@Test
	public void testLengthSquaredQuaternion() {
		Quaternion rot = new Quaternion(1, 0, 0, 0);
		float ls = rot.lengthSquared();
		if (Math.abs(ls - 1.0f) >= eps) {
			fail("Length Squared of " + rot + " Should be 1.f, got " + ls);
		}

		rot = new Quaternion(6, 4, 3, 2);
		ls = rot.lengthSquared();
		if (Math.abs(ls - 65.0f) >= eps) {
			fail("Length Squared of " + rot + " Should be 65.f, got " + ls);
		}

		rot = new Quaternion(6, -1, 0, 2);
		ls = rot.lengthSquared();
		if (Math.abs(ls - 41.0f) >= eps) {
			fail("Length Squared of " + rot + " Should be 41.f, got " + ls);
		}
	}

	@Test
	public void testLengthQuaternion() {
		Quaternion rot = new Quaternion(1, 0, 0, 0);
		float ls = rot.length();
		if (Math.abs(ls - 1.0f) >= eps) {
			fail("Length of " + rot + " Should be 1.f, got " + ls);
		}

		rot = new Quaternion(6, 4, 3, 2);
		ls = rot.length();
		if (Math.abs(ls - Math.sqrt(65.0f)) >= eps) {
			fail("Length of " + rot + " Should be 65.f, got " + ls);
		}

		rot = new Quaternion(6, -1, 0, 2);
		ls = rot.length();
		if (Math.abs(ls - Math.sqrt(41.0f)) >= eps) {
			fail("Length of " + rot + " Should be 41.f, got " + ls);
		}
	}

	@Test
	public void testNormalizeQuaternion() {
		Quaternion rot = new Quaternion(1, 0, 0, 0);
		Quaternion norm = rot.normalize();
		if (Math.abs(norm.length() - 1.f) >= eps) {
			fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		}

		rot = new Quaternion(6, 4, 3, 2);
		norm = rot.normalize();
		if (Math.abs(norm.length() - 1.f) >= eps) {
			fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		}

		rot = new Quaternion(6, -1, 0, 2);
		norm = rot.normalize();
		if (Math.abs(norm.length() - 1.f) >= eps) {
			fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		}
	}

	@Test
	public void testMultiplyQuaternionQuaternion() {
		Quaternion a = new Quaternion(1, 0, 0, 0);
		Quaternion b = new Quaternion(1, 0, 0, 0);
		Quaternion res = a.mul(b);
		testValues(res, 0, 0, 0, -1);

		a = new Quaternion(0, 0, 0, 1);
		b = new Quaternion(0, 0, 0, 1);
		res = a.mul(b);
		testValues(res, 0, 0, 0, 1);

		a = new Quaternion(5, 3, 1, 1);
		b = new Quaternion(0, 0, 0, 1);
		res = a.mul(b);
		testValues(res, 5, 3, 1, 1);

		a = new Quaternion(5, 3, 1, 1);
		b = new Quaternion(-5, 2, 1, 0);
		res = a.mul(b);
		testValues(res, -4, -8, 26, 18);
	}

	@Test
	public void testGetAxisAngles() {
		final float pitch = 20;
		final Quaternion qpitch = Quaternion.fromAngleDegAxis(pitch, Vector3.RIGHT);
		assertEquals(qpitch.getAxesAngleDeg().getX(), pitch, eps);

		final float yaw = 40;
		final Quaternion qyaw = Quaternion.fromAngleDegAxis(yaw, Vector3.UP);
		assertEquals(qyaw.getAxesAngleDeg().getY(), yaw, eps);

		final float roll = 140;
		final Quaternion qroll = Quaternion.fromAngleDegAxis(roll, Vector3.FORWARD);
		assertEquals(qroll.getAxesAngleDeg().getZ(), roll, eps);

		final Quaternion q1 = qyaw.mul(qpitch).mul(qroll);
		final Vector3 angles1 = q1.getAxesAngleDeg();
		assertEquals(angles1.getX(), pitch, eps);
		assertEquals(angles1.getY(), yaw, eps);
		assertEquals(angles1.getZ(), roll, eps);

		final Quaternion q2 = Quaternion.fromAxesAnglesDeg(pitch, yaw, roll);
		final Vector3 angles2 = q2.getAxesAngleDeg();
		assertEquals(angles2.getX(), pitch, eps);
		assertEquals(angles2.getY(), yaw, eps);
		assertEquals(angles2.getZ(), roll, eps);

		final float polePitch = 90;
		final float poleYaw = 18;
		final float poleRoll = 0;
		final Quaternion pole = Quaternion.fromAxesAnglesDeg(polePitch, poleYaw, poleRoll);
		final Vector3 angles3 = pole.getAxesAngleDeg();
		assertEquals(angles3.getX(), polePitch, eps);
		assertEquals(angles3.getY(), poleYaw, eps);
		assertEquals(angles3.getZ(), poleRoll, eps);
	}
}
