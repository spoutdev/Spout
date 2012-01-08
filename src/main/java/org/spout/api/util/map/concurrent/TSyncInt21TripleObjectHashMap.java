package org.spout.api.util.map.concurrent;

import org.spout.api.util.map.TInt21TripleObjectHashMap;

/**
 * A simplistic map that supports a 3 21 bit integers for keys, using a trove long Object hashmap in the backend. 1 bit is wasted.
 * 
 * This map is backed by a read/write lock synchronised map.
 * 
 * @param <K> the value type
 */
public class TSyncInt21TripleObjectHashMap<K> extends TInt21TripleObjectHashMap<K> {

	public TSyncInt21TripleObjectHashMap() {
		map = new TSyncLongObjectHashMap<K>(100);
	}

	public TSyncInt21TripleObjectHashMap(int capacity) {
		map = new TSyncLongObjectHashMap<K>(capacity);
	}
	
	public TSyncInt21TripleObjectHashMap(TSyncLongObjectMap<K> map) {
		this.map = map;
	}
	
	public boolean remove(int x, int y, int z, K value) {
		long key = key(x, y, z);
		return ((TSyncLongObjectHashMap<K>)map).remove(key, value);
	}
	
	public K putIfAbsent(int x, int y, int z, K value) {
		long key = key(x, y, z);
		return ((TSyncLongObjectHashMap<K>)map).putIfAbsent(key, value);
	}

}
