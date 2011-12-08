package org.getspout.commons.util.map;
import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.hash.TLongFloatHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove long float hashmap in the backend.
 * @author Afforess
 *
 */
public class TIntPairFloatHashMap{
	private TLongFloatHashMap map;
	
	public TIntPairFloatHashMap() {
		map = new TLongFloatHashMap(100);
	}
	
	public TIntPairFloatHashMap(int capacity){
		map = new TLongFloatHashMap(capacity);
	}
	
	public float put(int key1, int key2, float value) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.put(key, value);
	}
	
	public float get(int key1, int key2) {
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

	public boolean containsValue(float val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongFloatIterator iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public float remove(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TFloatCollection valueCollection() {
		return map.valueCollection();
	}

	public float[] values() {
		return map.values();
	}
}
