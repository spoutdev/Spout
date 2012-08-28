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

import java.util.Random;

/**
 * A class designed for Sinus operations using a table lookup system
 */
public class SinusHelper {
	private static final BitSize SIN_SCALE = new BitSize(16); // used to compute the size and mask to use for sin
	private static final float[] SIN_TABLE = new float[SIN_SCALE.SIZE];
	private static float SIN_CONVERSION_FACTOR = (float) (SIN_SCALE.SIZE / MathHelper.TWO_PI);
	private static final int COS_OFFSET = SIN_SCALE.SIZE / 4;

	static {
		for (int i = 0; i < SIN_SCALE.SIZE; i++) {
			SIN_TABLE[i] = (float) Math.sin((i * MathHelper.TWO_PI) / SIN_SCALE.SIZE);
		}
	}

	private static float sinRaw(int idx) {
		return SIN_TABLE[idx & SIN_SCALE.MASK];
	}

	private static float cosRaw(int idx) {
		return SIN_TABLE[(idx + COS_OFFSET) & SIN_SCALE.MASK];
	}

	private static Vector3 get3DAxisRaw(int yawIdx, int pitchIdx) {
		float yFact = cosRaw(pitchIdx);
		return new Vector3(yFact * sinRaw(yawIdx), sinRaw(pitchIdx), yFact * cosRaw(yawIdx));	
	}

	private static Vector2 get2DAxisRaw(int angleIdx) {
		return new Vector2(sinRaw(angleIdx), cosRaw(angleIdx));
	}

	/**
	 * Gets a random 3D axis of a random pitch and yaw using the random specified
	 * 
	 * @param random to use
	 * @return the 3D axis of a random direction
	 */
	public static Vector3 getRandom3DAxis(Random random) {
		return get3DAxisRaw(random.nextInt(SIN_SCALE.SIZE), random.nextInt(SIN_SCALE.SIZE));
	}

	/**
	 * Gets the 3D axis of yaw and pitch angles
	 * 
	 * @param yaw of the rotation in radians
	 * @param pitch of the rotation in radians
	 * @return the 3D axis
	 */
	public static Vector3 get3DAxis(float yaw, float pitch) {
		return get3DAxisRaw(MathHelper.floor(yaw * SIN_CONVERSION_FACTOR), MathHelper.floor(pitch * SIN_CONVERSION_FACTOR));	
	}

	/**
	 * Gets a random 2D axis of a random angle using the random specified
	 * 
	 * @param random to use
	 * @return the 2D axis of a random angle
	 */
	public static Vector2 getRandom2DAxis(Random random) {
		return get2DAxisRaw(random.nextInt(SIN_SCALE.SIZE));
	}

	/**
	 * Gets the 2D axis of a certain angle
	 * 
	 * @param angle in radians
	 * @return the 2D axis
	 */
	public static Vector2 get2DAxis(float angle) {
		return get2DAxisRaw(MathHelper.floor(angle * SIN_CONVERSION_FACTOR));
	}

	/**
	 * Tangent calculations using a table.<br>
	 * <i>sin(angle) / cos(angle)</i><br><br>
	 * 
	 * <b>No interpolation is performed:</b> Accuracy is up to the 5th decimal place
	 * 
	 * @param angle in radians
	 * @return the tangent of the angle
	 */
	public static float tan(float angle) {
		int idx = MathHelper.floor(angle * SIN_CONVERSION_FACTOR);
		return sinRaw(idx) / cosRaw(idx);
	}

	/**
	 * Cotangent calculations using a table.<br>
	 * <i>cos(angle) / sin(angle)</i><br><br>
	 * 
	 * <b>No interpolation is performed:</b> Accuracy is up to the 5th decimal place
	 * 
	 * @param angle in radians
	 * @return the cotangent of the angle
	 */
	public static float cot(float angle) {
		int idx = MathHelper.floor(angle * SIN_CONVERSION_FACTOR);
		return cosRaw(idx) / sinRaw(idx);
	}

	/**
	 * Secant calculations using a table:<br>
	 * <i>1 / cos(angle)</i><br><br>
	 * 
	 * <b>No interpolation is performed:</b> Accuracy is up to the 5th decimal place
	 * 
	 * @param angle the angle in radians
	 * @return the secant of the angle
	 */
	public static float sec(float angle) {
		return 1.0f / cos(angle);
	}

	/**
	 * Cosecant calculations using a table.<br>
	 * <i>1 / sin(angle)</i><br><br>
	 * 
	 * <b>No interpolation is performed:</b> Accuracy is up to the 5th decimal place
	 * 
	 * @param angle the angle in radians
	 * @return the cosecant of the angle
	 */
	public static float cosec(float angle) {
		return 1.0f / sin(angle);
	}

	/**
	 * Sinus calculation using a table.<br>
	 * For double-precision sin values, use the MathHelper sin function<br><br>
	 * 
	 * <b>No interpolation is performed:</b> Accuracy is up to the 5th decimal place
	 * 
	 * @param angle the angle in radians
	 * @return the sinus of the angle
	 */
	public static float sin(float angle) {
		return sinRaw(MathHelper.floor(angle * SIN_CONVERSION_FACTOR));
	}

	/**
	 * Cosinus calculation using a table.<br>
	 * For double-precision cos values, use the MathHelper cos function<br><br>
	 * 
	 * <b>No interpolation is performed:</b> Accuracy is up to the 5th decimal place
	 * 
	 * @param angle the angle in radians
	 * @return the cosinus of the angle
	 */
	public static float cos(float angle) {
		return cosRaw(MathHelper.floor(angle * SIN_CONVERSION_FACTOR));
	}
}
