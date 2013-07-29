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

public class SpiralSpawnArrangement extends GenericSpawnArrangement {
	private final float scaleRadius;
	private final float scaleCircumference;

	public SpiralSpawnArrangement(Point center, int number, float scale) {
		this(center, number, scale, 1.0F);
	}

	public SpiralSpawnArrangement(Point center, int number, float scaleRadius, float scaleCircumference) {
		super(center, number);
		this.scaleRadius = scaleRadius;
		this.scaleCircumference = scaleCircumference;
	}

	@Override
	public Point[] generatePoints(Point center, int number) {

		float angle = 0;
		float distance;

		Point[] points = new Point[number];

		points[0] = center;

		for (int i = 1; i < number; i++) {
			distance = (float) Math.sqrt(i);
			final Vector3 offset = Matrix3.createRotation(Quaternion.fromAngleDegAxis(angle, 0, 1, 0))
					.transform(Vector3.FORWARD).mul(distance * scaleRadius);

			points[i] = center.add(offset);

			angle += scaleCircumference * 360.0 / (Math.PI * distance);
		}

		return points;
	}
}
