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
package org.spout.math.test.imaginary;

import org.junit.Assert;
import org.junit.Test;

import org.spout.math.TrigMath;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix3;
import org.spout.math.test.TestUtil;
import org.spout.math.vector.Vector3;

public class QuaternionTest {
	@Test
	public void testDefaultConstructor() {
		Quaternion quaternion = new Quaternion();
		TestUtil.assertEquals(quaternion, 0, 0, 0, 1);
	}

	@Test
	public void testDoubleComponentsConstructor() {
		Quaternion quaternion = new Quaternion(1d, 2d, 3d, 4d);
		TestUtil.assertEquals(quaternion, 1, 2, 3, 4);
	}

	@Test
	public void testFloatComponentsConstructor() {
		Quaternion quaternion = new Quaternion(1, 2, 3, 4);
		TestUtil.assertEquals(quaternion, 1, 2, 3, 4);
	}

	@Test
	public void testCopyConstructor() {
		Quaternion quaternion = new Quaternion(new Quaternion(1, 2, 3, 4));
		TestUtil.assertEquals(quaternion, 1, 2, 3, 4);
	}

	@Test
	public void testGetters() {
		Quaternion quaternion = new Quaternion(1, 2, 3, 4);
		TestUtil.assertEquals(quaternion.getX(), 1);
		TestUtil.assertEquals(quaternion.getY(), 2);
		TestUtil.assertEquals(quaternion.getZ(), 3);
		TestUtil.assertEquals(quaternion.getW(), 4);
	}

	@Test
	public void testQuaternionAddition() {
		Quaternion quaternion = new Quaternion(0, 1, 1, 1).add(new Quaternion(5.5f, -0.5f, 3.8f, 5.5f));
		TestUtil.assertEquals(quaternion, 5.5f, 0.5f, 4.8f, 6.5f);
	}

	@Test
	public void testDoubleComponentsAddition() {
		Quaternion quaternion = new Quaternion(0, 1, 1, 1).add(5.5, -0.5, 3.8, 5.5);
		TestUtil.assertEquals(quaternion, 5.5f, 0.5f, 4.8f, 6.5f);
	}

	@Test
	public void testFloatComponentsAddition() {
		Quaternion quaternion = new Quaternion(0, 1, 1, 1).add(5.5f, -0.5f, 3.8f, 5.5f);
		TestUtil.assertEquals(quaternion, 5.5f, 0.5f, 4.8f, 6.5f);
	}

	@Test
	public void testQuaternionSubtraction() {
		Quaternion quaternion = new Quaternion(10, 5, 1, 1).sub(new Quaternion(9, 4.5, 2, 1));
		TestUtil.assertEquals(quaternion, 1, 0.5f, -1, 0);
	}

	@Test
	public void testDoubleComponentsSubtraction() {
		Quaternion quaternion = new Quaternion(10, 5, 1, 1).sub(9, 4.5, 2, 1);
		TestUtil.assertEquals(quaternion, 1, 0.5f, -1, 0);
	}

	@Test
	public void testFloatComponentsSubtraction() {
		Quaternion quaternion = new Quaternion(10, 5, 1, 1).sub(9, 4.5f, 2f, 1f);
		TestUtil.assertEquals(quaternion, 1, 0.5f, -1, 0);
	}

	@Test
	public void testDoubleFactorMultiplication() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).mul(1.5);
		TestUtil.assertEquals(quaternion, 3, 4.5f, 6, 7.5f);
	}

	@Test
	public void testFloatFactorMultiplication() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).mul(1.5f);
		TestUtil.assertEquals(quaternion, 3, 4.5f, 6, 7.5f);
	}

	@Test
	public void testQuaternionMultiplication() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).mul(new Quaternion(1, 6, 7, 8));
		TestUtil.assertEquals(quaternion, 18, 44, 76, -8);
	}

	@Test
	public void testDoubleComponentsMultiplication() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).mul(2d);
		TestUtil.assertEquals(quaternion, 4, 6, 8, 10);
	}

	@Test
	public void testFloatComponentsMultiplication() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).mul(2);
		TestUtil.assertEquals(quaternion, 4, 6, 8, 10);
	}

	@Test
	public void testDoubleFactorDivision() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).div(2d);
		TestUtil.assertEquals(quaternion, 1, 1.5f, 2, 2.5f);
	}

	@Test
	public void testFloatFactorDivision() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).div(2);
		TestUtil.assertEquals(quaternion, 1, 1.5f, 2, 2.5f);
	}

	@Test
	public void testQuaternionDotProduct() {
		float f = new Quaternion(2, 3, 4, 5).dot(new Quaternion(6, 7, 8, 9));
		TestUtil.assertEquals(f, 110);
	}

	@Test
	public void testDoubleComponentsDotProduct() {
		float f = new Quaternion(2, 3, 4, 5).dot(6d, 7d, 8d, 9d);
		TestUtil.assertEquals(f, 110);
	}

	@Test
	public void testFloatComponentsDotProduct() {
		float f = new Quaternion(2, 3, 4, 5).dot(6, 7, 8, 9);
		TestUtil.assertEquals(f, 110);
	}

	@Test
	public void testDirection() {
		Vector3 vector = new Quaternion((float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO).getDirection();
		TestUtil.assertEquals(vector, 0, -1, 0);
	}

	@Test
	public void testAxesAnglesDegrees() {
		Vector3 vector1 = new Quaternion((float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO).getAxesAngleDeg();
		TestUtil.assertEquals(vector1, 90, 0, 0);
		Vector3 vector2 = new Quaternion(0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO).getAxesAngleDeg();
		TestUtil.assertEquals(vector2, 0, 90, 0);
		Vector3 vector3 = new Quaternion(0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO).getAxesAngleDeg();
		TestUtil.assertEquals(vector3, 0, 0, 90);
	}

	@Test
	public void testAxesAnglesRadians() {
		Vector3 vector1 = new Quaternion((float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO).getAxesAnglesRad();
		TestUtil.assertEquals(vector1, (float) TrigMath.HALF_PI, 0, 0);
		Vector3 vector2 = new Quaternion(0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO).getAxesAnglesRad();
		TestUtil.assertEquals(vector2, 0, (float) TrigMath.HALF_PI, 0);
		Vector3 vector3 = new Quaternion(0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO).getAxesAnglesRad();
		TestUtil.assertEquals(vector3, 0, 0, (float) TrigMath.HALF_PI);
	}

	@Test
	public void testConjugate() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).conjugate();
		TestUtil.assertEquals(quaternion, -2, -3, -4, 5);
	}

	@Test
	public void testInvert() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).invert();
		TestUtil.assertEquals(quaternion, -0.0370370370f, -0.0555555555f, -0.0740740740f, 0.0925925925f);
	}

	@Test
	public void testLengthSquared() {
		float f = new Quaternion(2, 3, 4, 5).lengthSquared();
		TestUtil.assertEquals(f, 54);
	}

	@Test
	public void testLength() {
		float f = new Quaternion(2, 3, 4, 5).length();
		TestUtil.assertEquals(f, 7.3484692283f);
	}

	@Test
	public void testNormalize() {
		Quaternion quaternion = new Quaternion(2, 3, 4, 5).normalize();
		TestUtil.assertEquals(quaternion, 0.2721655269f, 0.4082482904f, 0.5443310539f, 0.68041381744f);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(new Quaternion(122, 43, 96, 50).equals(new Quaternion(122, 43, 96, 50)));
		Assert.assertFalse(new Quaternion(122, 43, 96, 50).equals(new Quaternion(378, 95, 96, 0)));
	}

	@Test
	public void testComparison() {
		int c1 = new Quaternion(10, 20, 30, 40).compareTo(new Quaternion(20, 20, 30, 40));
		Assert.assertTrue(c1 < 0);
		int c2 = new Quaternion(10, 20, 30, 40).compareTo(new Quaternion(10, 20, 30, 40));
		Assert.assertTrue(c2 == 0);
		int c3 = new Quaternion(10, 20, 30, 40).compareTo(new Quaternion(10, 10, 30, 40));
		Assert.assertTrue(c3 > 0);
	}

	@Test
	public void testCloning() {
		Quaternion quaternion = new Quaternion(3, 2, 5, 6);
		Assert.assertEquals(quaternion, quaternion.clone());
	}

	@Test
	public void testCreateFromAxesDoubleAnglesDegrees() {
		Quaternion quaternion1 = Quaternion.fromAxesAnglesDeg(90d, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAxesAnglesDeg(0, 90d, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAxesAnglesDeg(0, 0, 90d);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromAxesDoubleAnglesRadians() {
		Quaternion quaternion1 = Quaternion.fromAxesAnglesRad(TrigMath.HALF_PI, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAxesAnglesRad(0, TrigMath.HALF_PI, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAxesAnglesRad(0, 0, TrigMath.HALF_PI);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromAxesFloatAnglesDegrees() {
		Quaternion quaternion1 = Quaternion.fromAxesAnglesDeg(90, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAxesAnglesDeg(0, 90, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAxesAnglesDeg(0, 0, 90);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromAxesFloatAnglesRadians() {
		Quaternion quaternion1 = Quaternion.fromAxesAnglesRad((float) TrigMath.HALF_PI, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAxesAnglesRad(0, (float) TrigMath.HALF_PI, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAxesAnglesRad(0, 0, (float) TrigMath.HALF_PI);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromRotationBetweenTwoVector3() {
		Quaternion quaternion1 = Quaternion.fromRotationTo(new Vector3(0, 1, 0), new Vector3(0, 0, 1));
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromRotationTo(new Vector3(0, 0, 1), new Vector3(1, 0, 0));
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromRotationTo(new Vector3(1, 0, 0), new Vector3(0, 1, 0));
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromDoubleAngleDegreesVectorAxis() {
		Quaternion quaternion1 = Quaternion.fromAngleDegAxis(90d, new Vector3(1, 0, 0));
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleDegAxis(90d, new Vector3(0, 1, 0));
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleDegAxis(90d, new Vector3(0, 0, 1));
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromDoubleAngleRadiansVectorAxis() {
		Quaternion quaternion1 = Quaternion.fromAngleRadAxis(TrigMath.HALF_PI, new Vector3(1, 0, 0));
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleRadAxis(TrigMath.HALF_PI, new Vector3(0, 1, 0));
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleRadAxis(TrigMath.HALF_PI, new Vector3(0, 0, 1));
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromFloatAngleDegreesVectorAxis() {
		Quaternion quaternion1 = Quaternion.fromAngleDegAxis(90, new Vector3(1, 0, 0));
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleDegAxis(90, new Vector3(0, 1, 0));
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleDegAxis(90, new Vector3(0, 0, 1));
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromFloatAngleRadiansVectorAxis() {
		Quaternion quaternion1 = Quaternion.fromAngleRadAxis((float) TrigMath.HALF_PI, new Vector3(1, 0, 0));
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleRadAxis((float) TrigMath.HALF_PI, new Vector3(0, 1, 0));
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleRadAxis((float) TrigMath.HALF_PI, new Vector3(0, 0, 1));
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromDoubleAngleDegreesAxisDoubleComponents() {
		Quaternion quaternion1 = Quaternion.fromAngleDegAxis(90d, 1d, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleDegAxis(90d, 0, 1d, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleDegAxis(90d, 0, 0, 1d);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromDoubleAngleRadiansAxisDoubleComponents() {
		Quaternion quaternion1 = Quaternion.fromAngleRadAxis(TrigMath.HALF_PI, 1d, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleRadAxis(TrigMath.HALF_PI, 0, 1d, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleRadAxis(TrigMath.HALF_PI, 0, 0, 1d);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromFloatAngleDegreesAxisFloatComponents() {
		Quaternion quaternion1 = Quaternion.fromAngleDegAxis(90, 1, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleDegAxis(90, 0, 1, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleDegAxis(90, 0, 0, 1);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	@Test
	public void testCreateFromFloatAngleRadiansAxisFloatComponents() {
		Quaternion quaternion1 = Quaternion.fromAngleRadAxis((float) TrigMath.HALF_PI, 1, 0, 0);
		TestUtil.assertEquals(quaternion1, (float) TrigMath.HALF_SQRT_OF_TWO, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion2 = Quaternion.fromAngleRadAxis((float) TrigMath.HALF_PI, 0, 1, 0);
		TestUtil.assertEquals(quaternion2, 0, (float) TrigMath.HALF_SQRT_OF_TWO, 0, (float) TrigMath.HALF_SQRT_OF_TWO);
		Quaternion quaternion3 = Quaternion.fromAngleRadAxis((float) TrigMath.HALF_PI, 0, 0, 1);
		TestUtil.assertEquals(quaternion3, 0, 0, (float) TrigMath.HALF_SQRT_OF_TWO, (float) TrigMath.HALF_SQRT_OF_TWO);
	}

	public void testCreateFromRotationMatrix3() {
		final Quaternion quaternion = Quaternion.fromAngleDegAxis(45, 1, 1, -1);
		final Matrix3 matrix = Matrix3.createRotation(quaternion);
		Assert.assertEquals(quaternion, Quaternion.fromRotationMatrix(matrix));
	}
}
