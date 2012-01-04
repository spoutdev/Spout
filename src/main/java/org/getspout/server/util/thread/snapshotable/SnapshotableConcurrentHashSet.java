package org.getspout.server.util.thread.snapshotable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

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
