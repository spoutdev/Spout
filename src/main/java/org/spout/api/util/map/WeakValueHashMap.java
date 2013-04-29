/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.util.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Set;

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
	
	public Set<K> keySet() {
		return map.keySet();
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
