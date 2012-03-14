/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.math;

import static org.junit.Assert.fail;

import org.junit.Test;

public class QuaternionTest {
	final float eps = 0.01f;

	private void testValues(Quaternion q, float x, float y, float z, float w) {
		//System.out.println("Testing! Expected: {"+x+","+y+","+z+","+w+"} got " + q);
		if (Math.abs(q.getX() - x) > eps || Math.abs(q.getY() - y) > eps || Math.abs(q.getZ() - z) > eps || Math.abs(q.getW() - w) > eps) {
			fail("Quaternion Wrong! Expected: {" + x + "," + y + "," + z + "," + w + "} got " + q);
		}
	}

	@Test
	public void testQuaternionDoubleDoubleDoubleDouble() {
		Quaternion q = Quaternion.create(1, 0, 0, 0, true);
		testValues(q, 1, 0, 0, 0);
		q = Quaternion.create(4, 2, 6, 8, true);
		testValues(q, 4, 2, 6, 8);
	}

	@Test
	public void testQuaternionDoubleVector3() {
		Quaternion rot = Quaternion.create(0, Vector3.create(1, 0, 0));
		float qx = 1.f * (float) Math.sin(0);
		float qy = 0.f * (float) Math.sin(0);
		float qz = 0.f * (float) Math.sin(0);
		float qw = (float) Math.cos(0);
		testValues(rot, qx, qy, qz, qw);

		rot = Quaternion.create(40, Vector3.create(3, 2, 1));
		qx = 3.f * (float) Math.sin((Math.toRadians(40) / 2));
		qy = 2.f * (float) Math.sin((Math.toRadians(40) / 2));
		qz = 1.f * (float) Math.sin((Math.toRadians(40) / 2));
		qw = (float) Math.cos((Math.toRadians(40) / 2));
		testValues(rot, qx, qy, qz, qw);

		rot = Quaternion.create(120, Vector3.create(6, -3, 2));
		qx = 6.f * (float) Math.sin((Math.toRadians(120) / 2));
		qy = -3.f * (float) Math.sin((Math.toRadians(120) / 2));
		qz = 2.f * (float) Math.sin((Math.toRadians(120) / 2));
		qw = (float) Math.cos((Math.toRadians(120) / 2));
		testValues(rot, qx, qy, qz, qw);
	}

	@Test
	public void testLengthSquaredQuaternion() {
		Quaternion rot = Quaternion.create(1, 0, 0, 0, true);
		float ls = rot.lengthSquared();
		if (Math.abs(ls - 1.0f) >= eps) {
			fail("Length Squared of " + rot + " Should be 1.f, got " + ls);
		}

		rot = Quaternion.create(6, 4, 3, 2, true);
		ls = rot.lengthSquared();
		if (Math.abs(ls - 65.0f) >= eps) {
			fail("Length Squared of " + rot + " Should be 65.f, got " + ls);
		}

		rot = Quaternion.create(6, -1, 0, 2, true);
		ls = rot.lengthSquared();
		if (Math.abs(ls - 41.0f) >= eps) {
			fail("Length Squared of " + rot + " Should be 41.f, got " + ls);
		}
	}

	@Test
	public void testLengthQuaternion() {
		Quaternion rot = Quaternion.create(1, 0, 0, 0, true);
		float ls = rot.length();
		if (Math.abs(ls - 1.0f) >= eps) {
			fail("Length of " + rot + " Should be 1.f, got " + ls);
		}

		rot = Quaternion.create(6, 4, 3, 2, true);
		ls = rot.length();
		if (Math.abs(ls - Math.sqrt(65.0f)) >= eps) {
			fail("Length of " + rot + " Should be 65.f, got " + ls);
		}

		rot = Quaternion.create(6, -1, 0, 2, true);
		ls = rot.length();
		if (Math.abs(ls - Math.sqrt(41.0f)) >= eps) {
			fail("Length of " + rot + " Should be 41.f, got " + ls);
		}
	}

	@Test
	public void testNormalizeQuaternion() {
		Quaternion rot = Quaternion.create(1, 0, 0, 0);
		Quaternion norm = rot.normalize();
		if (Math.abs(norm.length() - 1.f) >= eps) {
			fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		}

		rot = Quaternion.create(6, 4, 3, 2);
		norm = rot.normalize();
		if (Math.abs(norm.length() - 1.f) >= eps) {
			fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		}

		rot = Quaternion.create(6, -1, 0, 2);
		norm = rot.normalize();
		if (Math.abs(norm.length() - 1.f) >= eps) {
			fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		}
	}

	@Test
	public void testMultiplyQuaternionQuaternion() {
		Quaternion a = Quaternion.create(1, 0, 0, 0, true);
		Quaternion b = Quaternion.create(1, 0, 0, 0, true);
		Quaternion res = a.multiply(b);
		testValues(res, 0, 0, 0, -1);

		a = Quaternion.create(0, 0, 0, 1, true);
		b = Quaternion.create(0, 0, 0, 1, true);
		res = a.multiply(b);
		testValues(res, 0, 0, 0, 1);

		a = Quaternion.create(5, 3, 1, 1, true);
		b = Quaternion.create(0, 0, 0, 1, true);
		res = a.multiply(b);
		testValues(res, 5, 3, 1, 1);

		a = Quaternion.create(5, 3, 1, 1, true);
		b = Quaternion.create(-5, 2, 1, 0, true);
		res = a.multiply(b);
		testValues(res, -4, -8, 26, 18);
	}

	@Test
	public void testRotateQuaternionDoubleVector3() {
		float qx, qy, qz, qw;
		float x = 1;
		float y = 0;
		float z = 0;
		float ang = 0;

		Quaternion a = Quaternion.create(0, 0, 0, 1);
		Quaternion res = a.rotate(ang, Vector3.create(x, y, z));
		testValues(res, 0, 0, 0, 1);

		x = 1;
		ang = 45;
		a = Quaternion.create(0, 0, 0, 1);
		res = a.rotate(ang, Vector3.create(x, y, z));
		qx = x * (float) Math.sin((Math.toRadians(ang) / 2));
		qy = y * (float) Math.sin((Math.toRadians(ang) / 2));
		qz = z * (float) Math.sin((Math.toRadians(ang) / 2));
		qw = (float) Math.cos((Math.toRadians(ang) / 2));
		testValues(res, qx, qy, qz, qw);

		x = 1.f;
		y = 4.f;
		z = -3.f;
		ang = 120;
		a = Quaternion.create(0, 0, 0, 1);
		res = a.rotate(ang, Vector3.create(x, y, z));
		qx = x * (float) Math.sin((Math.toRadians(ang) / 2));
		qy = y * (float) Math.sin((Math.toRadians(ang) / 2));
		qz = z * (float) Math.sin((Math.toRadians(ang) / 2));
		qw = (float) Math.cos((Math.toRadians(ang) / 2));
		testValues(res, qx, qy, qz, qw);
	}

	@Test
	public void testGetAxisAngles() {
		Quaternion r;
		Vector3 res;
		float ang = 20;
		r = Quaternion.create(ang, Vector3.create(1, 0, 0));
		res = r.getAxisAngles();
		if (res.getX() - ang >= eps) {
			fail("Expected angle" + ang + ", got " + res.getX());
		}

		ang = 40;
		r = Quaternion.create(ang, Vector3.create(0, 1, 0));
		res = r.getAxisAngles();
		if (Math.abs(res.getY() - ang) >= eps) {
			fail("Expected angle" + ang + ", got " + res.getY());
		}

		ang = 140;
		r = Quaternion.create(ang, Vector3.create(0, 0, 1));
		res = r.getAxisAngles();
		if (Math.abs(res.getZ() - ang) >= eps) {
			fail("Expected angle " + ang + ", got " + res.getZ());
		}
	}
}
