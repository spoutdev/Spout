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
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.Tag;
import org.spout.nbt.holder.Field;

/**
 * Stores a UUID in NBT
 * The UUID is represented as a ListTag with 2 long values.
 * The item at index 0 is the most significant bits
 * The item at index 1 is the least significant bits
 */
public class UUIDField implements Field<UUID> {
	public static final UUIDField INSTANCE = new UUIDField();
	public UUID getValue(Tag<?> tag) throws IllegalArgumentException {
		ListTag<LongTag> list = getList(tag, LongTag.class, 2);
		long msb = list.getValue().get(0).getValue();
		long lsb = list.getValue().get(1).getValue();
		return new UUID(msb, lsb);
	}

	public Tag<?> getValue(String name, UUID value) {
		List<LongTag> list = new ArrayList<LongTag>();
		list.add(new LongTag("", value.getMostSignificantBits()));
		list.add(new LongTag("", value.getLeastSignificantBits()));
		return new ListTag<LongTag>(name, LongTag.class, list);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Tag<?>> ListTag<T> getList(Tag<?> testTag, Class<T> expectedType, int expectedLength) {
		Validate.isInstanceOf(ListTag.class, testTag);
		ListTag tag = (ListTag) testTag;

		Validate.isTrue(expectedType.isAssignableFrom(tag.getElementType()), "List does not contain " + expectedType + " type tags!");
		if (expectedLength != -1) {
			Validate.isTrue(tag.getValue().size() == expectedLength, "List is not " + expectedLength + " values long");
		}

		return (ListTag<T>) tag;
	}
}
