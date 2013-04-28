package org.spout.api.util.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Implements a weak value reference HashMap that removes entries 
 * from the map once the value has been garbage collected.<br>
 * <br>
 * The queue is polled whenever an entry is added or removed from the map
 */
public class WeakValueHashMap<K, V> {
	
	private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<V>();
	
	protected final HashMap<K, KeyReference> map = new HashMap<K, KeyReference>();

	public V put(K key, V value) {
		pollQueue();
		Reference<V> ref = map.put(key, new KeyReference(key, value, referenceQueue));
		if (ref != null) {
			return ref.get();
		} else {
			return null;
		}
	}
	
	public V get(K key) {
		pollQueue();
		Reference<V> ref = map.get(key);
		if (ref != null) {
			return ref.get();
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void pollQueue() {
		KeyReference ref;
		while ((ref = (KeyReference) referenceQueue.poll()) != null) {
			K key = ref.getKey();
			Reference<V> currentRef = map.get(key);
			if (currentRef.get() == null) {
				map.remove(key);
			}
		}
	}
	
	protected class KeyReference extends WeakReference<V> {

		private final K key;
		
		public KeyReference(K key, V referent, ReferenceQueue<? super V> q) {
			super(referent, q);
			this.key = key;
		}
		
		public K getKey() {
			return key;
		}
		
	}
	
}
