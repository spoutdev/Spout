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

import java.util.ArrayList;

import org.spout.api.geo.discrete.Point;

public class DiscSpawnArrangement extends GenericSpawnArrangement {
	private final float scale;

	public DiscSpawnArrangement(Point center, int number, float scale) {
		super(center, number);
		this.scale = scale;
	}

	@Override
	public Point[] generatePoints(Point center, int number) {

		ArrayList<Integer> shells = new ArrayList<>();

		int remaining = number;
		int shell = 0;
		while (remaining > 0) {
			int toAdd;
			if (shell == 0) {
				if (number == 2 || number == 3 || number == 4) {
					toAdd = 0;
				} else {
					toAdd = 1;
				}
			} else {
				toAdd = shell * 3;
			}
			if (toAdd > remaining) {
				toAdd = remaining;
			}
			shells.add(toAdd);
			remaining -= toAdd;
			shell++;
		}

		if (shells.size() > 1) {
			int lastIndex = shells.size() - 1;
			int last = shells.get(lastIndex);
			int secondLast = shells.get(lastIndex - 1);
			if (last < secondLast) {
				if (last >= secondLast - 2 && last > 2) {
					shells.set(lastIndex, secondLast);
					shells.set(lastIndex - 1, last);
				} else {
					shells.set(lastIndex, 0);
				}
				int i = lastIndex - 1;
				while (last > 0) {
					shells.set(i, shells.get(i) + 1);
					last--;
					i = (i == 1) ? (lastIndex - 1) : (i - 1);
				}
			}
		}

		Point[] points = new Point[number];

		int i = 0;

		for (int j = 0; j < shells.size(); j++) {
			Point[] shellPoints = new CircleSpawnArrangement(center, shells.get(j), j * scale, (j & 1) == 0).getArrangement();
			for (Point p : shellPoints) {
				points[i++] = p;
			}
		}

		return points;
	}
}
