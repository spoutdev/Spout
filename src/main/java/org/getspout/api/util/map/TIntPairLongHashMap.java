package org.getspout.api.util.map;
import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.TLongSet;

/**
 * A simplistic map that supports a pair of integers for keys, using a trove long long hashmap in the backend.
 * @author Afforess
 *
 */
public class TIntPairLongHashMap{
	private TLongLongHashMap map;
	
	public TIntPairLongHashMap() {
		map = new TLongLongHashMap(100);
	}
	
	public TIntPairLongHashMap(int capacity){
		map = new TLongLongHashMap(capacity);
	}
	
	public long put(int key1, int key2, long value) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.put(key, value);
	}
	
	public long get(int key1, int key2) {
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

	public boolean containsValue(long val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TLongLongIterator iterator() {
		return map.iterator();
	}

	public TLongSet keySet() {
		return map.keySet();
	}

	public long[] keys() {
		return map.keys();
	}

	public long remove(int key1, int key2) {
		long key = (((long)key1)<<32) | (((long)key2) & 0xFFFFFFFFL);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public TLongCollection valueCollection() {
		return map.valueCollection();
	}

	public long[] values() {
		return map.values();
	}
}
