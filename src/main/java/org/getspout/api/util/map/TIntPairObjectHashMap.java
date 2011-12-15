package org.getspout.api.util.map;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;

import java.util.Collection;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove long object hashmap in the backend.
 * @author Afforess
 *
 */
public class TIntPairObjectHashMap<K>{
	private TLongObjectHashMap<K> map;
	
	public TIntPairObjectHashMap() {
		map = new TLongObjectHashMap<K>(100);
	}
	
	public TIntPairObjectHashMap(int capacity){
		map = new TLongObjectHashMap<K>(capacity);
	}
	
	public K put(int key1, int key2, K value) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.put(key, value);
	}
	
	public K get(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.get(key);
	}
	
	public boolean containsKey(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
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

	public K remove(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
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
}
