package org.getspout.commons.util.map;
import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove long double hashmap in the backend.
 * @author Afforess
 *
 */
public class TIntPairDoubleHashMap{
	private TLongDoubleHashMap map;
	
	public TIntPairDoubleHashMap() {
		map = new TLongDoubleHashMap(100);
	}
	
	public TIntPairDoubleHashMap(int capacity){
		map = new TLongDoubleHashMap(capacity);
	}
	
	public double put(int key1, int key2, double value) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.put(key, value);
	}
	
	public double get(int key1, int key2) {
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

	public boolean containsValue(double val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongDoubleIterator iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public double remove(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
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
}
