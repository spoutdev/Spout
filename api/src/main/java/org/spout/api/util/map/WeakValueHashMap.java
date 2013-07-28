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
package org.spout.api.util.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Set;

/**
 * Implements a weak value reference HashMap that removes entries from the map once the value has been garbage collected.<br> <br> The queue is polled whenever an entry is added or removed from the
 * map
 */
public class WeakValueHashMap<K, V> {
	private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<>();
	protected final HashMap<K, KeyReference> map = new HashMap<>();

	/**
	 * Puts the given key, value pair into the map.  Any expired keys are automatically flushed.
	 *
	 * @return the old value, or null if the key didn't map to a value
	 */
	public V put(K key, V value) {
		flushKeys();
		Reference<V> ref = map.put(key, new KeyReference(key, value, referenceQueue));
		if (ref != null) {
			return ref.get();
		} else {
			return null;
		}
	}

	/**
	 * Gets the value associated with the given key.<br>
	 *
	 * This method does not cause keys to be flushed.  It is intended for use when iterating over the keyset.  Removing a key while iterating would cause a ConcurrentModificationException.  It is
	 * recommended that a manual call is made to flushKeys once the iteration is completed.
	 *
	 * @return the value associated with the value, or null
	 */
	public V safeGet(K key) {
		return get(key, false);
	}

	/**
	 * Gets the value associated with the given key.Any expired keys are automatically flushed.
	 *
	 * @return the value associated with the value, or null
	 */
	public V get(K key) {
		return get(key, true);
	}

	private V get(K key, boolean flushKeys) {
		if (flushKeys) {
			flushKeys();
		}
		Reference<V> ref = map.get(key);
		if (ref != null) {
			return ref.get();
		} else {
			return null;
		}
	}

	/**
	 * Gets the set of all keys in the map.  Some keys in the set may be expired
	 */
	public Set<K> keySet() {
		return map.keySet();
	}

	/**
	 * Flushes all expired keys.  Keys associated with values that have been garbage collected are considered expired.<br> <br> This method is automatically called by the get and put methods.
	 */
	@SuppressWarnings ("unchecked")
	public void flushKeys() {
		KeyReference ref;
		while ((ref = (KeyReference) referenceQueue.poll()) != null) {
			K key = ref.getKey();
			Reference<V> currentRef = map.get(key);
			if (currentRef.get() == null) {
				map.remove(key);
			}
		}
	}

	protected class KeyReference extends WeakReference<V> {
		private final K key;

		public KeyReference(K key, V referent, ReferenceQueue<? super V> q) {
			super(referent, q);
			this.key = key;
		}

		public K getKey() {
			return key;
		}
	}
}
