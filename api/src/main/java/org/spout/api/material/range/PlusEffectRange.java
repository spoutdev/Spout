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
package org.spout.api.material.range;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.math.IntVector3;

public class PlusEffectRange extends ListEffectRange {
	public PlusEffectRange(int range, boolean includeAboveBelow) {
		super(createList(range, includeAboveBelow));
	}

	private static List<IntVector3> createList(int range, boolean includeAboveBelow) {
		List<IntVector3> list = new ArrayList<>();
		list.add(new IntVector3(0, 0, 0));
		if (includeAboveBelow) {
			list.add(new IntVector3(0, 1, 0));
			list.add(new IntVector3(0, -1, 0));
		}
		for (int i = 1; i <= range; i++) {
			list.add(new IntVector3(0, 0, i));
			list.add(new IntVector3(0, 0, -i));
			list.add(new IntVector3(i, 0, 0));
			list.add(new IntVector3(-i, 0, 0));
		}
		return list;
	}
}
