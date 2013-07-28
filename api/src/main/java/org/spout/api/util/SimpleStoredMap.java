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
package org.spout.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.spout.api.io.store.simple.SimpleStore;

/**
 * Represents a map for mapping Strings to and Object. All conversions are cached in a store.
 */
public class SimpleStoredMap<T> implements StoredMap<T> {
	protected final SimpleStore<T> store;
	protected final String name;

	/**
	 * @param store the store to store ids
	 * @param name The name of this StringMap
	 */
	public SimpleStoredMap(SimpleStore<T> store, String name) {
		this.store = store;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Registers a key/id pair with the map.  If the id is already in use the method will fail.<br> <br> The id must be lower than the min id for the map to prevent clashing with the dynamically
	 * allocated ids
	 *
	 * @param key the key to be added
	 * @param value the desired value to be matched to the key
	 * @return true if the key/id pair was successfully registered
	 * @throws IllegalArgumentException if the id >= minId
	 */
	@Override
	public boolean register(String key, T value) {
		return store.setIfAbsent(key, value);
	}

	/**
	 * Gets the String corresponding to a given int.
	 *
	 * @return the String or null if no match
	 */
	@Override
	public String getString(T value) {
		return store.reverseGet(value);
	}

	/**
	 * Gets the int corresponding to a given String
	 *
	 * @param key The key
	 * @return The int or null if no match
	 */
	@Override
	public T getValue(String key) {
		return store.get(key);
	}

	/**
	 * Saves the map to the persistence system
	 *
	 * @return returns true if the map saves correctly
	 */
	@Override
	public boolean save() {
		return store.save();
	}

	/**
	 * Returns a collection of all keys for all (key, value) pairs within the Store
	 *
	 * @return returns a Collection containing all the keys
	 */
	@Override
	public Collection<String> getKeys() {
		return store.getKeys();
	}

	@Override
	public List<Pair<T, String>> getItems() {
		List<Pair<T, String>> items = new ArrayList<>();
		for (Map.Entry<String, T> entry : store.getEntrySet()) {
			items.add(new ImmutablePair<>(entry.getValue(), entry.getKey()));
		}
		return items;
	}

	@Override
	public void clear() {
		store.clear();
	}
}
