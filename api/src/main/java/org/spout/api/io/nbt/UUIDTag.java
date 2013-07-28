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
import java.util.UUID;

import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.Tag;
import org.spout.nbt.util.NBTMapper;

public class UUIDTag extends ListTag<LongTag> {
	public UUIDTag(String name, UUID u) {
		super(name, LongTag.class, UUIDToList(u));
	}

	private static List<LongTag> UUIDToList(UUID u) {
		List<LongTag> list = new ArrayList<>(2);
		list.add(new LongTag("", u.getMostSignificantBits()));
		list.add(new LongTag("", u.getLeastSignificantBits()));
		return list;
	}

	@SuppressWarnings ("unchecked")
	public static UUID getValue(Tag<?> tag) {
		try {
			return getValue((ListTag<LongTag>) tag);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static UUID getValue(ListTag<LongTag> list) {
		if (list == null) {
			return null;
		}
		return getValue(list.getValue());
	}

	public static UUID getValue(List<LongTag> list) {
		if (list == null || list.size() != 2) {
			return null;
		}
		Long m = NBTMapper.toTagValue(list.get(0), Long.class, null);
		Long l = NBTMapper.toTagValue(list.get(1), Long.class, null);

		if (m == null || l == null) {
			return null;
		}
		return new UUID(m, l);
	}
}
