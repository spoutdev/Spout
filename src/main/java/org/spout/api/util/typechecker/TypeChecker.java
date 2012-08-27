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
package org.spout.api.util.typechecker;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.spout.nbt.Tag;

public class TypeChecker<T> {
	private final Class<T> clazz;

	protected TypeChecker(Class<T> clazz) {
		this.clazz = clazz;
	}


	/**
	 * Creates a type checker that only checks the root type.
	 * For Maps and Collections, please use {@link #tMap tMap}, {@link #tCollection tCollection}, {@link #tList tList}, {@link #tSet tSet} or {@link #tQueue tQueue}
	 *
	 * @param type The class to check against
	 * @return a type checker for the specified class
	 */
	@SuppressWarnings("unchecked") // Conversion from TypeChecker<SpecificType> to TypeChecker<T> can only be unchecked. Type checking is done with clazz == SpecificType.class beforehand.
	public static <T> TypeChecker<T> tSimple(Class<T> type) {
		// First, check some common cases, and return faster specialized type checkers:
		if (type == Object.class) {
			return (TypeChecker<T>) new TypeChecker<Object>(null) {
				@Override
				public Object check(Object object) {
					return object;
				}
			};
		}

		if (type == String.class) {
			return (TypeChecker<T>) new TypeChecker<String>(null) {
				@Override
				public String check(Object object) {
					return (String) object;
				}
			};
		}

		if (type == Integer.class) {
			return (TypeChecker<T>) new TypeChecker<Integer>(null) {
				@Override
				public Integer check(Object object) {
					return (Integer) object;
				}
			};
		}

		if (type == Long.class) {
			return (TypeChecker<T>) new TypeChecker<Long>(null) {
				@Override
				public Long check(Object object) {
					return (Long) object;
				}
			};
		}

		if (type == Float.class) {
			return (TypeChecker<T>) new TypeChecker<Float>(null) {
				@Override
				public Float check(Object object) {
					return (Float) object;
				}
			};
		}

		if (type == Double.class) {
			return (TypeChecker<T>) new TypeChecker<Double>(null) {
				@Override
				public Double check(Object object) {
					return (Double) object;
				}
			};
		}

		// No specialized type checker found? just return a regular one
		return new TypeChecker<T>(type);
	}


	/**
	 * Creates a recursing type checker for a {@link Collection}.
	 *
	 * @param elementType The class to check the elements against
	 * @return a typechecker for a Collection containing elements of the specified type
	 */
	public static <T> TypeChecker<Collection<? extends T>> tCollection(Class<? extends T> elementType) {
		return tCollection(tSimple(elementType));
	}

	/**
	 * Creates a recursing type checker for a {@link Collection}.
	 *
	 * @param elementChecker The typechecker to check the elements with
	 * @return a typechecker for a Collection containing elements passing the specified type checker
	 */
	public static <T> TypeChecker<Collection<? extends T>> tCollection(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, Collection<? extends T>>(Collection.class, elementChecker);
	}


	/**
	 * Creates a recursing type checker for a {@link List}.
	 *
	 * @param elementType The class to check the elements against
	 * @return a typechecker for a List containing elements of the specified type
	 */
	public static <T> TypeChecker<List<? extends T>> tList(Class<? extends T> elementType) {
		return tList(tSimple(elementType));
	}

	/**
	 * Creates a recursing type checker for a {@link List}.
	 *
	 * @param elementChecker The typechecker to check the elements with
	 * @return a typechecker for a List containing elements passing the specified type checker
	 */
	public static <T> TypeChecker<List<? extends T>> tList(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, List<? extends T>>(List.class, elementChecker);
	}


	/**
	 * Creates a recursing type checker for a {@link Set}.
	 *
	 * @param elementType The class to check the elements against
	 * @return a typechecker for a Set containing elements of the specified type
	 */
	public static <T> TypeChecker<Set<? extends T>> tSet(Class<? extends T> elementType) {
		return tSet(tSimple(elementType));
	}

	/**
	 * Creates a recursing type checker for a {@link Set}.
	 *
	 * @param elementChecker The typechecker to check the elements with
	 * @return a typechecker for a Set containing elements passing the specified type checker
	 */
	public static <T> TypeChecker<Set<? extends T>> tSet(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, Set<? extends T>>(Set.class, elementChecker);
	}


	/**
	 * Creates a recursing type checker for a {@link Queue}.
	 *
	 * @param elementType The class to check the elements against
	 * @return a typechecker for a Queue containing elements of the specified type
	 */
	public static <T> TypeChecker<Queue<? extends T>> tQueue(Class<? extends T> elementType) {
		return tQueue(tSimple(elementType));
	}

	/**
	 * Creates a recursing type checker for a {@link Queue}.
	 *
	 * @param elementChecker The typechecker to check the elements with
	 * @return a typechecker for a Queue containing elements passing the specified type checker
	 */
	public static <T> TypeChecker<Queue<? extends T>> tQueue(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, Queue<? extends T>>(Queue.class, elementChecker);
	}


	/**
	 * Creates a recursing type checker for a {@link Map}.
	 *
	 * @param keyType The class to check the keys against
	 * @param valueType The class to check the values against
	 * @return a typechecker for a Map containing keys and values of the specified types
	 */
	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(Class<? extends K> keyType, Class<? extends V> valueType) {
		return tMap(tSimple(keyType), tSimple(valueType));
	}

	/**
	 * Creates a recursing type checker for a {@link Map}.
	 *
	 * @param keyType The class to check the keys against
	 * @param valueType The typechecker to check the values with
	 * @return a typechecker for a Map containing keys of the specified type and values passing the specified type checker
	 */
	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(Class<? extends K> keyType, TypeChecker<? extends V> valueChecker) {
		return tMap(tSimple(keyType), valueChecker);
	}

	/**
	 * Creates a recursing type checker for a {@link Map}.
	 *
	 * @param keyChecker The typechecker to check the keys with
	 * @param valueType The typechecker to check the values with
	 * @return a typechecker for a Map containing keys passing the specified type checker and values of the specified type
	 */
	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(TypeChecker<? extends K> keyChecker, Class<? extends V> valueType) {
		return tMap(keyChecker, tSimple(valueType));
	}

	/**
	 * Creates a recursing type checker for a {@link Map}.
	 *
	 * @param keyChecker The typechecker to check the keys with
	 * @param valueChecker The typechecker to check the values with
	 * @return a typechecker for a Map containing keys and values passing the specified type checkers
	 */
	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(TypeChecker<? extends K> keyChecker, TypeChecker<? extends V> valueChecker) {
		return new MapTypeChecker<K, V, Map<? extends K, ? extends V>>(Map.class, keyChecker, valueChecker);
	}


	/**
	 * Checks and casts an object to the specified type.
	 *
	 * @param object The object to be checked
	 * @return The same object, cast to the specified class
	 * @throws ClassCastException if casting fails
	 */
	public T check(Object object) throws ClassCastException {
		return clazz.cast(object);
	}

	/**
	 * Checks and casts an object to the specified type.
	 * If casting fails, a default value is returned.
	 *
	 * @param object The object to be checked
	 * @param defaultValue The default value to be returned if casting fails
	 * @return The same object, cast to the specified class, or the default value, if casting fails
	 */
	public final T check(Object object, T defaultValue) {
		try {
			return check(object);
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	/**
	 * Checks and casts an object contained in a tag to the specified type.
	 *
	 * @param tag The Tag containing the object to be checked
	 * @return The object contained in the tag, cast to the specified class
	 * @throws ClassCastException if casting fails
	 */
	public final T checkTag(Tag tag) {
		return check(tag.getValue());
	}

	/**
	 * Checks and casts an object to the specified type.
	 * If casting fails, a default value is returned.
	 *
	 * @param tag The Tag containing the object to be checked
	 * @param defaultValue The default value to be returned if casting fails
	 * @return The object contained in the tag, cast to the specified class, or the default value, if casting fails
	 */
	public final T checkTag(Tag tag, T defaultValue) {
		if (tag == null) {
			return defaultValue;
		}

		try {
			return check(tag.getValue());
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}
}
