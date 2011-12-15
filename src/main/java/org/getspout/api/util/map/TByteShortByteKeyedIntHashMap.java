package org.getspout.api.util.map;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports (byte, short, byte) keys, using a trove int int hashmap in the backend.
 * @author Afforess
 *
 */
public class TByteShortByteKeyedIntHashMap extends TByteShortByteKeyedMap{
	private TIntIntHashMap map;
	
	public TByteShortByteKeyedIntHashMap() {
		map = new TIntIntHashMap(100);
	}
	
	public TByteShortByteKeyedIntHashMap(int capacity){
		map = new TIntIntHashMap(capacity);
	}
	
	public int put(int key1, int key2, int key3, int value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}
	
	public int get(int key1, int key2, int key3) {
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

	public boolean increment(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntIntIterator iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public int remove(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
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
