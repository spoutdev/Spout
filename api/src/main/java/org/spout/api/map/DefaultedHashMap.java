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
package org.spout.api.map;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link DefaultedMap} using a HashMap
 */
public class DefaultedHashMap<V> extends HashMap<String, V> implements DefaultedMap<V> {
	private static final long serialVersionUID = 1L;

	public DefaultedHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public DefaultedHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public DefaultedHashMap() {
	}

	public DefaultedHashMap(Map<? extends String, ? extends V> m) {
		super(m);
	}

	@Override
	@SuppressWarnings ("unchecked")
	public <T extends V> T get(Object key, T defaultValue) {
		V value = get(key);
		if (value == null) {
			value = defaultValue;
		}

		return (T) value;
	}

	@Override
	public <T extends V> T get(DefaultedKey<T> key) {
		return get(key.getKeyString(), key.getDefaultValue());
	}

	@Override
	@SuppressWarnings ("unchecked")
	public <T extends V> T put(DefaultedKey<T> key, T value) {
		try {
			return (T) put(key.getKeyString(), value);
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	@SuppressWarnings ("unchecked")
	public <T extends V> T putIfAbsent(DefaultedKey<T> key, T value) {
		try {
			return (T) putIfAbsent(key.getKeyString(), value);
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public V putIfAbsent(String key, V value) {
		if (!containsKey(key)) {
			return put(key, value);
		} else {
			return get(key);
		}
	}
}
