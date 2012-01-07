package org.getspout.api.util.map;

import java.util.Collection;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a 3 21 bit integers for keys, using a trove long Object hashmap in the backend. 1 bit is wasted.
 */
public class TInt21TripleObjectHashMap<K> {
	protected TLongObjectMap<K> map;

	public TInt21TripleObjectHashMap() {
		map = new TLongObjectHashMap<K>(100);
	}

	public TInt21TripleObjectHashMap(int capacity) {
		map = new TLongObjectHashMap<K>(capacity);
	}
	
	public TInt21TripleObjectHashMap(TLongObjectMap<K> map) {
		this.map = map;
	}

	public K put(int key1, int key2, int key3, K value) {
		long key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public K get(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
		return map.get(key);
	}

	public boolean containsKey(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
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

	public TLongObjectIterator<K> iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public K remove(int key1, int key2, int key3) {
		long key = key(key1, key2, key3);
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
	
	protected TLongObjectMap<K> getInternalMap() {
		return map;
	}

	protected static final long key(int x, int y, int z) {
		return (((long)x) & 0x1FFFFF) << 42 | (((long)z) & 0x1FFFFF) << 21 | ((long)y) & 0x1FFFFF;
	}
}
