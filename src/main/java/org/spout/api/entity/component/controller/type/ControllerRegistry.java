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
package org.spout.api.entity.component.controller.type;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.entity.component.Controller;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.StringMap;

/**
 * Handles lookup of entity controller types.
 */
public class ControllerRegistry {
	private static final StringMap ID_LOOKUP = new StringMap(null, new MemoryStore<Integer>(), 0, Integer.MAX_VALUE, ControllerType.class.getName());
	private static final Map<String, ControllerType> NAME_LOOKUP = new HashMap<String, ControllerType>();
	private static final Map<Class<? extends Controller>, ControllerType> CLASS_LOOKUP = new HashMap<Class<? extends Controller>, ControllerType>();

	public static void register(ControllerType type) {
		synchronized (CLASS_LOOKUP) {
			if (!CLASS_LOOKUP.containsKey(type.getControllerClass())) {
				CLASS_LOOKUP.put(type.getControllerClass(), type);
				NAME_LOOKUP.put(type.getName().toLowerCase(), type);
				type.setId(ID_LOOKUP.register(type.getName().toLowerCase()));
			}
		}
	}

	public static ControllerType get(int id) {
		return NAME_LOOKUP.get(ID_LOOKUP.getString(id));
	}

	public static ControllerType get(String name) {
		return NAME_LOOKUP.get(name.toLowerCase());
	}

	public static ControllerType get(Class<? extends Controller> type) {
		return CLASS_LOOKUP.get(type);
	}

	public static Collection<ControllerType> getAll() {
		return Collections.unmodifiableCollection(CLASS_LOOKUP.values());
	}
}
