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
package org.spout.api.generic;

import java.util.HashMap;

/**
 * Defines a generic type-to-id matching store.
 * This can be used for Widgets, Layouts, Screens or Entities. 
 * @param <T> the type
 */
public class GenericType<T> {
	
	private Class<? extends T> clazz;
	private int id;
	
	private static HashMap<Class<?>, HashMap<Integer, GenericType<?>>> types = new HashMap<Class<?>, HashMap<Integer,GenericType<?>>>();
	
	public GenericType(Class<? extends T> clazz, int id) {
		getTypes(clazz).put(id, this);
		this.id = id;
		this.clazz = clazz;
	}
	
	public Class<? extends T> getClazz() {
		return clazz;
	}
	
	public int getId() {
		return id;
	}
	
	public static GenericType<?> getType(Class<?> clazz, int id) {
		return getTypes(clazz).get(id);
	}
	
	private static HashMap<Integer, GenericType<?>> getTypes(Class<?> clazz) {
		if (types.containsKey(clazz)) {
			return types.get(clazz);
		}
		HashMap<Integer, GenericType<?>> ret = new HashMap<Integer, GenericType<?>>();
		types.put(clazz, ret);
		return ret;
	}
}
