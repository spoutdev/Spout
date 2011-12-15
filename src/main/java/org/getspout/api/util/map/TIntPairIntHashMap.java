package org.getspout.api.util.map;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove long int hashmap in the backend.
 * @author Afforess
 *
 */
public class TIntPairIntHashMap{
	private TLongIntHashMap map;
	
	public TIntPairIntHashMap() {
		map = new TLongIntHashMap(100);
	}
	
	public TIntPairIntHashMap(int capacity){
		map = new TLongIntHashMap(capacity);
	}
	
	public int put(int key1, int key2, int value) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.put(key, value);
	}
	
	public int get(int key1, int key2) {
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

	public boolean containsValue(int val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongIntIterator iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public int remove(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TIntCollection valueCollection() {
		return map.valueCollection();
	}

	public int[] values() {
		return map.values();
	}
}
