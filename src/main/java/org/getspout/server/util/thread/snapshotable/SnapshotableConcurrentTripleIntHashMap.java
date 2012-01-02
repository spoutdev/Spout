package org.getspout.server.util.thread.snapshotable;

import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.getspout.api.util.map.TInt21TripleObjectHashMap;
import org.getspout.api.util.map.TUnmodifiableInt21TripleObjectHashMap;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.server.util.TripleInt;

/**
 * A snapshotable class for triple int HashMaps based on Trove long maps.
 * 
 * This allows the class to support getLive functionality.
 * 
 * Removals from the Map occur at the next snapshot update.
 */
public class SnapshotableConcurrentTripleIntHashMap<V> implements Snapshotable {
	
	private final TInt21TripleObjectHashMap<V> live;
	private final ConcurrentHashMap<TripleInt, Boolean> dirtyMap;
	private final ConcurrentLinkedQueue<TripleInt> dirtyQueue;
	private final TInt21TripleObjectHashMap<V> snapshot;
	private final TUnmodifiableInt21TripleObjectHashMap<V> unmutableSnapshot;
	private final TUnmodifiableInt21TripleObjectHashMap<V> unmutableLive;
	
	public SnapshotableConcurrentTripleIntHashMap(SnapshotManager manager) {
		live = new TInt21TripleObjectHashMap<V>();
		snapshot = new TInt21TripleObjectHashMap<V>();
		unmutableSnapshot = new TUnmodifiableInt21TripleObjectHashMap<V>(snapshot);
		unmutableLive = new TUnmodifiableInt21TripleObjectHashMap<V>(live);
		dirtyQueue = new ConcurrentLinkedQueue<TripleInt>();
		dirtyMap = new ConcurrentHashMap<TripleInt, Boolean>();
		manager.add(this);
	}
	
	/**
	 * Adds an object to the map
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param value the value
	 * @return the previous value
	 */
	@DelayedWrite
	@LiveRead
	public V put(int x, int y, int z, V value) {
		markDirty(new TripleInt(x, y, z));
		synchronized(live) {
			return live.put(x, y, z, value);
		}
	}
	
	/**
	 * Adds an object to the map, if not already present
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param value the value
	 * @return the current value, or null on success
	 */
	@DelayedWrite
	@LiveRead
	public V putIfAbsent(int x, int y, int z, V value) {
		synchronized(live) {
			V oldValue = live.get(x, y, z);
			if (oldValue == null) {
				live.put(x, y, z, value);
				TripleInt key = new TripleInt(x, y, z);
				markDirty(key);
			}
			return oldValue;
		}
	}
	
	/**
	 * Removes a key/value pair from the Map
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return the previous value
	 */
	@DelayedWrite
	@LiveRead
	public V remove(int x, int y, int z) {
		V oldValue; 
		synchronized(live) {
			oldValue = live.remove(x, y, z);
			if (oldValue != null) {
				TripleInt key = new TripleInt(x, y, z);
				markDirty(key);
			}
		}
		return oldValue;
	}
	
	/**
	 * Removes a key/value pair from the Map.
	 * 
	 * This method will have no effect if the key does not map to the given value when the removal is attempted
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param value the value
	 * @return true if the value was removed
	 */
	@DelayedWrite
	@LiveRead
	public boolean remove(int x, int y, int z, V value) {
		synchronized(live) {
			V current = live.get(x, y, z);
			if (current.equals(value)) {
				live.remove(x, y, z);
				TripleInt key = new TripleInt(x, y, z);
				markDirty(key);
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Gets the snapshot value
	 * 
	 * @return the stable snapshot value
	 */
	@SnapshotRead
	public TInt21TripleObjectHashMap<V> get() {
		return unmutableSnapshot;
	}
	
	/**
	 * Gets the live/unstable value
	 * 
	 * DO NOT MODIFY THIS MAP
	 * 
	 * @return the stable snapshot value
	 */
	@LiveRead
	public TInt21TripleObjectHashMap<V> getLive() {
		return unmutableLive;
	}
	
	/**
	 * Gets a value from a key, checks the Live map and then the snapshot map
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return the live value, or the snapshot value if no live value is present
	 */
	@LiveRead
	@SnapshotRead
	public V getValue(int x, int y, int z) {
		synchronized(live) {
			V liveValue = live.get(x, y, z);
			if (liveValue == null) {
				return snapshot.get(x, y, z);
			} else {
				return liveValue;
			}
		}
	}
	
	/**
	 * Gets a value from a key
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return the live value, or the snapshot value if no live value is present
	 */
	@SnapshotRead
	public V get(int x, int y, int z) {
		return snapshot.get(x, y, z);
	}
	
	/**
	 * Gets a value from a key
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return the live value, or the snapshot value if no live value is present
	 */
	@LiveRead
	public V getLive(int x, int y, int z) {
		synchronized(live) {
			return live.get(x, y, z);
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
		synchronized(live) {
			for (V value : live.values()) {
				values.add(value);
			}
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
		for (TripleInt k : dirtyQueue) {
			V value = live.get(k.x, k.y, k.z);
			if (value == null) {
				snapshot.remove(k.x, k.y, k.z);
			} else {
				snapshot.put(k.x, k.y, k.z, value);
			}
		}
		dirtyMap.clear();
		dirtyQueue.clear();
	}
	
	private void markDirty(TripleInt key) {
		Boolean old = dirtyMap.putIfAbsent(key, Boolean.TRUE);
		if (old == null) {
			dirtyQueue.add(key);
		}
	}
	
}
