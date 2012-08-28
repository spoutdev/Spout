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

import org.spout.api.entity.components.DatatableComponent;

/**
 * Represents the accessing portion of an entity that controls retrieving/removing components.
 */
public interface ComponentHolder<T> {
	/**
	 * Adds a new component to the holder.  If the holder already contains a component of that type, then a new component is not
	 * constructed, and the one already attached is returned
	 * @param component the component to be added
	 * @return The component created, or the one already attached
	 */
	public T addComponent(Component component);

	/**
	 * Removes a component from the list
	 * @param component Type of component to remove
	 * @return True if a component is removed, false if not.  False is also returned if the component doesn't exist.
	 */
	public boolean removeComponent(Class<? extends Component> component);

	/**
	 * Returns the first component that is assignable from the given class, or null if it doesn't exist
	 * @param component the type of component to get
	 * @return The component instance, or null if it doesn't exist
	 */
	public T getComponent(Class<? extends Component> component);

	/**
	 * Returns true if a component exists that is assignable from the given class.
	 * @param component Type of component to check if the entity has
	 * @return True if a component exists that is assignable from the given class, false if not
	 */
	public boolean hasComponent(Class<? extends Component> component);
	
	public DatatableComponent getDatatable();
}
