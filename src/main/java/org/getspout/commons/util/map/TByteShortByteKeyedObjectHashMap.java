package org.getspout.commons.util.map;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;

import java.util.Collection;

/**
 * A simplistic map that supports a 3 bytes for keys, using a trove int int hashmap in the backend.
 * @author Afforess
 *
 */
public class TByteShortByteKeyedObjectHashMap<K>{
	private TIntObjectHashMap<K> map;
	
	public TByteShortByteKeyedObjectHashMap() {
		map = new TIntObjectHashMap<K>(100);
	}
	
	public TByteShortByteKeyedObjectHashMap(int capacity){
		map = new TIntObjectHashMap<K>(capacity);
	}
	
	public K put(int key1, int key2, int key3, K value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}
	
	public K get(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.get(key);
	}
	
	public boolean containsKey(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.containsKey(key);
	}
	
	public void clear() {
		map.clear();
	}

	public boolean containsValue(int val) {
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

	public K remove(int key1, int key2, int key3) {
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
		return ((x & 0xF) << 11) | ((z & 0xF) << 7) | (y & 0x7F);
	}
}
