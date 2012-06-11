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
	 * This method takes a list and constructs a transform from it.
	 * @param list the list of floats representing a transform.
	 * @return the constructed transform from NBT
	 */
	public static Transform nbtToTransform(World world, List<? extends FloatTag> list) {
		//Position
		float px = SafeCast.toFloat(list.get(0).getValue(), 1f);
		float py = SafeCast.toFloat(list.get(1).getValue(), 85f);
		float pz = SafeCast.toFloat(list.get(2).getValue(), 1f);
		//Rotation
		float rw = SafeCast.toFloat(list.get(3).getValue(), 1f);
		float rx = SafeCast.toFloat(list.get(4).getValue(), 1f);
		float ry = SafeCast.toFloat(list.get(5).getValue(), 1f);
		float rz = SafeCast.toFloat(list.get(6).getValue(), 1f);
		//Scale
		float sx = SafeCast.toFloat(list.get(7).getValue(), 1f);
		float sy = SafeCast.toFloat(list.get(8).getValue(), 1f);
		float sz = SafeCast.toFloat(list.get(9).getValue(), 1f);
		return new Transform(new Point(world, px, py, pz), new Quaternion(rw, rx, ry, rz), new Vector3(sx, sy, sz));
	}

	/**
	 * This method takes a transform and converts it into a series of float tags representing a transform. The world object
	 * within the position object composed in this transform will not be saved.
	 * @param transform The transform to convert to tags
	 * @return list composed of float tags representing the transform
	 */
	public static List<FloatTag> transformToNBT(Transform transform) {
		ArrayList<FloatTag> list = new ArrayList<FloatTag>(10);
		Vector3 point = transform.getPosition();
		Quaternion rotation = transform.getRotation();
		Vector3 scale = transform.getScale();
		//Position
		list.add(new FloatTag("px", point.getX()));
		list.add(new FloatTag("py", point.getY()));
		list.add(new FloatTag("pz", point.getZ()));
		//Rotation
		list.add(new FloatTag("rw", rotation.getW()));
		list.add(new FloatTag("rx", rotation.getX()));
		list.add(new FloatTag("ry", rotation.getY()));
		list.add(new FloatTag("rz", rotation.getZ()));
		//Scale
		list.add(new FloatTag("sx", scale.getX()));
		list.add(new FloatTag("sy", scale.getY()));
		list.add(new FloatTag("sz", scale.getZ()));
		//Return list of floats
		return list;
	}

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
