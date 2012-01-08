/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.permissions;

import org.spout.api.data.DataSubject;
import org.spout.api.event.EventManager;
import org.spout.api.event.Result;
import org.spout.api.event.server.data.RetrieveIntDataEvent;
import org.spout.api.event.server.data.RetrieveObjectDataEvent;
import org.spout.api.event.server.data.RetrieveStringDataEvent;
import org.spout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.spout.api.event.server.permissions.PermissionGroupEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.api.geo.World;

/**
 * Represents a user group as a permissions subject. This allows for
 * checking group inheritance, group permissions, etc.
 * 
 * @author yetanotherx
 */
public class PermissionsGroup implements PermissionsSubject, DataSubject {

	private EventManager manager;
	private String name;

	public PermissionsGroup(EventManager manager, String name) {
		this.manager = manager;
		this.name = name;
	}

	public boolean hasPermission(String node) {
		return hasPermission(null, node);
	}

	public boolean hasPermission(World world, String node) {
		PermissionNodeEvent event = manager.callEvent(new PermissionNodeEvent(world, this, node));
		if (event.getResult() == Result.DEFAULT) {
			return false;
		}

		return event.getResult().getResult();
	}

	public boolean isInGroup(String group) {
		PermissionGroupEvent event = manager.callEvent(new PermissionGroupEvent(null, this, group));
		return event.getResult();
	}

	public String[] getGroups() {
		PermissionGetGroupsEvent event = manager.callEvent(new PermissionGetGroupsEvent(null, this));
		return event.getGroups();
	}

	public boolean isGroup() {
		return false;
	}

	public Object getData(String node) {
		return getData(node, null);
	}

	public Object getData(String node, Object defaultValue) {
		return getData(null, node, defaultValue);
	}

	public Object getData(World world, String node) {
		return getData(world, node, null);
	}

	public Object getData(World world, String node, Object defaultValue) {
		RetrieveObjectDataEvent event = manager
				.callEvent(new RetrieveObjectDataEvent(world, this, node));
		Object res = event.getResult();
		if (res == null) {
			return defaultValue;
		}
		return res;
	}

	public int getInt(String node) {
		return getInt(node, RetrieveIntDataEvent.DEFAULT_VALUE);
	}

	public int getInt(String node, int defaultValue) {
		return getInt(null, node, defaultValue);
	}

	public int getInt(World world, String node) {
		return getInt(world, node, RetrieveIntDataEvent.DEFAULT_VALUE);
	}

	public int getInt(World world, String node, int defaultValue) {
		RetrieveIntDataEvent event = manager.callEvent(new RetrieveIntDataEvent(world, this, node));
		int res = event.getResult();
		if (res == RetrieveIntDataEvent.DEFAULT_VALUE) {
			return defaultValue;
		}
		return res;
	}

	public String getString(String node) {
		return getString(node, null);
	}

	public String getString(String node, String defaultValue) {
		return getString(null, node, defaultValue);
	}

	public String getString(World world, String node) {
		return getString(world, node, null);
	}

	public String getString(World world, String node, String defaultValue) {
		RetrieveStringDataEvent event = manager.callEvent(new RetrieveStringDataEvent(world, this, node));
		String res = event.getResult();
		if (res == null) {
			return defaultValue;
		}
		return res;
	}

	public String getName() {
		return name;
	}
}
