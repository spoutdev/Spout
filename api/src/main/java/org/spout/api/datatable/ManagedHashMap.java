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
package org.spout.api.datatable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.datatable.delta.DeltaMap;

public class ManagedHashMap extends SerializableHashMap implements ManagedMap {
	private static final long serialVersionUID = 1L;
	private final DeltaMap delta;

	public ManagedHashMap() {
		this.delta = new DeltaMap(DeltaMap.DeltaType.SET);
	}

	public ManagedHashMap(ManagedHashMap parent, String key) {
		this.delta = new DeltaMap(parent.delta, DeltaMap.DeltaType.SET, key);
	}

	@Override
	public Serializable putIfAbsent(String key, Serializable value) {
		delta.putIfAbsent(key, value);
		return super.putIfAbsent(key, value);
	}

	@Override
	public Serializable put(String key, Serializable value) {
		delta.putIfAbsent(key, value);
		return super.put(key, value);
	}

	@Override
	public Serializable remove(String key) {
		delta.put(key, null);
		return map.remove(key);
	}

	@Override
	public void clear() {
		delta.clear();
		map.clear();
	}

	@Override
	public void deserialize(byte[] data, boolean wipe) throws IOException {
		delta.deserialize(data, wipe);
		super.deserialize(data, wipe);
	}

	/**
	 * This will return if the map has been map has been modified since the last call to setDirty(false).
	 *
	 * @return the dirty state of the map
	 */
	@Override
	public DeltaMap getDeltaMap() {
		return delta;
	}

	@Override
	public void resetDelta() {
		delta.reset();
	}

	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder("ManagedHashMap {");
		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			toString.append("(");
			toString.append(e.getKey());
			toString.append(", ");
			toString.append(e.getValue());
			toString.append("), ");
		}
		toString.delete(toString.length() - 2, toString.length());
		toString.append("}");
		return toString.toString();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			builder.append(e.getKey());
			builder.append(e.getValue());
		}
		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ManagedHashMap)) {
			return false;
		}

		ManagedHashMap other = (ManagedHashMap) obj;
		if (isEmpty() && other.isEmpty()) {
			return true;
		}

		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			Serializable value = e.getValue();
			Serializable otherValue = other.get(e.getKey());
			if (value != null) {
				if (!value.equals(otherValue)) {
					return false;
				}
			} else if (otherValue != null) {
				return false;
			}
		}
		return true;
	}
}
