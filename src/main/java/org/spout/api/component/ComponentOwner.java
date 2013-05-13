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
package org.spout.api.component;

import java.util.Collection;

/**
 * Represents an object which may own components.
 */
public interface ComponentOwner {
	/**
	 * Gets all components held by this component owner.
	 * @return The components held by this owner
	 */
	public Collection<Component> values();

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
	 * Returns all components of the specified type (or a child implementation).
	 * 
	 * @param type whose components are to be returned from the holder
	 * @return the component list.
	 */
	public <T extends Component> Collection<T> getAll(Class<T> type);
}
