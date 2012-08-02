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
package org.spout.api.util.map.concurrent;

public interface Atomic3DObjectMap<T> {
	
	/**
	 * Gets the value for the given (x, y, z) key, or null if none
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the value
	 */
	public T get(int x, int y, int z);
	
	/**
	 * Removes the key/value pair for the given (x, y, z) key
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return the value removed, or null on failure
	 */
	public T remove(int x, int y, int z);
	
	/**
	 * Removes the given key/value pair
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return true if the key/value pair was removed
	 */
	public boolean remove(int x, int y, int z, T value);
	
	/**
	 * Adds the given key/value pair to the map
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param value the non-null value
	 * @return the old value
	 */
	public T put(int x, int y, int z, T value);
	
	/**
	 * Adds the given key/value pair to the map, but only if the key does not already map to a value
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param value the non-null value
	 * @return the current value, or null on success
	 */
	public T putIfAbsent(int x, int y, int z, T value);

}
