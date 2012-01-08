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
package org.spout.api.event.server.permissions;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;
import org.spout.api.geo.World;
import org.spout.api.permissions.PermissionsSubject;

/**
 * This event is called when PermissionSubject.getGroups() is called.
 */
public class PermissionGetGroupsEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private World world;
	private PermissionsSubject subject;
	private String[] groups;

	public PermissionGetGroupsEvent(World world, PermissionsSubject subject) {
		this.world = world;
		this.subject = subject;
	}

	public String[] getGroups() {
		return groups;
	}

	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	public PermissionsSubject getSubject() {
		return subject;
	}

	public void setSubject(PermissionsSubject subject) {
		this.subject = subject;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
