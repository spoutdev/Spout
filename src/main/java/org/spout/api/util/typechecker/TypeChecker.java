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


	public static <T> TypeChecker<Collection<? extends T>> tCollection(Class<? extends T> elementType) {
		return tCollection(tSimple(elementType));
	}

	public static <T> TypeChecker<Collection<? extends T>> tCollection(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, Collection<? extends T>>(Collection.class, elementChecker);
	}


	public static <T> TypeChecker<List<? extends T>> tList(Class<? extends T> elementType) {
		return tList(tSimple(elementType));
	}

	public static <T> TypeChecker<List<? extends T>> tList(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, List<? extends T>>(List.class, elementChecker);
	}


	public static <T> TypeChecker<Set<? extends T>> tSet(Class<? extends T> elementType) {
		return tSet(tSimple(elementType));
	}

	public static <T> TypeChecker<Set<? extends T>> tSet(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, Set<? extends T>>(Set.class, elementChecker);
	}


	public static <T> TypeChecker<Queue<? extends T>> tQueue(Class<? extends T> elementType) {
		return tQueue(tSimple(elementType));
	}

	public static <T> TypeChecker<Queue<? extends T>> tQueue(TypeChecker<? extends T> elementChecker) {
		return new CollectionTypeChecker<T, Queue<? extends T>>(Queue.class, elementChecker);
	}


	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(Class<? extends K> keyType, Class<? extends V> valueType) {
		return tMap(tSimple(keyType), tSimple(valueType));
	}

	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(Class<? extends K> keyType, TypeChecker<? extends V> valueChecker) {
		return tMap(tSimple(keyType), valueChecker);
	}

	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(TypeChecker<? extends K> keyChecker, Class<? extends V> valueType) {
		return tMap(keyChecker, tSimple(valueType));
	}

	public static <K, V> TypeChecker<Map<? extends K, ? extends V>> tMap(TypeChecker<? extends K> keyChecker, TypeChecker<? extends V> valueChecker) {
		return new MapTypeChecker<K, V, Map<? extends K, ? extends V>>(Map.class, keyChecker, valueChecker);
	}


	public T check(Object object) {
		return clazz.cast(object);
	}

	public final T check(Object object, T defaultValue) {
		try {
			return check(object);
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	public final T checkTag(Tag tag) {
		return check(tag.getValue());
	}

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
