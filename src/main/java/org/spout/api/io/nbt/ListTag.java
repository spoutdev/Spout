/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.io.nbt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code TAG_List} tag.
 * @author Graham Edgecombe
 */
public final class ListTag<T extends Tag> extends Tag {
	/**
	 * The type of entries within this list.
	 */
	private final Class<T> type;

	/**
	 * The value.
	 */
	private final List<T> value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param type The type of item in the list.
	 * @param value The value.
	 */
	public ListTag(String name, Class<T> type, List<T> value) {
		super(name);
		this.type = type;
		this.value = Collections.unmodifiableList(value);
	}

	/**
	 * Gets the type of item in this list.
	 * @return The type of item in this list.
	 */
	public Class<T> getType() {
		return type;
	}

	@Override
	public List<T> getValue() {
		return value;
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}

		StringBuilder bldr = new StringBuilder();
		bldr.append("TAG_List").append(append).append(": ").append(value.size()).append(" entries of type ").append(NBTUtils.getTypeName(type)).append("\r\n{\r\n");
		for (Tag t : value) {
			bldr.append("   ").append(t.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
		}
		bldr.append("}");
		return bldr.toString();
	}

	@SuppressWarnings("unchecked")
	public ListTag<T> clone() {
		List<T> newList = new ArrayList<T>();
		
		for (T v : value) {
			newList.add((T)v.clone());
		}
		
		return new ListTag<T>(getName(), type, newList);
	}
}
