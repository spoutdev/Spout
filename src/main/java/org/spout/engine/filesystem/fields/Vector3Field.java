/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.filesystem.fields;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.spout.api.math.Vector3;
import org.spout.nbt.FloatTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.Tag;
import org.spout.nbt.holder.Field;

/**
 * The Vector3 is represented as a ListTag with 3 float values.
 * The item at index 0 is the x value
 * The item at index 1 is the y value
 * The item at index 2 is the z value
 */
public class Vector3Field implements Field<Vector3> {
	public static final Vector3Field INSTANCE = new Vector3Field();

	public Vector3 getValue(Tag<?> tag) throws IllegalArgumentException {
		ListTag<FloatTag> list = getList(tag, FloatTag.class, 3);
		float x = list.getValue().get(0).getValue();
		float y = list.getValue().get(1).getValue();
		float z = list.getValue().get(2).getValue();
		return new Vector3(x, y, z);
	}

	public Tag<?> getValue(String name, Vector3 value) {
		List<FloatTag> list = new ArrayList<FloatTag>(3);
		list.add(new FloatTag("", value.getX()));
		list.add(new FloatTag("", value.getY()));
		list.add(new FloatTag("", value.getZ()));
		return new ListTag<FloatTag>(name, FloatTag.class, list);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Tag<?>> ListTag<T> getList(Tag<?> testTag, Class<T> expectedType, int expectedLength) {
		Validate.isInstanceOf(ListTag.class, testTag);
		ListTag tag = (ListTag) testTag;

		Validate.isTrue(expectedType.isAssignableFrom(tag.getElementType()), "List does not contain" + expectedType + " type tags!");
		if (expectedLength != -1) {
			Validate.isTrue(tag.getValue().size() == expectedLength, "List is not 3 values long");
		}

		return (ListTag<T>) tag;
	}
}
