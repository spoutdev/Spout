/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

import org.spout.api.datatable.value.DatatableBool;
import org.spout.api.datatable.value.DatatableFloat;
import org.spout.api.datatable.value.DatatableInt;
import org.spout.api.datatable.value.DatatableObject;
import org.spout.api.datatable.value.DatatableSerializable;

/**
 * A simpler abstraction for a Datatable Map
 */
public class DataMap implements Map<String, Serializable>{
	final DatatableMap map;
	public DataMap(DatatableMap map) {
		this.map = map;
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
		for (Object o : map.values()) {
			if (o.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Serializable get(Object key) {
		if (key instanceof String) {
			return get((String)key);
		}
		return null;
	}
	
	public Serializable get(String key) {
		return map.get(key).get();
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

	@Override
	public Serializable remove(Object key) {
		if (key instanceof String) {
			return remove((String)key);
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
			current = list.get(index);
			next = list.get(index+1);
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
			next = list.get(index);
			return new Entry(map.getStringKey(keys.get(index)), current);
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
			current = list.get(index);
			next = list.get(index+1);
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
			next = list.get(index);
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
}
