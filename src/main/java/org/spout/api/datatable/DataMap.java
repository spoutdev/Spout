/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.datatable;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.datatable.value.DatatableBool;
import org.spout.api.datatable.value.DatatableFloat;
import org.spout.api.datatable.value.DatatableInt;
import org.spout.api.datatable.value.DatatableObject;
import org.spout.api.datatable.value.DatatableSerializable;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedMap;

/**
 * A simpler abstraction for a Datatable Map
 */
public class DataMap implements DefaultedMap<String, Serializable>{
	final DatatableMap map;
	public DataMap(DatatableMap map) {
		this.map = map;
	}
	
	/**
	 * Returns the DatatableMap that backs this DataMap. Changes to the backing map will be reflected here as well.
	 * 
	 * @return backing datatable map
	 */
	public DatatableMap getRawMap() {
		return map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return containsKey((String)key);
		}
		return false;
	}

	public boolean containsKey(String key) {
		return map.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (DatatableObject o : map.values()) {
			if (o.get() != null && o.get().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Serializable get(Object key) {
		return get(key, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T get(Object key, T defaultValue) {
		if (!(key instanceof String)) {
			return defaultValue;
		}

		final String keyString = (String) key;
		final T value;
		try {
			value = (T)map.get(keyString).get();
		} catch (ClassCastException e) {
			return defaultValue;
		}

		if (value == null) {
			return defaultValue;
		}

		return value;
	}
	
	@Override
	public <T extends Serializable> T get(DefaultedKey<T> key) {
		T defaultValue = key.getDefaultValue();
		String keyString = key.getKeyString();
		return get(keyString, defaultValue);
	}

	@Override
	public Serializable put(String key, Serializable value) {
		int intKey = map.getIntKey(key);
		Serializable old = map.get(intKey).get();
		if (value instanceof Boolean) {
			map.set(intKey, new DatatableBool(intKey, (Boolean)value));
		} else if (value instanceof Float) {
			map.set(intKey, new DatatableFloat(intKey, (Float)value));
		} else if (value instanceof Integer) {
			map.set(intKey, new DatatableInt(intKey, (Integer)value));
		} else {
			map.set(intKey, new DatatableSerializable(intKey, (Serializable)value));
		}
		return old;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T put(DefaultedKey<T> key, T value) {
		String keyString = key.getKeyString();
		try {
			return (T)put(keyString, value);
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public Serializable remove(Object key) {
		if (key instanceof String) {
			return remove((String)key);
		} else if (key instanceof DefaultedKey) {
			return remove(((DefaultedKey<?>)key).getKeyString());
		}
		return null;
	}
	
	public Serializable remove(String key) {
		return map.remove(key).get();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Serializable> m) {
		 for (Map.Entry<? extends String, ? extends Serializable> e : m.entrySet()) {
			 put(e.getKey(), e.getValue());
		 }
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Serializable> values() {
		return new Values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		return new EntrySet();
	}
	
	private final class EntrySet extends AbstractSet<Map.Entry<String, Serializable>> {
		int size = map.size();
		
		@Override
		public Iterator<java.util.Map.Entry<String, Serializable>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return size;
		}
		
	}
	
	private final class Values extends AbstractCollection<Serializable> {
		public Iterator<Serializable> iterator() {
			return new ValueIterator();
		}

		public int size() {
			return map.size();
		}

		public boolean contains(Object o) {
			return containsValue(o);
		}

		public void clear() {
			this.clear();
		}
	}
	
	private final class EntryIterator implements Iterator<Map.Entry<String, Serializable>> {
		Serializable next, current;
		int index = 0;
		int expectedAmount = map.size();
		ArrayList<Serializable> list = new ArrayList<Serializable>();
		ArrayList<Integer> keys = new ArrayList<Integer>();
		EntryIterator() {
			for (DatatableObject o : map.values()) {
				list.add(o.get());
				keys.add(o.getKey());
			}
			current = null;
			if (expectedAmount == 0) {
				next = null;
			} else {
				next = list.get(index);
			}
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Map.Entry<String, Serializable> next() {
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			index++;
			current = next;
			if (index < expectedAmount) {
				next = list.get(index);
			} else {
				next = null;
			}
			return new Entry(map.getStringKey(keys.get(index-1)), current);
		}

		@Override
		public void remove() {
			if (current == null) {
				throw new IllegalStateException();
			}
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			current = null;
			map.remove(keys.get(index));
		}
	}
	
	private final class Entry implements Map.Entry<String, Serializable> {
		final String key;
		Serializable value;
		Entry(String key, Serializable value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Serializable getValue() {
			return value;
		}

		@Override
		public Serializable setValue(Serializable value) {
			this.value = value;
			return DataMap.this.put(key, value);
		}
		
	}
	
	private final class ValueIterator implements Iterator<Serializable> {
		Serializable next, current;
		int index = 0;
		int expectedAmount = map.size();
		ArrayList<Serializable> list = new ArrayList<Serializable>();
		ArrayList<Integer> keys = new ArrayList<Integer>();
		ValueIterator() {
			for (DatatableObject o : map.values()) {
				list.add(o.get());
				keys.add(o.getKey());
			}
			if (expectedAmount > 1) {
				current = list.get(index);
				next = list.get(index + 1);
			} else if (expectedAmount > 0) {
				current = list.get(index);
				next = null;
			} else {
				current = next = null;
			}
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Serializable next() {
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			index++;
			current = next;
			if (index < expectedAmount) {
				next = list.get(index);
			} else {
				next = null;
			}
			return current;
		}

		@Override
		public void remove() {
			if (current == null) {
				throw new IllegalStateException();
			}
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			current = null;
			map.remove(keys.get(index));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder("DataMap {");
		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			toString.append("(");
			toString.append(e.getKey());
			toString.append(", ");
			toString.append(e.getValue());
			toString.append("), ");
		}
		toString.delete(toString.length() - 3, toString.length());
		toString.append("}");
		return toString.toString();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			builder.append(e.getKey());
			builder.append(e.getValue());
		}
		return builder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DataMap)) {
			return false;
		}

		DataMap other = (DataMap)obj;
		if (isEmpty() && other.isEmpty()) {
			return true;
		}

		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			Serializable value = e.getValue();
			Serializable otherValue = other.get(e.getKey());
			if (value != null) {
				if (!value.equals(otherValue)) {
					return false;
				}
			} else if (otherValue != null) {
				return false;
			}
		}
		return true;
	}
}
