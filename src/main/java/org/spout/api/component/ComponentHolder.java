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

import java.util.Collection;

import org.spout.api.component.components.DatatableComponent;

/**
 * Represents an abstract holder, which contains components that may be added
 * and removed from it.
 */
public interface ComponentHolder {

	/**
	 * Adds the component of the specified type to the holder and returns it if it is not present.
	 * 
	 * Otherwise, it returns the component of the specified type if there was one present.
	 * @param type whose component is to be added to the holder
	 * @return the new component that was added, or the existing one if it had one
	 */
	public <T extends Component> T add(Class<T> type);

	/**
	 * Removes the component of the specified type from the holder if it is
	 * present.
	 * @param type whose component is to be removed from the holder
	 * @return the removed component, or null if there was not one
	 */
	public <T extends Component> T detach(Class<? extends Component> type);

	/**
	 * Returns the component of the specified type (or a child implementation) from the holder if it is
	 * present.
	 * @param type whose component is to be returned from the holder
	 * @return the component, or null if one was not found
	 */
	public <T extends Component> T get(Class<T> type);

	/**
	 * Returns the component of the specified type (not a child implementation) from the holder if it is
	 * present.
	 * @param type whose component is to be returned from the holder
	 * @return the component, or null if one was not found.
	 */
	public <T extends Component> T getExact(Class<T> type);

	/**
	 * Returns true if the holder contains a component of the specified type.
	 * Will always return true if the specified type is used, and will return
	 * true for a super type if the super type was mapped to the component using
	 * the special put.
	 * @param type whose component is to be checked for in the holder
	 * @return true if the component exists in the holder
	 */
	public boolean has(Class<? extends Component> type);

	/**
	 * Returns true if the holder contains a component of the specified type.
	 * Will always return true if the specified type is used but will return false
	 * for any child implementations of the type (think an exact match to the type).
	 * @param type whose component is to be checked for in the holder
	 * @return true if the component exists in the holder
	 */
	public boolean hasExact(Class<? extends Component> type);

	/**
	 * Gets all components held by this component holder.
	 * @return The components held by this holder
	 */
	public Collection<Component> values();

	/**
	 * Gets the datatable component held by this component holder.
	 * @return Gets the datatable held by this holder
	 */
	public DatatableComponent getData();
}
