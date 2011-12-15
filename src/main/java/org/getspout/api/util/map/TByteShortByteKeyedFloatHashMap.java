package org.getspout.api.util.map;
import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports (byte, short, byte) keys, using a trove int float hashmap in the backend.
 * @author Afforess
 *
 */
public class TByteShortByteKeyedFloatHashMap extends TByteShortByteKeyedMap{
	private TIntFloatHashMap map;
	
	public TByteShortByteKeyedFloatHashMap() {
		map = new TIntFloatHashMap(100);
	}
	
	public TByteShortByteKeyedFloatHashMap(int capacity){
		map = new TIntFloatHashMap(capacity);
	}
	
	public float put(int key1, int key2, int key3, float value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}
	
	public float get(int key1, int key2, int key3) {
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

	public boolean containsValue(float val) {
		return map.containsValue(val);
	}

	public boolean increment(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.increment(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntFloatIterator iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public float remove(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
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
