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
package org.spout.api.util;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.sanitation.SafeCast;

import org.spout.nbt.FloatTag;
import org.spout.nbt.Tag;

/**
 * Utility class to map out complex objects into NBT structures vice-versa.
 */
public class NBTMapper {

	/**
	 * Takes in an NBT tag, sanely checks null status, and then returns its value.
	 * @param t Tag to get value from
	 * @return tag value as an object or null if no value
	 */
	public static Object toTagValue(Tag t) {
		if (t == null) {
			return null;
		} else {
			return t.getValue();
		}
	}

	/**
	 * Takes in an NBT tag, sanely checks null status, and then returns it value.
	 * This method will return null if the value cannot be cast to the given class.
	 *
	 * @param t Tag to get the value from
	 * @param clazz the return type to use
	 * @return the value as an onbject of the same type as the given class
	 */
	public static <T> T getTagValue(Tag t, Class<? extends T> clazz) {
		Object o = toTagValue(t);
		if (o == null) {
			return null;
		}
		try {
			return clazz.cast(o);
		} catch (ClassCastException e) {
			return null;
		}
	}

	/**
	 * Takes in an NBT tag, sanely checks null status, and then returns it value.
	 * This method will return null if the value cannot be cast to the default value.
	 *
	 * @param t Tag to get the value from
	 * @param defaultValue the value to return if the tag or its value is null or the value cannot be cast
	 * @return the value as an onbject of the same type as the default value, or the default value
	 */
	public static <T, U extends T> T toTagValue(Tag t, Class<? extends T> clazz, U defaultValue) {
		Object o = toTagValue(t);
		if (o == null) {
			return defaultValue;
		}
		try {
			T value = clazz.cast(o);
			return value;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}


}
