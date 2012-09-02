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
package org.spout.api.chat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.util.SpoutToStringStyle;

/**
 * A placeholder for a value in ChatArguments
 */
public class Placeholder {
	private final String name;

	public Placeholder(String name) {
		this.name = name.toUpperCase();
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + 7;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {return false;}
		if (getClass() != obj.getClass()) {return false;}
		return ((Placeholder) obj).name.equals(this.name);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("name", name)
				.toString();
	}
}

/**
 * The value of a {@link Placeholder}, for internal use only
 */
class Value {
	public ChatArguments value;
	public int index;

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Value other = (Value) o;

		if (index != other.index) return false;
		if (value != null ? !value.equals(other.value) : other.value != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result = value != null ? value.hashCode() : 0;
		result = 31 * result + index;
		return result;
	}
}
