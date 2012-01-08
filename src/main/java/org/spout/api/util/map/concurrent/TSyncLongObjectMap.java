package org.spout.api.util.map.concurrent;

import gnu.trove.map.TLongObjectMap;

/**
 * This is a synchronized version of the Trove TLongObjectMap
 *
 * @param <V> the value type
 */
public interface TSyncLongObjectMap<V> extends TLongObjectMap<V> {
	/**
	 * Removes a key/value pair from the map, but only if the key is mapped to a given value
	 * 
	 * @param key the key
	 * @param value the expected value
	 * @return true if on success
	 */
	public boolean remove(long key, V value);
}
