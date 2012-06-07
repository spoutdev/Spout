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
package org.spout.api.entity.spawn;

import org.spout.api.entity.type.ControllerType;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;

public class SpiralSpawnArrangement extends GenericSpawnArrangement {

	private final float scaleRadius;
	private final float scaleCircumference;

	public SpiralSpawnArrangement(Point center, ControllerType type, int number, float scale) {
		this(center, type, number, scale, 1.0F);
	}
	
	public SpiralSpawnArrangement(Point center, ControllerType type, int number, float scaleRadius, float scaleCircumference) {
		super(center, type, number);
		this.scaleRadius = scaleRadius;
		this.scaleCircumference = scaleCircumference;
	}
	
	public SpiralSpawnArrangement(Point center, ControllerType[] types, float scale) {
		this(center, types, scale, scale);
	}

	public SpiralSpawnArrangement(Point center, ControllerType[] types, float scaleRadius, float scaleCircumference) {
		super(center, types);
		this.scaleRadius = scaleRadius;
		this.scaleCircumference = scaleCircumference;
	}
	
	public Point[] generatePoints(Point center, int number) {
		
		float angle = 0;
		float distance = 1;
		
		Point[] points = new Point[number];
		
		points[0] = center;
		
		for (int i = 1; i < number; i++) {
			distance = (float)Math.sqrt(i);
			
			Vector3 offset = Point.FORWARD.transform(MathHelper.rotateY(angle));
			offset = offset.multiply(distance).multiply(scaleRadius);
			
			points[i] = center.add(offset);
			
			angle += scaleCircumference * 360.0 / (Math.PI * distance);
		}
		
		return points;
	}

}
