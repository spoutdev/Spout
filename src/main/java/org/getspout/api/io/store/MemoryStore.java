package org.getspout.api.io.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This implements a SimpleStore that is stored in memory. It is not persisted
 * between restarts.
 */

public class MemoryStore<T> implements SimpleStore<T> {

	private final Map<String, T> map;
	private final Map<T, String> reverseMap;

	public MemoryStore() {
		map = new HashMap<String, T>();
		reverseMap = new HashMap<T, String>();
	}

	public synchronized boolean save() {
		return true;
	}

	public synchronized boolean load() {
		return true;
	}

	public synchronized Collection<String> getKeys() {
		return map.keySet();
	}

	public synchronized Set<Entry<String, T>> getEntrySet() {
		return map.entrySet();
	}

	public synchronized int getSize() {
		return map.size();
	}

	public synchronized boolean clear() {
		map.clear();
		return true;
	}

	public synchronized T get(String key) {
		return map.get(key);
	}

	public synchronized String reverseGet(T value) {
		return reverseMap.get(value);
	}

	public synchronized T get(String key, T def) {
		T value = get(key);
		if (value == null) {
			return def;
		} else {
			return value;
		}
	}

	public synchronized T remove(String key) {
		T value = map.remove(key);
		if (value != null) {
			reverseMap.remove(value);
		}
		return value;
	}

	public synchronized T set(String key, T value) {
		T oldValue = map.put(key, value);
		if (oldValue != null) {
			reverseMap.remove(oldValue);
		}
		reverseMap.put(value, key);
		return oldValue;
	}
	
	public synchronized boolean setIfAbsent(String key, T value) {
		if (map.get(key) != null || reverseMap.get(value) != null) {
			return false;
		} else {
			set(key, value);
			return true;
		}
	}

}
