package org.getspout.unchecked.api.util.map;

import java.util.Collection;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports a 3 bytes for keys, using a trove int Object
 * hashmap in the backend.
 *
 * @author Afforess
 *
 */
public class TByteTripleObjectHashMap<K> {
	private TIntObjectHashMap<K> map;

	public TByteTripleObjectHashMap() {
		map = new TIntObjectHashMap<K>(100);
	}

	public TByteTripleObjectHashMap(int capacity) {
		map = new TIntObjectHashMap<K>(capacity);
	}

	public K put(byte key1, byte key2, byte key3, K value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public K get(byte key1, byte key2, byte key3) {
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

	public boolean containsValue(K val) {
		return map.containsValue(val);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntObjectIterator<K> iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public K remove(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<K> valueCollection() {
		return map.valueCollection();
	}

	public K[] values() {
		return map.values();
	}

	private static final int key(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | y & 0x7F;
	}
}
