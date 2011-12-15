package org.getspout.api.util.map;
import gnu.trove.TLongCollection;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports (byte, short, byte) keys, using a trove int long hashmap in the backend.
 * @author Afforess
 *
 */
public class TByteShortByteKeyedLongHashMap extends TByteShortByteKeyedMap{
	private TIntLongHashMap map;
	
	public TByteShortByteKeyedLongHashMap() {
		map = new TIntLongHashMap(100);
	}
	
	public TByteShortByteKeyedLongHashMap(int capacity){
		map = new TIntLongHashMap(capacity);
	}
	
	public long put(int key1, int key2, int key3, long value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}
	
	public long get(int key1, int key2, int key3) {
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

	public boolean containsValue(long val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntLongIterator iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public long remove(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
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
