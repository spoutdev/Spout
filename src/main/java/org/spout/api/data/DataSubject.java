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
package org.spout.api.data;

import org.spout.api.geo.World;
import org.spout.api.util.Named;

public interface DataSubject extends Named {
	/**
	 * Gets data from the given node
	 * @param node
	 * @return null if the data is not set
	 */
	public Object getData(String node);

	/**
	 * Gets data from the given node
	 * @param node
	 * @param defaultValue
	 * @return null if the data is not set
	 */
	public Object getData(String node, Object defaultValue);

	/**
	 * Gets data from the given node for the given world
	 * @param world
	 * @param node
	 * @return null if the data is not set
	 */
	public Object getData(World world, String node);

	/**
	 * Gets data from the given node for the given world
	 * @param world
	 * @param node
	 * @param defaultValue
	 * @return null if the data is not set
	 */
	public Object getData(World world, String node, Object defaultValue);

	/**
	 * Gets data from the given node
	 * @param node
	 * @return null if the data is not set or the data is not an int
	 */
	public int getInt(String node);

	/**
	 * Gets data from the given node
	 * @param node
	 * @param defaultValue
	 * @return null if the data is not set or the data is not an int
	 */
	public int getInt(String node, int defaultValue);

	/**
	 * Gets int data from the given node for the given world
	 * @param world
	 * @param node
	 * @return null if the data is not set or the data is not an int
	 */
	public int getInt(World world, String node);

	/**
	 * Gets data from the given node
	 * @param world
	 * @param node
	 * @param defaultValue
	 * @return null if the data is not set or the data is not an int
	 */
	public int getInt(World world, String node, int defaultValue);

	/**
	 * Gets data from the given node
	 * @param node
	 * @return null if the data is not set or the data is not a string
	 */
	public String getString(String node);

	/**
	 * Gets data from the given node
	 * @param node
	 * @param defaultValue
	 * @return null if the data is not set or the data is not a string
	 */
	public String getString(String node, String defaultValue);

	/**
	 * Gets data from the given node
	 * @param world
	 * @param node
	 * @return null if the data is not set or the data is not a string
	 */
	public String getString(World world, String node);

	/**
	 * Gets data from the given node
	 * @param world
	 * @param node
	 * @param defaultValue
	 * @return null if the data is not set or the data is not a string
	 */
	public String getString(World world, String node, String defaultValue);
}
