package org.getspout.server.util.thread.snapshotable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * A snapshotable class for HashMaps
 */
public class SnapshotableHashMap<K, V> implements Snapshotable {
	private ConcurrentLinkedQueue<SnapshotUpdate<Map.Entry<K, V>>> pendingUpdates = new ConcurrentLinkedQueue<SnapshotUpdate<Map.Entry<K, V>>>();
	
	private HashMap<K, V> snapshot;
	
	public SnapshotableHashMap(SnapshotManager manager, HashMap<K, V> initial) {
		snapshot = new HashMap<K, V>();
		for (Map.Entry<K, V> e : initial.entrySet()) {
			add(e.getKey(), e.getValue());
		}
		manager.add(this);
	}
	
	/**
	 * Adds an object to the list
	 * 
	 * @param next
	 */
	@DelayedWrite
	public void add(K key, V value) {
		Map.Entry<K, V> entry = new MapEntry<K, V>(key, value);
		pendingUpdates.add(new SnapshotUpdate<Map.Entry<K, V>>(entry, true));
	}
	
	
	/**
	 * Removes a key/value pair from the Map
	 * 
	 * @param key the key of the key/value pair
	 */
	@DelayedWrite
	public void remove(K key) {
		pendingUpdates.add(new SnapshotUpdate<Map.Entry<K, V>>(new MapEntry<K, V>(key, null), false));
	}
	
	/**
	 * Removes a key/value pair from the Map.
	 * 
	 * This method will have no effect if the key does not map to the given value when the removal is attempted
	 * 
	 * @param key the key
	 * @param value the value
	 */
	@DelayedWrite
	public void remove(K key, V value) {
		pendingUpdates.add(new SnapshotUpdate<Map.Entry<K, V>>(new MapEntry<K, V>(key, value, true), false));
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
	 * Copies the next values to the snapshot
	 */
	public void copySnapshot() {
		SnapshotUpdate<Map.Entry<K, V>> update;
		while ((update = pendingUpdates.poll()) != null) {
			processUpdate(update);
		}
	}
	
	private void processUpdate(SnapshotUpdate<Map.Entry<K, V>> update) {
		if (update.isIndexed()) {
			throw new IllegalStateException("Hash maps do not support indexed operation");
		} else {
			Map.Entry<K, V> object = update.getObject();
			if (update.isAdd()) {
				snapshot.put(object.getKey(), object.getValue());
			} else {
				MapEntry<K,V> entry = (MapEntry<K,V>)object;
				K key = entry.getKey();
				if (entry.isExact() && snapshot.get(key) != entry.getValue()) {
					return;
				}
				snapshot.remove(key);
			}
		}
	}
	
	private static class MapEntry<K, V> implements Map.Entry<K, V> {

		private final K key;
		private final V value;
		private final boolean exact;
		
		public MapEntry(K key, V value) {
			this(key, value, false);
		}
		
		public MapEntry(K key, V value, boolean exact) {
			this.key = key;
			this.value = value;
			this.exact = exact;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}
		
		public boolean isExact() {
			return exact;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException("Values are immutable for this class");
		}
		
	}

}
