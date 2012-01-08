package org.getspout.api.util.map;

import java.util.Collection;

import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.set.TShortSet;

/**
 * A simplistic map that supports a 3 nibbles (4 bits) for keys, using a trove short object hashmap in the backend.
 */
public class TNibbleTripleObjectHashMap<K> {
	protected TShortObjectMap<K> map;

	public TNibbleTripleObjectHashMap() {
		map = new TShortObjectHashMap<K>(100);
	}

	public TNibbleTripleObjectHashMap(int capacity) {
		map = new TShortObjectHashMap<K>(capacity);
	}
	
	public TNibbleTripleObjectHashMap(TShortObjectMap<K> map) {
		this.map = map;
	}

	public K put(byte key1, byte key2, byte key3, K value) {
		short key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public K get(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
		return map.get(key);
	}

	public boolean containsKey(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
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

	public TShortObjectIterator<K> iterator() {
		return map.iterator();
	}

	public TShortSet keySet() {
		return map.keySet();
	}

	public short[] keys() {
		return map.keys();
	}

	public K remove(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
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
	
	protected static final short key(byte x, byte y, byte z) {
		return (short) (((x & 0xF) << 8) | ((y & 0xF) << 4) | (z & 0xF));
	}
}
