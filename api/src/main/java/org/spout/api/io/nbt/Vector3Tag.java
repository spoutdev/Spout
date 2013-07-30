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
package org.spout.api.io.nbt;

import java.util.ArrayList;
import java.util.List;

import org.spout.math.vector.Vector3;
import org.spout.nbt.FloatTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.Tag;
import org.spout.nbt.util.NBTMapper;

public class Vector3Tag extends ListTag<FloatTag> {
	public Vector3Tag(String name, Vector3 v) {
		super(name, FloatTag.class, vector3ToList(v));
	}

	private static List<FloatTag> vector3ToList(Vector3 v) {
		List<FloatTag> list = new ArrayList<>(3);
		list.add(new FloatTag("", v.getX()));
		list.add(new FloatTag("", v.getY()));
		list.add(new FloatTag("", v.getZ()));
		return list;
	}

	@SuppressWarnings ("unchecked")
	public static Vector3 getValue(Tag<?> tag) {
		try {
			return getValue((ListTag<FloatTag>) tag);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Vector3 getValue(ListTag<FloatTag> list) {
		if (list == null) {
			return null;
		}
		return getValue(list.getValue());
	}

	public static Vector3 getValue(List<FloatTag> list) {
		if (list == null || list.size() != 3) {
			return null;
		}
		Float x = NBTMapper.toTagValue(list.get(0), Float.class, null);
		Float y = NBTMapper.toTagValue(list.get(1), Float.class, null);
		Float z = NBTMapper.toTagValue(list.get(2), Float.class, null);

		if (x == null || y == null || z == null) {
			return null;
		}

		return new Vector3(x, y, z);
	}
}
