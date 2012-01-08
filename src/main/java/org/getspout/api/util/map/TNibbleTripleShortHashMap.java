package org.getspout.api.util.map;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.map.hash.TShortShortHashMap;
import gnu.trove.set.TShortSet;

/**
 * A simplistic map that supports a 3 nibbles (4 bits) for keys, using a trove short short hashmap in the backend.
 */
public class TNibbleTripleShortHashMap {
	protected TShortShortMap map;

	public TNibbleTripleShortHashMap() {
		map = new TShortShortHashMap(100);
	}

	public TNibbleTripleShortHashMap(int capacity) {
		map = new TShortShortHashMap(capacity);
	}
	
	public TNibbleTripleShortHashMap(TShortShortMap map) {
		this.map = map;
	}

	public short put(byte key1, byte key2, byte key3, short value) {
		short key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public short get(byte key1, byte key2, byte key3) {
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

	public boolean containsValue(short val) {
		return map.containsValue(val);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TShortShortIterator iterator() {
		return map.iterator();
	}

	public TShortSet keySet() {
		return map.keySet();
	}

	public short[] keys() {
		return map.keys();
	}

	public short remove(byte key1, byte key2, byte key3) {
		short key = key(key1, key2, key3);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TShortCollection valueCollection() {
		return map.valueCollection();
	}

	public short[] values() {
		return map.values();
	}
	
	protected static final short key(byte x, byte y, byte z) {
		return (short) (((x & 0xF) << 8) | ((y & 0xF) << 4) | (z & 0xF));
	}
}
