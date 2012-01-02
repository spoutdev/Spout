package org.getspout.server.util.thread.snapshotable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

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
		boolean success = dirtyMap.putIfAbsent(key, Boolean.TRUE);
		if (success) {
			dirtyQueue.add(key);
		}
	}
	
}
