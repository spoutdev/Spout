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
package org.spout.api.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.spout.api.io.store.simple.SimpleStore;

/**
 * Represents a map for mapping Strings to unique ids.
 *
 * The class supports conversion of ids between maps and allocation of new unique ids for unknown Strings
 *
 * Conversions to and from parent/child maps are cached
 */

public class StringMap {
	//private static StringMap root;

	private final StringMap parent;
	private final SimpleStore<Integer> store;

	private final AtomicIntegerArray thisToParentMap;
	private final AtomicIntegerArray parentToThisMap;

	@SuppressWarnings("unused")
	private final int minId;
	private final int maxId;
	private AtomicInteger nextId;

	/**
	 * @param parent the parent of this map
	 * @param store the store to store ids
	 * @param updateTask the task to call on update
	 * @param minId the lowest valid id
	 * @param maxId the highest valid id + 1
	 */
	public StringMap(StringMap parent, SimpleStore<Integer> store, int minId, int maxId) {
		this.parent = parent;
		this.store = store;
		thisToParentMap = new AtomicIntegerArray(maxId);
		parentToThisMap = new AtomicIntegerArray(maxId);
		for (int i = 0; i < maxId; i++) {
			thisToParentMap.set(i, 0);
			parentToThisMap.set(i, 0);
		}
		this.minId = minId;
		this.maxId = maxId;
		nextId = new AtomicInteger(minId);

	}

	/**
	 * Converts an id local to this map to a foreign id, local to another map.
	 *
	 * @param other the other map
	 * @return returns the foreign id, or 0 on failure
	 */

	public int convertTo(StringMap other, int localId) {
		int foreignId = 0;

		if (other == this) {
			if (store.reverseGet(localId) != null) {
				return localId;
			} else {
				return 0;
			}
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

	public int convertFrom(StringMap other, int foreignId) {
		return other.convertTo(this, foreignId);
	}

	/**
	 * Registers a key with the map and returns the matching id.
	 *
	 * The id corresponding to a key will be consistent if registered more than
	 * once, including over restarts, subject to the persistence of the store.
	 *
	 * @param key the key to be added
	 * @return returns the local id, or 0 on failure
	 */

	public int register(String key) {

		Integer id = store.get(key);

		if (id != null) {
			return id;
		} else {
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
		}
		throw new IllegalStateException("StringMap id space exhausted");
	}

	/**
	 * Saves the map to the persistence system
	 *
	 * @return returns true if the map saves correctly
	 */
	public boolean save() {
		return store.save();
	}
}
