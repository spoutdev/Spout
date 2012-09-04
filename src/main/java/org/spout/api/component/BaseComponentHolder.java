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

public class BaseComponentHolder implements ComponentHolder {
	private final HashMap<Class<? extends Component>, Component> components = new HashMap<Class<? extends Component>, Component>();
	private final DatatableComponent datatable = new DatatableComponent();
	
	public BaseComponentHolder() {
		addComponent(datatable);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T addComponent(T component) {
		if (component.attachTo(this)) {
			Class<? extends Component> clazz = component.getClass();
			if (hasComponent(clazz)) {
				return (T) getComponent(clazz);
			}
			component.onAttached();
			components.put(clazz, component);
			return component;
		} else {
			return null;
		}
	}

	@Override
	public boolean removeComponent(Class<? extends Component> aClass) {
		if (!hasComponent(aClass)) {
			return false;
		}
		Component component = getComponent(aClass);
		if (component.isDetachable()) {
			getComponent(aClass).onDetached();
			components.remove(aClass);
			return true;			
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T getComponent(Class<T> aClass) {
		return (T) components.get(aClass);
	}

	@Override
	public <T extends Component> T getOrCreate(Class<T> component) {
		T componentToGet = getComponent(component);
		if (componentToGet == null) {
			try {
				componentToGet = (T) componentToGet.getClass().newInstance();
				addComponent(componentToGet);
			} catch (InstantiationException ie) {
				ie.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			}
		}
		return componentToGet;
	}

	@Override
	public boolean hasComponent(Class<? extends Component> aClass) {
		return components.containsKey(aClass);
	}

	@Override
	public Collection<Component> getComponents() {
		return Collections.unmodifiableList(new ArrayList<Component>(components.values()));
	}
	
	@Override
	public DatatableComponent getDatatable() {
		return datatable;
	}
}
