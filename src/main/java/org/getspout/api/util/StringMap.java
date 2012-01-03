package org.getspout.api.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.getspout.api.inventory.ItemMapRunnable;
import org.getspout.api.io.store.SimpleStore;

/**
 * Represents a map for mapping Strings to unique ids.
 * 
 * The class supports conversion of ids between maps and allocation of new unique ids for unknown Strings
 * 
 * Conversions to and from parent/child maps are cached
 */

public class StringMap {

	private static StringMap root;

	private final StringMap parent;
	private final SimpleStore<Integer> store;

	private final ItemMapRunnable updateTask;

	private final AtomicIntegerArray thisToParentMap;
	private final AtomicIntegerArray parentToThisMap;

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
	public StringMap(StringMap parent, SimpleStore<Integer> store, ItemMapRunnable updateTask, int minId, int maxId) {
		this.parent = parent;
		this.store = store;
		this.updateTask = updateTask;
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
