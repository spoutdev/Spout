package org.getspout.api.util.map;
import gnu.trove.TLongCollection;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports a 3 bytes for keys, using a trove int long hashmap in the backend.
 * @author Afforess
 *
 */
public class TByteTripleLongHashMap{
	private TIntLongHashMap map;
	
	public TByteTripleLongHashMap() {
		map = new TIntLongHashMap(100);
	}
	
	public TByteTripleLongHashMap(int capacity){
		map = new TIntLongHashMap(capacity);
	}
	
	public long put(byte key1, byte key2, byte key3, long value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}
	
	public long get(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.get(key);
	}
	
	public boolean containsKey(byte key1, byte key2, byte key3) {
		int key = key(key1, key2, key3);
		return map.containsKey(key);
	}
	
	public void clear() {
		map.clear();
	}

	public boolean containsValue(long val) {
		return map.containsValue(val);
	}

	public boolean increment(byte key1, byte key2, byte key3) {
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

	public long remove(byte key1, byte key2, byte key3) {
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
	
	private static final int key(int x, int y, int z) {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
	}
}
