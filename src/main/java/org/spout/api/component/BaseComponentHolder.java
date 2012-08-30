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
			components.put(clazz, component);
			component.onAttached();
			return (T) component;
		} else {
			return null;
		}
	}

	@Override
	public boolean removeComponent(Class<? extends Component> aClass) {
		if (!hasComponent(aClass)) {
			return false;
		}
		getComponent(aClass).onDetached();
		components.remove(aClass);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> T getComponent(Class<T> aClass) {
		for(Class<? extends Component> c : components.keySet()){
			if(aClass.isAssignableFrom(c)) return (T) components.get(c);
		}
		return null;
	}

	@Override
	public boolean hasComponent(Class<? extends Component> aClass) {
		for(Class<? extends Component> c : components.keySet()){
			if(aClass.isAssignableFrom(c)) return true;
		}
		return false;
	}

	@Override
	public Collection<Component> getComponents() {
		return new ArrayList<Component>(components.values());
	}
	
	@Override
	public DatatableComponent getDatatable() {
		return datatable;
	}
}
