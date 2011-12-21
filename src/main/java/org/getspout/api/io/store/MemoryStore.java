package org.getspout.api.io.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This implements a SimpleStore that is stored in memory. It is no persisted
 * between restarts.
 */

public class MemoryStore<T> implements SimpleStore<T> {

	private final Map<String, T> map;
	private final Map<T, String> reverseMap;

	public MemoryStore() {
		map = new HashMap<String, T>();
		reverseMap = new HashMap<T, String>();
	}

	public boolean save() {
		return true;
	}

	public boolean load() {
		return true;
	}

	public Collection<String> getKeys() {
		return map.keySet();
	}

	public Set<Entry<String, T>> getEntrySet() {
		return map.entrySet();
	}

	public int getSize() {
		return map.size();
	}

	public boolean clear() {
		map.clear();
		return true;
	}

	public T get(String key) {
		return map.get(key);
	}

	public String reverseGet(T value) {
		return reverseMap.get(value);
	}

	public T get(String key, T def) {
		T value = get(key);
		if (value == null) {
			return def;
		} else {
			return value;
		}
	}

	public T remove(String key) {
		T value = map.remove(key);
		if (value != null) {
			reverseMap.remove(value);
		}
		return value;
	}

	public T set(String key, T value) {
		T oldValue = map.put(key, value);
		if (oldValue != null) {
			reverseMap.remove(oldValue);
		}
		reverseMap.put(value, key);
		return oldValue;
	}

}
