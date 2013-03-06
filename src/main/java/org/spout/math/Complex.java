/*
 * This file is part of Math.
 *
 * Copyright (c) 2011-2013, Spout LLC <http://www.spout.org/>
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
package org.spout.math;

/**
 * Represents a 2D rotation
 */
public class Complex {
	protected final float x, y;
	/**
	 * Represents no rotation
	 */
	public static final Complex IDENTITY = new Complex(1, 0);
	/**
	 * Represents 90 degrees rotation
	 */
	public static final Complex UNIT = new Complex(0, 1);

	public Complex(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Complex(float angle) {
		this.x = (float) Math.cos(Math.toRadians(angle));
		this.y = (float) Math.sin(Math.toRadians(angle));
	}

	public float getReal() {
		return x;
	}

	public float getImaginary() {
		return y;
	}

	public Vector2 getDirection() {
		return new Vector2(x, y);
	}

	public float getAngle() {
		return (float) Math.toDegrees(Math.atan2(x, y));
	}

	/**
	 * Return a 3x3 rotation matrix
	 * @return
	 */
	public Matrix toMatrix() {
		/*
		 * [x, -y, 0]
		 * [y, x, 0]
		 * [0, 0, 1]
		 */
		Matrix res = new Matrix(3);
		res.set(0, 0, x);
		res.set(0, 1, -y);
		res.set(1, 0, y);
		res.set(1, 1, x);

		return res;
	}
}