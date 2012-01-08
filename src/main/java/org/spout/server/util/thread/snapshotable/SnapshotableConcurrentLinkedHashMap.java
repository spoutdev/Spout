/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.server.util.thread.snapshotable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * A snapshotable class for LinkedHashMaps.
 *
 * The snapshot is a LinkedHashMap, so iterator ordering is preserved.
 *
 * This allows the class to support getLive functionality.
 *
 * Removals from the Map occur at the next snapshot update.
 */
public class SnapshotableConcurrentLinkedHashMap<K, V> implements Snapshotable {

	private final ConcurrentHashMap<K, V> live;
	private final ConcurrentHashMap<K, Boolean> dirtyMap;
	private final ConcurrentLinkedQueue<K> dirtyQueue;
	private final LinkedHashMap<K, V> snapshot;

	public SnapshotableConcurrentLinkedHashMap(SnapshotManager manager, HashMap<K, V> initial) {
		snapshot = new LinkedHashMap<K, V>();
		live = new ConcurrentHashMap<K, V>();
		dirtyQueue = new ConcurrentLinkedQueue<K>();
		dirtyMap = new ConcurrentHashMap<K, Boolean>();
		if (initial != null) {
			for (Map.Entry<K, V> e : initial.entrySet()) {
				put(e.getKey(), e.getValue());
			}
		}
		manager.add(this);
	}

	/**
	 * Adds an object to the map
	 *
	 * @param key the key
	 * @param value the value
	 * @return the previous value
	 */
	@DelayedWrite
	@LiveRead
	public V put(K key, V value) {
		markDirty(key);
		return live.put(key, value);
	}

	/**
	 * Adds an object to the map, if not already present
	 *
	 * @param key the key
	 * @param value the value
	 * @return the current value, or null on success
	 */
	@DelayedWrite
	@LiveRead
	public V putIfAbsent(K key, V value) {
		V oldValue = live.putIfAbsent(key, value);
		if (oldValue == null) {
			markDirty(key);
		}
		return oldValue;
	}

	/**
	 * Removes a key/value pair from the Map
	 *
	 * @param key the key of the key/value pair
	 * @return the previous value
	 */
	@DelayedWrite
	@LiveRead
	public V remove(K key) {
		V oldValue = live.remove(key);
		if (oldValue != null) {
			markDirty(key);
		}
		return oldValue;
	}

	/**
	 * Removes a key/value pair from the Map.
	 *
	 * This method will have no effect if the key does not map to the given value when the removal is attempted
	 *
	 * @param key the key
	 * @param value the value
	 * @return true if the value was removed
	 */
	@DelayedWrite
	@LiveRead
	public boolean remove(K key, V value) {
		boolean success = live.remove(key, value);
		if (success) {
			markDirty(key);
		}
		return success;
	}

	/**
	 * Gets the snapshot value
	 *
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public Map<K, V> get() {
		return Collections.unmodifiableMap(snapshot);
	}

	/**
	 * Gets the live/unstable value
	 *
	 * @return the stable snapshot value
	 */
	@LiveRead
	public Map<K, V> getLive() {
		return Collections.unmodifiableMap(live);
	}

	/**
	 * Gets a value from a key, checks the Live map and then the snapshot map
	 *
	 * @param key key
	 * @return the live value, or the snapshot value if no live value is present
	 */
	@LiveRead
	@SnapshotRead
	public V getValue(K key) {
		V liveValue = live.get(key);
		if (liveValue == null) {
			return snapshot.get(key);
		} else {
			return liveValue;
		}
	}

	/**
	 * Gets all values that are on the live map and the snapshot map
	 *
	 * @return an Iterable containing the values
	 */
	@LiveRead
	@SnapshotRead
	public Iterable<V> getValues() {
		LinkedHashSet<V> values = new LinkedHashSet<V>(snapshot.size());
		for (V value : live.values()) {
			values.add(value);
		}
		for (V value : snapshot.values()) {
			values.add(value);
		}
		return values;
	}

	/**
	 * Copies the next values to the snapshot
	 */
	public void copySnapshot() {
		for (K k : dirtyQueue) {
			V value = live.get(k);
			if (value == null) {
				snapshot.remove(k);
			} else {
				snapshot.put(k, value);
			}
		}
		dirtyMap.clear();
		dirtyQueue.clear();
	}

	private void markDirty(K key) {
		Boolean old = dirtyMap.putIfAbsent(key, Boolean.TRUE);
		if (old == null) {
			dirtyQueue.add(key);
		}
	}

}
