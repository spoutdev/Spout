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
package org.spout.api.util;

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public interface StoredMap<T> {

	public void clear();

	public List<Pair<T, String>> getItems();

	/**
	 * Returns a collection of all keys for all (key, value) pairs within the
	 * Store
	 *
	 * @return returns a Collection containing all the keys
	 */
	public Collection<String> getKeys();

	public String getName();

	/**
	 * Gets the String corresponding to a given int.
	 *
	 * @param value
	 * @return the String or null if no match
	 */
	public String getString(T value);

	/**
	 * Gets the int corresponding to a given String
	 * @param key The key
	 * @return The int or null if no match
	 */
	public T getValue(String key);

	/**
	 * Registers a key/id pair with the map.  If the id is already in use the method will fail.<br>
	 * <br>
	 * The id must be lower than the min id for the map to prevent clashing with the dynamically allocated ids
	 *
	 * @param key the key to be added
	 * @param value the desired value to be matched to the key
	 * @return true if the key/id pair was successfully registered
	 * @exception IllegalArgumentException if the id >= minId
	 */
	public boolean register(String key, T value);

	/**
	 * Saves the map to the persistence system
	 *
	 * @return returns true if the map saves correctly
	 */
	public boolean save();
    
}
