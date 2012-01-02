package org.getspout.api.util.map;

import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;

/**
 * A simplistic unmodifiable map that supports a 3 21 bit integers for keys, using a trove long Object hashmap in the backend. 1 bit is wasted.
 */
public class TUnmodifiableInt21TripleObjectHashMap<K> extends TInt21TripleObjectHashMap<K>{
	public TUnmodifiableInt21TripleObjectHashMap(TInt21TripleObjectHashMap<K> map) {
		this.map = new TUnmodifiableLongObjectMap<K>(map.getInternalMap());
	}
	
	/**
	 * Replaces the internal immutable map with this new one
	 * @param newMap to replace
	 */
	public void update(TInt21TripleObjectHashMap<K> newMap) {
		this.map = new TUnmodifiableLongObjectMap<K>(newMap.getInternalMap());
	}
}
