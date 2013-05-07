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
 * Represents an abstract holder, which contains components that may be added
 * and removed from it.
 */
public interface ComponentHolder extends ComponentOwner {
	/**
	 * Adds the component of the specified type to the holder and returns it if it is not present.
	 * <p/>
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
	 * Returns all components of the specified type (or a child implementation).
	 * 
	 * @param type whose components are to be returned from the holder
	 * @return the component list.
	 */
	public <T extends Component> Collection<T> getAll(Class<T> type);
}
