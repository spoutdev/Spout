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

import static org.junit.Assert.*;

import org.junit.Test;

public class QuaternionmTest {
	final float eps = 0.01f;
	private void testValues(Quaternion q, float x, float y, float z, float w){
		//System.out.println("Testing! Expected: {"+x+","+y+","+z+","+w+"} got " + q);
		if(Math.abs(q.getX() - x) > eps || Math.abs(q.getY() - y) > eps
				|| Math.abs(q.getZ() - z) > eps || Math.abs(q.getW() - w) > eps) {
			fail("Quaternion Wrong! Expected: {"+x+","+y+","+z+","+w+"} got " + q);
		}
	}

	@Test
	public void testNormalizeQuaternionm() {
		Quaternion rot = new Quaternionm(1,0,0,0);
		Quaternion norm = rot.normalize();
		if(Math.abs(norm.length()- 1.f) >= eps) fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		assertTrue("norm is not identical to rot", norm == rot);

		rot = new Quaternionm(6,4,3,2);
		norm = rot.normalize();
		if(Math.abs(norm.length()- 1.f) >= eps) fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		assertTrue("norm is not identical to rot", norm == rot);

		rot = new Quaternionm(6,-1,0,2);
		norm = rot.normalize();
		if(Math.abs(norm.length()- 1.f) >= eps) fail("Normalized form of " + rot + " Should be length 1 but got " + norm.length());
		assertTrue("norm is not identical to rot", norm == rot);
	}

	@Test
	public void testMultiplyQuaternionQuaternionm() {
		Quaternion a = new Quaternionm(1,0,0,0);
		Quaternion b = new Quaternionm(1,0,0,0);
		Quaternion res = a.multiply(b);
		testValues(res, 0, 0, 0, -1);
		assertTrue("res is not identical to a", res == a);

		a = new Quaternionm(0,0,0,1);
		b = new Quaternionm(0,0,0,1);
		res = a.multiply(b);
		testValues(res, 0, 0, 0, 1);
		assertTrue("res is not identical to a", res == a);

		a = new Quaternionm(5,3,1,1);
		b = new Quaternionm(0,0,0,1);
		res = a.multiply(b);
		testValues(res, 5, 3, 1, 1);
		assertTrue("res is not identical to a", res == a);

		a = new Quaternionm(5,3,1,1);
		b = new Quaternionm(-5,2,1,0);
		res = a.multiply(b);
		testValues(res, -4, -8, 26, 18);
		assertTrue("res is not identical to a", res == a);
	}

	@Test
	public void testRotateQuaternionDoubleVector3() {
		float qx,qy,qz,qw;
		float x = 1;
		float y = 0;
		float z = 0;
		float ang = 0;

		Quaternion a = new Quaternionm(0,0,0,1);
		Quaternion res = a.rotate(ang, new Vector3(x,y,z));
		testValues(res, 0, 0, 0, 1);
		assertTrue("res is not identical to a", res == a);


		x = 1;
		ang = 45;
		a = new Quaternionm(0,0,0,1);
		res = a.rotate(ang, new Vector3(x,y,z));
		qx = x * (float)Math.sin((Math.toRadians(ang)/2));
		qy = y * (float)Math.sin((Math.toRadians(ang)/2));
		qz = z * (float)Math.sin((Math.toRadians(ang)/2));
		qw = (float)Math.cos((Math.toRadians(ang)/2));
		testValues(res, qx, qy, qz, qw);

		x = 1.f;
		y = 4.f;
		z = -3.f;
		ang = 120;
		a = new Quaternionm(0,0,0,1);
		res = a.rotate(ang, new Vector3(x,y,z));
		qx = x * (float)Math.sin((Math.toRadians(ang)/2));
		qy = y * (float)Math.sin((Math.toRadians(ang)/2));
		qz = z * (float)Math.sin((Math.toRadians(ang)/2));
		qw = (float)Math.cos((Math.toRadians(ang)/2));
		testValues(res, qx, qy, qz, qw);
		assertTrue("res is not identical to a", res == a);
	}
}
