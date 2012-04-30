/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.datatable;

/**
 * Interface for a Datatable Map.
 *
 */
public interface DatatableMap extends Outputable {
	/**
	 * Adds the Datatable Tuple to the map, using hashCode() as the key
	 * <br>
	 * The int used must correspond to a String key.
	 * 
	 * @param value
	 */
	public void set(DatatableTuple value);

	/**
	 * Adds the DatatableTuple to the map, using the int as the key.<br>
	 * <br>
	 * The given int must correspond to a String key.
	 *
	 * @param key
	 * @param value
	 */
	public void set(int key, DatatableTuple value);

	/**
	 * Adds the DatatableTuple to the map, using the string key. This triggers a
	 * string lookup and registration if necessary.
	 *
	 * @param key
	 * @param value
	 */
	public void set(String key, DatatableTuple value);
	
	/**
	 * Gets the integer key corresponding to a particular String.<br>
	 * <br>
	 * This method will register the String if no mapping already exists
	 * 
	 * @param key
	 * @return the int corresponding to the String
	 */
	public int getIntKey(String key);
	
	/**
	 * Gets the String key corresponding to a particular int.
	 * 
	 * @param key
	 * @return the String corresponding to the int, or null if no match
	 */
	public String getStringKey(int key);

	/**
	 * Gets the DatatableTuple corresponding to the given String key
	 * 
	 * @param key
	 * @return
	 */
	public DatatableTuple get(String key);
	
	/**
	 * Gets the DatatableTuple corresponding to the given int key
	 * 
	 * @param key
	 * @return
	 */
	public DatatableTuple get(int key);
	
	/**
	 * Gets if the map contains a particular key String
	 * 
	 * @param key
	 * @return true if the map contains the key
	 */
	public boolean contains(String key);
	
	/**
	 * Gets if the map contains a particular key int
	 * 
	 * @param key
	 * @return true if the int maps to a String and the map contains the key
	 */
	public boolean contains(int key);

	public byte[] compress();

	public void decompress(byte[] compressedData);
}
