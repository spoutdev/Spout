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

import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.geo.discrete.Point;

public abstract class GenericSpawnArrangement implements SpawnArrangement {
	private final AtomicReference<Point[]> points;
	private final Point center;
	private final int number;

	public GenericSpawnArrangement(Point center, int number) {
		this.points = new AtomicReference<>(null);
		this.center = center;
		this.number = number;
	}

	protected abstract Point[] generatePoints(Point center, int number);

	@Override
	public Point[] getArrangement() {
		Point[] p = points.get();
		if (p == null) {
			p = generatePoints(center, number);
			Point[] old = points.getAndSet(p);
			if (old != null) {
				p = old;
			}
		}
		return p;
	}
}
