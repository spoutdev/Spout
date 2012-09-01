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
package org.spout.api.permissions;

import org.spout.api.data.DataSubject;
import org.spout.api.geo.World;
import org.spout.api.util.Named;

/**
 * Interface for classes that can have permissions attached to them.
 */
public interface PermissionsSubject extends DataSubject {
	/**
	 * Gets whether or not the {@link Named} has a given permission
	 *
	 * @param node to check
	 * @return true if the Named has the permission, otherwise false.
	 */
	public boolean hasPermission(String node);

	/**
	 * Gets whether or not the {@link Named} has a given permission in a {@link World}
	 *
	 * @param node to check
	 * @return true if the Named has permission in the given world, otherwise false.
	 */
	public boolean hasPermission(World world, String node);

	/**
	 * Whether or not the subject is in the specified group.
	 *
	 * @param group to check
	 * @return true if in group
	 */
	public boolean isInGroup(String group);

	/**
	 * Whether or not the subject is in the specified group in the specified world.
	 *
	 * @param world of player
	 * @param group to check
	 * @return true if in group
	 */
	public boolean isInGroup(World world, String group);

	/**
	 * Gets all groups in the players current world.
	 *
	 * @return all groups
	 */
	public String[] getGroups();

	/**
	 * Gets all groups in the specified world.
	 *
	 * @param world to check
	 * @return all groups from world
	 */
	public String[] getGroups(World world);
}
