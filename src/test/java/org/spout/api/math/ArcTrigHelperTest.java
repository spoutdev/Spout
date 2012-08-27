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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArcTrigHelperTest {
	private void testValue(double value, double result, double realValue) {
		assertTrue("value=" + value + " expected " + realValue + " but got " + result, Math.abs(result - realValue) < 0.0000001);
	}

	private void testAsin(double value) {
		testValue(value, ArcTrigHelper.asin(value), Math.asin(value));
	}

	private void testAcos(double value) {
		testValue(value, ArcTrigHelper.acos(value), Math.acos(value));
	}

	private void testAtan(double value) {
		testValue(value, ArcTrigHelper.atan(value), Math.atan(value));
	}

	private void testAtan2(double y, double x) {
		double realValue = Math.atan2(y, x);
		double result = ArcTrigHelper.atan2(y, x);
		assertTrue("x=" + x + ",y=" + y + " expected " + realValue + " but got " + result, Math.abs(result - realValue) < 0.0000001);
	}

	@Test
	public void testASinACos() {
		double step = 2.0 / 100.0;
		for (double i = -1.0; i <= 1.0; i += step) {
			testAsin(i);
			testAcos(i);
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
