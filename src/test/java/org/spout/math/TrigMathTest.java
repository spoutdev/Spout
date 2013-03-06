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
import static org.junit.Assert.assertTrue;

public class TrigMathTest {
	private void testValue(float angle, float result, float realValue) {
		assertTrue("angle=" + angle + " expected " + realValue + " but got " + result, Math.abs(result - realValue) < 0.0001);
	}

	private void testSin(float value) {
		testValue(value, TrigMath.sin(value), (float) Math.sin(value));
	}

	private void testCos(float value) {
		testValue(value, TrigMath.cos(value), (float) Math.cos(value));
	}

	@Test
	public void testSinCos() {
		float step = (float) (TrigMath.TWO_PI / 100.0); //100 steps in the circle
		for (float i = (float) -TrigMath.PI; i < TrigMath.TWO_PI; i += step) {
			testSin(i);
			testCos(i);
		}
	}

	private void assert2D(float angle, Vector2 vector, float x, float y) {
		String msg = "angle=" + angle + " expected [" + x + ", " + y + "] but got [" + vector.getX() + ", " + vector.getY() + "]";
		assertTrue(msg, vector.subtract(x, y).lengthSquared() < 0.001);
	}

	private void assert3D(float yaw, float pitch, Vector3 vector, float x, float y, float z) {
		String msg = "[yaw=" + yaw + ", pitch=" + pitch + "] expected [" + x + ", " + y + ", " + z + "] but got [" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + "]";
		assertTrue(msg, vector.subtract(x, y, z).lengthSquared() < 0.001);
	}

	private void test2D(float angle, float x, float y) {
		assert2D(angle, VectorMath.getDirection2D(angle), x, y);
	}

	private void test3D(float yaw, float pitch, float x, float y, float z) {
		assert3D(yaw, pitch, VectorMath.getDirection3D(yaw, pitch), x, y, z);
	}

	@Test
	public void test3DAxis() {
		test3D(0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
		test3D((float) TrigMath.HALF_PI, (float) TrigMath.PI, 0.0f, 0.0f, -1.0f);
		test3D((float) TrigMath.QUARTER_PI, (float) TrigMath.QUARTER_PI, 0.5f, (float) TrigMath.HALF_SQRTOFTWO, 0.5f);
		test3D(0.0f, (float) TrigMath.HALF_PI, 0.0f, 1.0f, 0.0f);
		test3D(0.0f, (float) TrigMath.THREE_PI_HALVES, 0.0f, -1.0f, 0.0f);
		// verify that the 2D axis are the same for 3D axis without pitch
		float step = (float) (TrigMath.TWO_PI / 50.0); //50 steps in the circle
		for (float i = (float) -TrigMath.PI; i < TrigMath.TWO_PI; i += step) {
			Vector2 vec2D = VectorMath.getDirection2D(i);
			Vector2 vec3D = VectorMath.getDirection3D(i, 0.0f).toVector2();
			assertEquals("[2D-3D test] angle=" + i + " expected " + vec2D + " but was " + vec3D, vec2D, vec3D);
		}
	}

	@Test
	public void test2DAxis() {
		test2D(0.0f, 1.0f, 0.0f);
		test2D((float) TrigMath.HALF_PI, 0.0f, 1.0f);
		test2D((float) TrigMath.PI, -1.0f, 0.0f);
		test2D((float) TrigMath.THREE_PI_HALVES, 0.0f, -1.0f);
		test2D((float) TrigMath.QUARTER_PI, (float) TrigMath.HALF_SQRTOFTWO, (float) TrigMath.HALF_SQRTOFTWO);
	}

	private void testValue(double value, double result, double realValue) {
		assertTrue("value=" + value + " expected " + realValue + " but got " + result, Math.abs(result - realValue) < 0.0000001);
	}

	private void testAsin(double value) {
		testValue(value, TrigMath.asin(value), Math.asin(value));
	}

	private void testAcos(double value) {
		testValue(value, TrigMath.acos(value), Math.acos(value));
	}

	private void testAsec(double value) {
		testValue(value, TrigMath.asec(value), Math.acos(1 / value));
	}

	private void testAcosec(double value) {
		testValue(value, TrigMath.acsc(value), Math.asin(1 / value));
	}

	private void testAtan(double value) {
		testValue(value, TrigMath.atan(value), Math.atan(value));
	}

	private void testAtan2(double y, double x) {
		double realValue = Math.atan2(y, x);
		double result = TrigMath.atan2(y, x);
		assertTrue("x=" + x + ",y=" + y + " expected " + realValue + " but got " + result, Math.abs(result - realValue) < 0.0000001);
	}

	@Test
	public void testAsinAcos() {
		double step = 2.0 / 100.0;
		for (double i = -1.0; i <= 1.0; i += step) {
			testAsin(i);
			testAcos(i);
		}
	}

	@Test
	public void testAsecAcosec() {
		double step = 4.0 / 100.0;
		for (double i = -2.0; i <= -1; i += step) {
			testAsec(i);
			testAcosec(i);
		}
		for (double i = 1; i <= 2; i += step) {
			testAsec(i);
			testAcosec(i);
		}
	}

	@Test
	public void testAtan() {
		double step = 0.1;
		for (double i = -10.0; i <= 10.0; i += step) {
			testAtan(i);
		}
	}

	@Test
	public void testAtan2() {
		double step = 0.2;
		for (double x = -5.0; x <= 5.0; x += step) {
			for (double y = -5.0; y <= 5.0; y += step) {
				testAtan2(y, x);
			}
		}
	}
}
