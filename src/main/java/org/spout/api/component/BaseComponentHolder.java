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
package org.spout.api.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.spout.api.component.components.DatatableComponent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class BaseComponentHolder implements ComponentHolder {

	/**
	 * Map of class name, component
	 */
	private final HashMap<Class<? extends Component>, Component> components = new HashMap<Class<? extends Component>, Component>();

	/**
	 * Bidirectional map of the super class of a component, and its actual
	 * class. Used to mark a component as belonging to a specific super class to
	 * map to for has/get. If no super class is picked, it will simply use the
	 * actual class in both sides of the map.
	 */
	private final BiMap<Class<? extends Component>, Class<? extends Component>> typeMap = HashBiMap.create();

	private final DatatableComponent datatable = new DatatableComponent();

	public BaseComponentHolder() {
		put(datatable);
	}

	@Override
	public <T extends Component> T put(T component) {
		return put(null, component);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T put(Class<? extends Component> type, T component) {
		if (component == null) {
			return null;
		}

		Class<? extends Component> typeClass;
		if (type != null) {
			typeClass = type;
			// Check if the component actually belongs to the specified super
			// type.
			if (!typeClass.isAssignableFrom(component.getClass())) {
				return component;
			}
		} else {
			typeClass = component.getClass();
		}

		component.onAttached();
		typeMap.put(component.getClass(), typeClass);
		return (T) components.put(component.getClass(), component);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T remove(Class<? extends Component> type) {
		if (!has(type)) {
			return null;
		}

		Class<? extends Component> key = findKey(type);
		if (key == null) {
			return null;
		}

		typeMap.remove(key);
		Component component = components.remove(key);
		if (component != null) {
			component.onDetached();
		}

		return (T) component;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T get(Class<T> type) {
		if (type == null) {
			return null;
		}

		return (T) components.get(findKey(type));
	}

	@Override
	public <T extends Component> T getOrCreate(Class<T> type) {
		T component = (T) this.get(type);
		if (component != null) {
			return component;
		} else {
			try {
				component = (T) type.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return component;
		}
	}

	@Override
	public boolean has(Class<? extends Component> type) {
		if (type == null) {
			return false;
		}
		return typeMap.containsKey(type) || typeMap.containsValue(type);
	}

	@Override
	public Collection<Component> values() {
		return Collections.unmodifiableList(new ArrayList<Component>(components.values()));
	}

	@Override
	public DatatableComponent getData() {
		return datatable;
	}

	private Class<? extends Component> findKey(Class<? extends Component> type) {
		if (typeMap.containsKey(type)) {
			return type;
		}

		return typeMap.inverse().get(type);
	}
}
