/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.io.nbt;

import java.util.Collections;
import java.util.Map;

/**
 * The {@code TAG_Compound} tag.
 * @author Graham Edgecombe
 */
public final class CompoundTag extends Tag {
	/**
	 * The value.
	 */
	private final Map<String, Tag> value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param value The value.
	 */
	public CompoundTag(String name, Map<String, Tag> value) {
		super(name);
		this.value = Collections.unmodifiableMap(value);
	}

	@Override
	public Map<String, Tag> getValue() {
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
		bldr.append("TAG_Compound").append(append).append(": ").append(value.size()).append(" entries\r\n{\r\n");
		for (Map.Entry<String, Tag> entry : value.entrySet()) {
			bldr.append("   ").append(entry.getValue().toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
		}
		bldr.append("}");
		return bldr.toString();
	}

	public CompoundTag clone() {
		Map<String, Tag> newMap = Tag.cloneMap(value);
		return new CompoundTag(getName(), newMap);
	}
}
