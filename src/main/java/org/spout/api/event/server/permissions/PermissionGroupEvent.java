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
package org.spout.api.event.server.permissions;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;
import org.spout.api.geo.World;
import org.spout.api.permissions.PermissionsSubject;

/**
 * This event is called when {@link PermissionSubject#isInGroup()} is called.
 */
public class PermissionGroupEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final World world;
	private final PermissionsSubject subject;
	private final String group;
	private boolean result = false;

	public PermissionGroupEvent(World world, PermissionsSubject subject, String group) {
		this.world = world;
		this.subject = subject;
		this.group = group;
	}

	/**
	 * The group that the subject is being tested for.
	 * 
	 * @return name of the group.
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * The current result of the check, true if the subject is in the group.<br/>
	 * default: false
	 * @return true if in the group, otherwise false.
	 */
	public boolean getResult() {
		return result;
	}

	/**
	 * Sets the result of this permission check.
	 * 
	 * @param result to set.
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**
	 * The subject being checked by the event.
	 * 
	 * @return subject
	 */
	public PermissionsSubject getSubject() {
		return subject;
	}

	/**
	 * The world this is being checked for, or false if it's a global check.
	 * 
	 * @return world being checked, or null if global.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * name of the world being checked, or null if this is a global check.
	 * 
	 * @return name of the world, or null if global.
	 */
	public String getWorldName() {
		return world == null ? null : world.getName();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
