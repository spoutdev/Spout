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
package org.spout.engine.util.thread.snapshotable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.spout.api.scheduler.TickStage;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.api.util.thread.annotation.SnapshotRead;

/**
 * A snapshotable class for HashMaps
 */
public class SnapshotableHashMap<K, V> implements Snapshotable {
	private final Map<K, V> snapshot = new LinkedHashMap<>();
	private final Map<K, V> unmodifySnapshot = Collections.unmodifiableMap(snapshot);
	private final ConcurrentMap<K, V> live = new ConcurrentHashMap<>();
	private final Map<K, V> unmodifyLive = Collections.unmodifiableMap(live);
	private final ConcurrentLinkedQueue<K> dirtyKeys = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<V> dirtyValues = new ConcurrentLinkedQueue<>();

	public SnapshotableHashMap(SnapshotManager manager) {
		manager.add(this);
	}

	/**
	 * Adds a key/value pair to the map
	 *
	 * @param key the key
	 * @param value the value
	 * @return the old value
	 */
	@DelayedWrite
	@LiveRead
	public V put(K key, V value) {
		V oldValue = live.put(key, value);
		dirtyKeys.add(key);
		dirtyValues.add(value);
		return oldValue;
	}

	/**
	 * Adds a key/value pair to the map, if no value exists for the key
	 *
	 * @param key the key
	 * @param value the value
	 * @return the old value
	 */
	@DelayedWrite
	@LiveRead
	public V putIfAbsent(K key, V value) {
		V oldValue = live.putIfAbsent(key, value);
		if (oldValue == null) {
			dirtyKeys.add(key);
			dirtyValues.add(value);
		}
		return oldValue;
	}

	/**
	 * Removes a key/value pair from the list
	 *
	 * @param key the key
	 * @return the old value
	 */
	@DelayedWrite
	@LiveRead
	public V remove(K key) {
		V oldValue = live.remove(key);
		if (oldValue != null) {
			dirtyKeys.add(key);
			dirtyValues.add(oldValue);
		}
		return oldValue;
	}

	/**
	 * Removes a key/value pair from the list
	 *
	 * @param key the key
	 * @param value the value
	 * @return true if the key/value pair was removed
	 */
	@DelayedWrite
	@LiveRead
	public boolean remove(K key, V value) {
		boolean success = live.remove(key, value);
		if (success) {
			dirtyKeys.add(key);
			dirtyValues.add(value);
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
		return unmodifySnapshot;
	}

	/**
	 * Gets the live value
	 *
	 * @return the live set
	 */
	public Map<K, V> getLive() {
		return unmodifyLive;
	}

	/**
	 * Creates a list of keys that have been changed since the last snapshot copy.<br> <br> This method may only be called during the pre-snapshot stage and the list only remains valid during that
	 * stage.
	 *
	 * @return the list of elements that have been updated
	 */
	public List<K> getDirtyKeyList() {
		TickStage.checkStage(TickStage.PRESNAPSHOT);
		return Collections.unmodifiableList(new ArrayList<>(dirtyKeys));
	}

	/**
	 * Creates a list of values that have been changed since the last snapshot copy.<br> <br> This method may only be called during the pre-snapshot stage and the list only remains valid during that
	 * stage.
	 *
	 * @return the list of elements that have been updated
	 */
	public List<V> getDirtyValueList() {
		TickStage.checkStage(TickStage.PRESNAPSHOT);
		return Collections.unmodifiableList(new ArrayList<>(dirtyValues));
	}

	/**
	 * Copies the next values to the snapshot
	 */
	@Override
	public void copySnapshot() {
		for (K key : dirtyKeys) {
			V value = live.get(key);
			if (value == null) {
				snapshot.remove(key);
			} else {
				snapshot.put(key, value);
			}
		}
		dirtyKeys.clear();
		dirtyValues.clear();
	}
}
