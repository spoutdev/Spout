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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.io.store.simple.SimpleStore;

/**
 * Represents a map for mapping Strings to unique ids.
 *
 * The class supports conversion of ids between maps and allocation of new unique ids for unknown Strings
 *
 * Conversions to and from parent/child maps are cached
 */
public class StringToUniqueIntegerMap extends SimpleStoredMap<Integer> {
	private final StringToUniqueIntegerMap parent;
	private final AtomicIntegerArray thisToParentMap;
	private final AtomicIntegerArray parentToThisMap;
	private final int minId;
	private final int maxId;
	private AtomicInteger nextId;

	public StringToUniqueIntegerMap(String name) {
		this(null, new MemoryStore<Integer>(), 0, Integer.MAX_VALUE, name);
	}

	public StringToUniqueIntegerMap(String name, int maxId) {
		this(null, new MemoryStore<Integer>(), 0, maxId, name);
	}

	/**
	 * @param parent the parent of this map
	 * @param store the store to store ids
	 * @param minId the lowest valid id for dynamic allocation (ids below this are assumed to be reserved)
	 * @param maxId the highest valid id + 1
	 * @param name The name of this StringToUniqueIntegerMap
	 */
	public StringToUniqueIntegerMap(StringToUniqueIntegerMap parent, SimpleStore<Integer> store, int minId, int maxId, String name) {
		super(store, name);
		this.parent = parent;
		if (this.parent != null) {
			thisToParentMap = new AtomicIntegerArray(maxId);
			parentToThisMap = new AtomicIntegerArray(maxId);
			for (int i = 0; i < maxId; i++) {
				thisToParentMap.set(i, 0);
				parentToThisMap.set(i, 0);
			}
		} else {
			thisToParentMap = null;
			parentToThisMap = null;
		}
		this.minId = minId;
		this.maxId = maxId;
		nextId = new AtomicInteger(minId);
	}

	/**
	 * Converts an id local to this map to the id local to the parent map
	 *
	 * @param localId to convert
	 * @return the foreign id, or 0 on failure
	 */
	public int convertToParent(int localId) {
		if (parent == null) {
			throw new IllegalStateException("Parent map is null!");
		}
		return convertTo(parent, localId);
	}

	/**
	 * Converts an id local to this map to a foreign id, local to another map.
	 *
	 * @param other the other map
	 * @param localId to convert
	 * @return returns the foreign id, or 0 on failure
	 */
	public int convertTo(StringToUniqueIntegerMap other, int localId) {
		if (other == null) {
			throw new IllegalStateException("Other map is null");
		}
		int foreignId = 0;

		if (other == this) {
			if (store.reverseGet(localId) == null) {
				return 0;
			}

			return localId;
		} else if (other == parent) {
			foreignId = thisToParentMap.get(localId);
		} else if (other.parent == this) {
			foreignId = other.parentToThisMap.get(localId);
		}

		// Cache hit
		if (foreignId != 0) {
			return foreignId;
		}

		String localKey = store.reverseGet(localId);

		// There is no entry in the local map to perform the translation
		if (localKey == null) {
			return 0;
		}

		Integer integerForeignId = other.store.get(localKey);

		// The other map doesn't have an entry for this key
		if (integerForeignId == null) {
			integerForeignId = other.register(localKey);
		}

		// Add the key/value pair to the cache, if is no problem with the foreign key
		if (integerForeignId != 0) {
			if (other == parent) {
				thisToParentMap.set(localId, integerForeignId);
				parentToThisMap.set(integerForeignId, localId);
			} else if (other.parent == this) {
				other.thisToParentMap.set(integerForeignId, localId);
				other.parentToThisMap.set(localId, integerForeignId);
			}
		}

		return integerForeignId;
	}

	/**
	 * Converts a foreign id, local to a foreign map to an id local to this map.
	 *
	 * @param other the other map
	 * @return returns the local id, or 0 on failure
	 */
	public int convertFrom(StringToUniqueIntegerMap other, int foreignId) {
		return other.convertTo(this, foreignId);
	}

	/**
	 * Registers a key with the map and returns the matching id.
	 *
	 * The id corresponding to a key will be consistent if registered more than once, including over restarts, subject to the persistence of the store.
	 *
	 * @param key the key to be added
	 * @return returns the local id, or 0 on failure
	 */
	public int register(String key) {
		Integer id = store.get(key);
		if (id != null) {
			return id;
		}

		int localId = nextId.getAndIncrement();

		while (localId < maxId) {
			if (store.setIfAbsent(key, localId)) {
				return localId;
			}

			Integer storeId = store.get(key);
			if (storeId != null) {
				return storeId;
			}

			localId = nextId.getAndIncrement();
		}

		throw new IllegalStateException("StringMap id space exhausted");
	}

	/**
	 * Registers a key/id pair with the map.  If the id is already in use the method will fail.<br> <br> The id must be lower than the min id for the map to prevent clashing with the dynamically
	 * allocated ids
	 *
	 * @param key the key to be added
	 * @param id the desired id to be matched to the key
	 * @return true if the key/id pair was successfully registered
	 * @throws IllegalArgumentException if the id >= minId
	 */
	public boolean register(String key, int id) {
		if (id >= this.minId) {
			throw new IllegalArgumentException("Hardcoded ids must be below the minimum id value");
		}

		return store.setIfAbsent(key, id);
	}

	/**
	 * Gets the String corresponding to a given int.
	 *
	 * @return the String or null if no match
	 */
	@Override
	public String getString(Integer value) {
		return store.reverseGet(value);
	}

	/**
	 * Gets the int corresponding to a given String
	 *
	 * @param key The key
	 * @return The int or null if no match
	 */
	@Override
	public Integer getValue(String key) {
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
	public List<Pair<Integer, String>> getItems() {
		List<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
		for (Map.Entry<String, Integer> entry : store.getEntrySet()) {
			items.add(new ImmutablePair<Integer, String>(entry.getValue(), entry.getKey()));
		}
		return items;
	}

	@Override
	public void clear() {
		while (this.nextId.getAndSet(minId) != minId) {
			if (this.parent != null) {
				for (int i = 0; i < maxId; i++) {
					thisToParentMap.set(i, 0);
					parentToThisMap.set(i, 0);
				}
			}
			store.clear();
		}
	}
}
