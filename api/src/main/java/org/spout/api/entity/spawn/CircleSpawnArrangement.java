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
package org.spout.api.entity.spawn;

import org.spout.api.geo.discrete.Point;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix3;
import org.spout.math.vector.Vector3;

public class CircleSpawnArrangement extends GenericSpawnArrangement {
	private final boolean halfRotate;
	private final float radius;

	public CircleSpawnArrangement(Point center, int number, float radius, boolean halfRotate) {
		super(center, number);
		this.halfRotate = halfRotate;
		this.radius = radius;
	}

	@Override
	protected Point[] generatePoints(Point center, int number) {
		Vector3 offset = Point.FORWARD.mul(radius);
		int angle = number == 0 ? 0 : (360 / number);
		Matrix3 rotate = Matrix3.createRotation(Quaternion.fromAngleDegAxis(angle, 0, 1, 0));
		if (halfRotate) {
			offset = Matrix3.createRotation(Quaternion.fromAngleDegAxis(angle * 0.5f, 0, 1, 0)).transform(offset);
		}
		Point[] points = new Point[number];
		for (int i = 0; i < number; i++) {
			points[i] = center.add(offset);
			offset = rotate.transform(offset);
		}
		return points;
	}
}