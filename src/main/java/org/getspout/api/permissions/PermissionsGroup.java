/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * The SpoutAPI is licensed under the SpoutDev license version 1.
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
package org.getspout.api.permissions;

import org.getspout.api.data.DataSubject;
import org.getspout.api.event.EventManager;
import org.getspout.api.event.Result;
import org.getspout.api.event.server.data.RetrieveDataEvent;
import org.getspout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.getspout.api.event.server.permissions.PermissionGroupEvent;
import org.getspout.api.event.server.permissions.PermissionNodeEvent;
import org.getspout.api.geo.World;

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

	@Override
	public boolean hasPermission(String node) {
		return hasPermission(null, node);
	}

	@Override
	public boolean hasPermission(World world, String node) {
		PermissionNodeEvent event = new PermissionNodeEvent(world, this, node);
		manager.callEvent(event);
		if (event.getResult() == Result.DEFAULT) {
			return false;
		}

		return event.getResult().getResult();
	}

	@Override
	public boolean isInGroup(String group) {
		PermissionGroupEvent event = new PermissionGroupEvent(null, this, group);
		manager.callEvent(event);
		return event.getResult();
	}

	@Override
	public String[] getGroups() {
		PermissionGetGroupsEvent event = new PermissionGetGroupsEvent(null, this);
		manager.callEvent(event);
		return event.getGroups();
	}

	public String getName() {
		return name;
	}

	public boolean isGroup() {
		return true;
	}
	
	public Object getData(String node) {
		return getData((World) null, node);
	}
	
	public Object getData(String node, Object defaultValue) {
		return getData(null, node, defaultValue);
	}
	
	public Object getData(World world, String node) {
		RetrieveDataEvent event = new RetrieveDataEvent(world, this, node);
		manager.callEvent(event);
		return event.getResult();
	}
	
	public Object getData(World world, String node, Object defaultValue) {
		RetrieveDataEvent event = new RetrieveDataEvent(world, this, node);
		manager.callEvent(event);
		Object res = event.getResult();
		if( res == null ) {
			return defaultValue;
		}
		return res;
	}
}
