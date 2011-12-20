package org.getspout.api.util.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports a 3 bytes for keys, using a trove int double
 * hashmap in the backend.
 *
 * @author Afforess
 *
 */
public class TByteTripleDoubleHashMap {
	private TIntDoubleHashMap map;

	public TByteTripleDoubleHashMap() {
		map = new TIntDoubleHashMap(100);
	}

	public TByteTripleDoubleHashMap(int capacity) {
		map = new TIntDoubleHashMap(capacity);
	}

	public double put(byte key1, byte key2, byte key3, double value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public double get(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.get(key);
	}

	public boolean containsKey(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(double val) {
		return map.containsValue(val);
	}

	public boolean increment(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntDoubleIterator iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public double remove(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TDoubleCollection valueCollection() {
		return map.valueCollection();
	}

	public double[] values() {
		return map.values();
	}

	private static final int key(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | y & 0x7F;
	}
}
