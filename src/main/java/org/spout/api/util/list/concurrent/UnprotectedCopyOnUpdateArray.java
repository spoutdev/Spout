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
package org.spout.api.util.list.concurrent;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An unprotected array that uses copy on update for array updating.  
 * The internal array is returned directly from getArray().  
 * For proper operation, this array must not be mutated. 
 * 
 * @param <T>
 */
public class UnprotectedCopyOnUpdateArray<T> implements Collection<T> {
	
	private final AtomicReference<T[]> ref;
	private final Class<?> clazz;
	private final boolean asSet;
	
	/**
	 * Creates an empty UnprotectedCopyOnUpdateArray
	 * 
	 * @param the component class
	 */
	public UnprotectedCopyOnUpdateArray(Class<?> clazz) {
		this.clazz = clazz;
		this.ref = new AtomicReference<T[]>(newArray(0));
		if (clazz == null) {
			throw new NullPointerException("Provided class is null");
		}
		asSet = false;
	}
	
	/**
	 * Creates an empty UnprotectedCopyOnUpdateArray
	 * 
	 * @param the component class
	 * @param asSet duplicate elements are rejected, if true
	 */
	public UnprotectedCopyOnUpdateArray(Class<?> clazz, boolean asSet) {
		this.clazz = clazz;
		this.ref = new AtomicReference<T[]>(newArray(0));
		if (clazz == null) {
			throw new NullPointerException("Provided class is null");
		}
		this.asSet = asSet;
	}
	
	/**
	 * Creates an UnprotectedCopyOnUpdateArray using the given array as the initial array.<br>
	 * <br>
	 * This array is used directly as the internal array, so should not be mutated.
	 * 
	 * @param the component class
	 * @param initial
	 */
	public UnprotectedCopyOnUpdateArray(Class<?> clazz, T[] initial) {
		this(clazz);
		ref.set(initial);
	}
	
	/**
	 * Creates an UnprotectedCopyOnUpdateArray using the given array as the initial array.<br>
	 * <br>
	 * This array is used directly as the internal array, so should not be mutated.
	 * 
	 * @param the component class
	 * @param initial
	 * @param asSet duplicate elements are rejected, if true
	 */
	public UnprotectedCopyOnUpdateArray(Class<?> clazz, T[] initial, boolean asSet) {
		this(clazz, asSet);
		ref.set(initial);
	}
	
	@Override
	public boolean add(T value) {
		boolean success = false;
		while (!success) {
			T[] oldArray = ref.get();
			T[] newArray = newArray(oldArray.length + 1);
			int i;
			for (i = 0; i < oldArray.length; i++) {
				if (asSet) {
					if (isEquals(oldArray[i], value)) {
						return false;
					}
				}
				newArray[i] = oldArray[i];
			}
			newArray[i] = value;
			success = ref.compareAndSet(oldArray, newArray);
		}
		return true;
	}
	
	@Override
	public int size() {
		return ref.get().length;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		T[] array = ref.get();
		for (int i = 0; i < array.length; i++) {
			if (isEquals(o, array[i])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator(ref.get());
	}

	@Override
	public T[] toArray() {
		return ref.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T2> T2[] toArray(T2[] a) {
		return (T2[]) ref.get();
	}

	@Override
	public boolean remove(Object o) {
		boolean success = false;
		boolean removed = false;
		while (!success) {
			T[] oldArray = ref.get();
			T[] newArray = newArray(oldArray.length - 1);
			int i;
			int j = 0;
			removed = false;
			for (i = 0; i < newArray.length; i++) {
				T old = oldArray[j++];
				if (isEquals(o, old)) {
					removed = true;
					break;
				}
				newArray[i] = old;
			}
			if (removed) {
				for (; i < newArray.length; i++) {
					newArray[i] = oldArray[j++]; 
				}
			} else {
				if (!isEquals(o, oldArray[j])) {
					return false;
				}
			}
			success = ref.compareAndSet(oldArray, newArray);
		}
		return true;	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsAll(Collection<?> c) {
		@SuppressWarnings("rawtypes")
		LinkedHashSet s = new LinkedHashSet();
		for (Object o : c) {
			s.add(o);
		}
		T[] array = ref.get();
		for (int i = 0; i < array.length; i++) {
			if (s.remove(array[i]) && s.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		ref.set(newArray(0));
	}

	@SuppressWarnings("unchecked")
	private T[] newArray(int length) {
		return (T[]) Array.newInstance(clazz, length);
	}
	
	private static boolean isEquals(Object o1, Object o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}
	
	private class ArrayIterator implements Iterator<T> {

		private final T[] array;
		int i;
		
		public ArrayIterator(T[] array) {
			this.array = array;
			i = 0;
		}
		
		@Override
		public boolean hasNext() {
			return i < array.length;
		}

		@Override
		public T next() {
			return array[i++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

}
