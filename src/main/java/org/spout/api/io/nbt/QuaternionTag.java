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
package org.spout.api.io.nbt;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.math.Quaternion;
import org.spout.nbt.FloatTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.Tag;
import org.spout.nbt.util.NBTMapper;

public class QuaternionTag extends ListTag<FloatTag> {

	public QuaternionTag(String name, Quaternion q) {
		super(name, FloatTag.class, quaternionToList(q));
	}
	
	private static List<FloatTag> quaternionToList(Quaternion q) {
		List<FloatTag> list = new ArrayList<FloatTag>(4);
		list.add(new FloatTag("", q.getX()));
		list.add(new FloatTag("", q.getY()));
		list.add(new FloatTag("", q.getZ()));
		list.add(new FloatTag("", q.getW()));
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static Quaternion getValue(Tag<?> tag) {
		try {
			return getValue((ListTag<FloatTag>) tag);
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	public static Quaternion getValue(ListTag<FloatTag> list) {
		if (list == null) {
			return null;
		}
		return getValue(list.getValue());
	}
	
	public static Quaternion getValue(List<FloatTag> list) {
		if (list == null || list.size() != 4) {
			return null;
		}
		Float x = NBTMapper.toTagValue(list.get(0), Float.class, null);
		Float y = NBTMapper.toTagValue(list.get(1), Float.class, null);
		Float z = NBTMapper.toTagValue(list.get(2), Float.class, null);
		Float w = NBTMapper.toTagValue(list.get(3), Float.class, null);
		if (x == null || y == null || z == null || w == null) {
			return null;
		}
		return new Quaternion(x, y, z, w, true);
	}

}
