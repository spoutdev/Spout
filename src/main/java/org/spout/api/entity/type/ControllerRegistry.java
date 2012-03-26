/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.entity.type;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.entity.Controller;

/**
 * Handles lookup of entity controller types.
 */
public class ControllerRegistry {
	private final static Map<String, ControllerType> nameLookup = new HashMap<String, ControllerType>();
	private static final Map<Class<? extends Controller>, ControllerType> classLookup = new HashMap<Class<? extends Controller>, ControllerType>();

	public static void register(ControllerType type) {
		synchronized (classLookup) {
			if (!classLookup.containsKey(type.getControllerClass())) {
				classLookup.put(type.getControllerClass(), type);
				nameLookup.put(type.getName().toLowerCase(), type);
			}
		}
	}

	public static ControllerType get(String name) {
		return nameLookup.get(name.toLowerCase());
	}

	public static ControllerType get(Class<? extends Controller> type) {
		return classLookup.get(type);
	}

	public static Collection<ControllerType> getAll() {
		return Collections.unmodifiableCollection(classLookup.values());
	}
}
