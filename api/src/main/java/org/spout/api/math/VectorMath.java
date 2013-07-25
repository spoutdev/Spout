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

import java.util.Random;

/**
 * Class containing vector mathematical functions.
 */
public class VectorMath {
	private VectorMath() {
	}

	/**
	 * Gets the direction vector of a random pitch and yaw using the random specified.
	 *
	 * @param random to use
	 * @return the random direction vector
	 */
	public static Vector3 getRandomDirection3D(Random random) {
		return getDirection3D(random.nextFloat() * (float) TrigMath.TWO_PI,
				random.nextFloat() * (float) TrigMath.TWO_PI);
	}

	/**
	 * Gets the direction vector of a certain yaw and pitch.
	 *
	 * @param azimuth in radians
	 * @param inclination in radians
	 * @return the random direction vector
	 */
	public static Vector3 getDirection3D(float azimuth, float inclination) {
		final float yFact = TrigMath.cos(inclination);
		return new Vector3(yFact * TrigMath.cos(azimuth), TrigMath.sin(inclination), yFact * TrigMath.sin(azimuth));
	}

	/**
	 * Gets the direction vector of a random angle using the random specified.
	 *
	 * @param random to use
	 * @return the random direction vector
	 */
	public static Vector2 getRandomDirection2D(Random random) {
		return getDirection2D(random.nextFloat() * (float) TrigMath.TWO_PI);
	}

	/**
	 * Gets the direction vector of a certain angle.
	 *
	 * @param azimuth in radians
	 * @return the direction vector
	 */
	public static Vector2 getDirection2D(float azimuth) {
		return new Vector2(TrigMath.cos(azimuth), TrigMath.sin(azimuth));
	}

	/**
	 * Returns the forward vector transformed by the provided quaternion
	 *
	 * @param rot The rotation
	 * @return The forward vector for the rotation
	 */
	public static Vector3 getDirection(Quaternion rot) {
		return transform(Vector3.FORWARD, rot);
	}

	/**
	 * Calculates and returns a new Vector3 transformed by the given quaternion
	 *
	 * @param vector The vector to transform
	 * @param rot The rotation to apply
	 * @return The rotated vector
	 */
	public static Vector3 transform(Vector3 vector, Quaternion rot) {
		return transform(vector, MatrixMath.createRotated(rot));
	}

	/**
	 * Calculates and returns a new Vector3 transformed by the transformation matrix.
	 *
	 * @param v the vector to transform
	 * @param m the transformation matrix
	 * @return The transformed vector
	 */
	public static Vector3 transform(Vector3 v, Matrix m) {
		float[] vector = {v.getX(), v.getY(), v.getZ(), 1};
		float[] vres = new float[4];
		final int dimension = m.getDimension();
		for (int i = 0; i < dimension; i++) {
			vres[i] = 0;
			for (int k = 0; k < dimension; k++) {
				float n = m.get(i, k) * vector[k];
				vres[i] += n;
			}
		}

		return new Vector3(vres[0], vres[1], vres[2]);
	}
}
