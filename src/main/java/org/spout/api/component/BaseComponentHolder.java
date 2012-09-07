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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.spout.api.component.components.DatatableComponent;

public class BaseComponentHolder implements ComponentHolder {
	/**
	 * Map of class name, component
	 */
	private final BiMap<Class<? extends Component>, Component> components = HashBiMap.create();

	public BaseComponentHolder() {
		add(DatatableComponent.class);
	}

	@Override
	public <T extends Component> T add(Class<T> type) {
		if (type == null) {
			return null;
		}

		T component = get(type);

		if (component == null) {
			try {
				component = type.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return component;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T remove(Class<? extends Component> type) {
		T component = (T) get(type);

		if (component != null) {
			component.onDetached();
			components.inverse().remove(component);
		}

		return component;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T get(Class<T> type) {
		if (type == null) {
			return null;
		}
		Component component = components.get(type);
		
		if (component == null) {
			component = findComponent(type);
		}
		return (T) component;
	}

	@Override
	public boolean has(Class<? extends Component> type) {
		Component component = components.get(type);
		
		if (component == null) {
			component = findComponent(type);
		}
		return component != null;
	}

	@Override
	public Collection<Component> values() {
		return Collections.unmodifiableList(new ArrayList<Component>(components.values()));
	}

	@Override
	public DatatableComponent getData() {
		return get(DatatableComponent.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends Component> T findComponent(Class<T> type) {
		for (Component component : values()) {
			if (type.isAssignableFrom(component.getClass())) {
				return (T) component;
			}
		}

		return null;
	}
}
