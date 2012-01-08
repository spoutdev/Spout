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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;

/**
 * A snapshotable class for ConcurrentHashSets
 *
 * This allows the class to support getLive functionality.
 *
 * Removals from the Map occur at the next snapshot update.
 */
public class SnapshotableConcurrentHashSet<K> implements Snapshotable {

	private final ConcurrentHashMap<K, Boolean> live;
	private final ConcurrentHashMap<K, Boolean> dirtyMap;
	private final ConcurrentLinkedQueue<K> dirtyQueue;
	private final HashMap<K, Boolean> snapshot;

	public SnapshotableConcurrentHashSet(SnapshotManager manager) {
		snapshot = new HashMap<K, Boolean>();
		live = new ConcurrentHashMap<K, Boolean>();
		dirtyQueue = new ConcurrentLinkedQueue<K>();
		dirtyMap = new ConcurrentHashMap<K, Boolean>();
		manager.add(this);
	}

	/**
	 * Adds an object to the set
	 *
	 * @param value the object's value
	 * @return true if the object was added
	 */
	@DelayedWrite
	@LiveRead
	public boolean add(K value) {
		markDirty(value);
		return live.put(value, Boolean.TRUE) == null;
	}

	/**
	 * Removes an object from the set
	 *
	 * @param value the object's value
	 * @return true if the object was removed
	 */
	@DelayedWrite
	@LiveRead
	public boolean remove(K value) {
		boolean success = live.remove(value) != null;
		if (success) {
			markDirty(value);
		}
		return success;
	}

	/**
	 * Gets the snapshot value
	 *
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public Set<K> get() {
		return Collections.unmodifiableSet(snapshot.keySet());
	}

	/**
	 * Gets the live/unstable value
	 *
	 * @return the stable snapshot value
	 */
	@LiveRead
	public Set<K> getLive() {
		return Collections.unmodifiableSet(live.keySet());
	}

	/**
	 * Copies the next values to the snapshot
	 */
	public void copySnapshot() {
		for (K k : dirtyQueue) {
			Boolean value = live.get(k);
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
