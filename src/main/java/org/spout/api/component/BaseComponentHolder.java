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
	private final HashMap<String, Component> components = new HashMap<String, Component>();

	private final BiMap<String, String> typeMap = HashBiMap.create();
	private final DatatableComponent datatable = new DatatableComponent();

	public BaseComponentHolder() {
		addComponent(datatable);
	}

	@Override
	public <T extends Component> T addComponent(T component) {
		if (component == null) {
			return null;
		}

		return addComponent(component.getClass(), component);
	}

	@Override
	public <T extends Component> T addComponent(Class<? extends Component> type, T component) {
		if (component == null || hasComponent(type)) {
			return component;
		}

		String typeName;
		if (type == null) {
			typeName = component.getClass().getName();
		} else {
			typeName = type.getName();
		}
	
		components.put(convert(component.getClass()), component);
		typeMap.put(convert(component.getClass()), typeName);
		return component;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T removeComponent(Class<? extends Component> type) {
		if (!hasComponent(type)) {
			return null;
		}

		String key = findKey(type);
		if (key == null) {
			return null;
		}

		typeMap.remove(key);
		return (T) components.remove(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T getComponent(Class<T> type) {
		if (type == null) {
			return null;
		}

		return (T) components.get(findKey(type));
	}

	@Override
	public <T extends Component> T getOrCreate(Class<? extends Component> typeClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasComponent(Class<? extends Component> type) {
		if (type == null) {
			return false;
		}
		return typeMap.containsKey(convert(type)) || typeMap.containsValue(convert(type));
	}

	@Override
	public Collection<Component> getComponents() {
		return Collections.unmodifiableList(new ArrayList<Component>(components.values()));
	}

	@Override
	public DatatableComponent getDatatable() {
		return datatable;
	}

	private String findKey(Class<? extends Component> type) {
		String typeString = convert(type);
		
		if (typeMap.containsKey(typeString)) {
			return typeString;
		}

		return typeMap.inverse().get(typeString);
	}

	private String convert(Class<? extends Component> clazz) {
		return clazz.getCanonicalName();
	}
}
