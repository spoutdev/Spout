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
package org.spout.api.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SinusHelperTest {

	private void testValue(float angle, float result, float realValue) {
		assertTrue("angle=" + angle + " expected " + realValue + " but got " + result, Math.abs(result - realValue) < 0.0001);
	}

	private void testSin(float value) {
		testValue(value, SinusHelper.sin(value), (float) Math.sin(value));
	}

	private void testCos(float value) {
		testValue(value, SinusHelper.cos(value), (float) Math.cos(value));
	}

	@Test
	public void testSinCos() {
		float step = (float) (MathHelper.TWO_PI / 100.0); //100 steps in the circle
		for (float i = (float) -MathHelper.PI; i < MathHelper.TWO_PI; i += step) {
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
		assert2D(angle, SinusHelper.get2DAxis(angle), x, y);
	}

	private void test3D(float yaw, float pitch, float x, float y, float z) {
		assert3D(yaw, pitch, SinusHelper.get3DAxis(yaw, pitch), x, y, z);
	}

	@Test
	public void test3DAxis() {
		test3D(0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		test3D((float) MathHelper.HALF_PI, (float) MathHelper.PI, -1.0f, 0.0f, 0.0f);
		test3D((float) MathHelper.QUARTER_PI, (float) MathHelper.QUARTER_PI, 0.5f, (float) MathHelper.HALF_SQRTOFTWO, 0.5f);
		test3D(0.0f, (float) MathHelper.HALF_PI, 0.0f, 1.0f, 0.0f);
		test3D(0.0f, (float) MathHelper.THREE_PI_HALVES, 0.0f, -1.0f, 0.0f);
		// verify that the 2D axis are the same for 3D axis without pitch
		float step = (float) (MathHelper.TWO_PI / 50.0); //50 steps in the circle
		for (float i = (float) -MathHelper.PI; i < MathHelper.TWO_PI; i += step) {
			Vector2 vec2D = SinusHelper.get2DAxis(i);
			Vector2 vec3D = SinusHelper.get3DAxis(i, 0.0f).toVector2();
			assertEquals("[2D-3D test] angle=" + i + " expected " + vec2D + " but was " + vec3D, vec2D, vec3D);
		}
	}
	
	@Test
	public void test2DAxis() {
		test2D(0.0f, 0.0f, 1.0f);
		test2D((float) MathHelper.HALF_PI, 1.0f, 0.0f);
		test2D((float) MathHelper.PI, 0.0f, -1.0f);
		test2D((float) MathHelper.THREE_PI_HALVES, -1.0f, 0.0f);
		test2D((float) MathHelper.QUARTER_PI, (float) MathHelper.HALF_SQRTOFTWO, (float) MathHelper.HALF_SQRTOFTWO);
	}
}
