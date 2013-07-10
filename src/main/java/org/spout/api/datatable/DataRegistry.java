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
package org.spout.api.datatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.StringToUniqueIntegerMap;

public class DataRegistry {
	private static final StringToUniqueIntegerMap idMap = new StringToUniqueIntegerMap(null, new MemoryStore<Integer>(), 0, 256, DataRegistry.class.getName());// AbstractData class name -> ID
	private static final Map<Integer, DataType<?>> datas = new ConcurrentHashMap<Integer, DataType<?>>();
	
	static {
		DataType.init();
	}

	public static <T extends AbstractData> DataType<T> register(DataType<T> o) {
		if (idMap.getValue(o.getDataType().getName()) != null) {
			throw new IllegalStateException("Attempt made to register an AbstractData twice.");
		}
		int id = idMap.register(o.getDataType().getName());
		datas.put(id, o);
		return o;
	}
	
	public static Integer getId(AbstractData o) {
		String name = o.getClass().getName();
		return idMap.getValue(name);
	}
	
	public static DataType<?> getData(int id) {
		DataType<?> get = datas.get(id);
		if (get == null) {
			throw new IllegalStateException("Tried to get the AbstractData of an unknown Id");
		}
		return get;
	}
}
