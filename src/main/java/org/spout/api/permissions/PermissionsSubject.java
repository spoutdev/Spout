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

import org.spout.api.geo.World;
import org.spout.api.util.Named;

/**
 * Interface for classes that can have permissions attached to them.
 *
 */
public interface PermissionsSubject extends Named {
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
	 * Gets whether or not the {@link Named} is in a given group
	 *
	 * @param group name
	 * @return true if the Named is in the group, otherwise false.
	 */
	public boolean isInGroup(String group);

	/**
	 * Gets the groups this {@link Named} is in.
	 *
	 * @return array of group names.
	 */
	public String[] getGroups();

	/**
	 * Returns whether or not this Named is a group itself
	 *
	 * @return true if this is a group.
	 */
	public boolean isGroup();
}
