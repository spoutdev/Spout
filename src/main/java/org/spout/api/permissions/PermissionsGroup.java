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
import org.spout.api.data.ValueHolder;
import org.spout.api.event.EventManager;
import org.spout.api.event.Result;
import org.spout.api.event.server.data.RetrieveDataEvent;
import org.spout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.spout.api.event.server.permissions.PermissionGroupEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.api.geo.World;
import org.spout.api.util.Named;

/**
 * Represents a user group as a permissions subject. This allows for checking group inheritance, group permissions, etc.
 *
 */
public class PermissionsGroup implements PermissionsSubject, DataSubject {
	private EventManager manager;
	private String name;

	public PermissionsGroup(EventManager manager, String name) {
		this.manager = manager;
		this.name = name;
	}

	@Override
	public boolean hasPermission(String node) {
		return hasPermission(null, node);
	}

	@Override
	public boolean hasPermission(World world, String node) {
		PermissionNodeEvent event = manager.callEvent(new PermissionNodeEvent(world, this, node));
		if (event.getResult() == Result.DEFAULT) {
			return false;
		}

		return event.getResult().getResult();
	}

	@Override
	public boolean isInGroup(String group) {
		PermissionGroupEvent event = manager.callEvent(new PermissionGroupEvent(null, this, group));
		return event.getResult();
	}

	@Override
	public String[] getGroups() {
		PermissionGetGroupsEvent event = manager.callEvent(new PermissionGetGroupsEvent(null, this));
		return event.getGroups();
	}

	@Override
	public boolean isGroup() {
		return false;
	}

	@Override
	public ValueHolder getData(String node) {
		RetrieveDataEvent event = manager.callEvent(new RetrieveDataEvent(this, node));
		return event.getResult();
	}

	public String getName() {
		return name;
	}
}
