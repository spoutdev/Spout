/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.event.server.permissions;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spout.api.event.HandlerList;
import org.spout.api.event.Result;
import org.spout.api.event.server.NodeBasedEvent;
import org.spout.api.permissions.PermissionsSubject;

/**
 * This event is called to gather the PermissionsSubjects with the given permission set.</br> Plugins responsible for managing PermissionsSubjects should add them to the list of subjects.
 */
public class PermissionGetAllWithNodeEvent extends NodeBasedEvent {
	private final Map<PermissionsSubject, Result> receivers = new HashMap<>();
	private static final HandlerList handlers = new HandlerList();

	public PermissionGetAllWithNodeEvent(String node) {
		super(node);
	}

	/**
	 * Returns the map of receivers. This map is modified to add applicable receivers
	 *
	 * @return The receivers map
	 */
	public Map<PermissionsSubject, Result> getReceivers() {
		return receivers;
	}

	public Set<PermissionsSubject> getAllowedReceivers() {
		Set<PermissionsSubject> ret = new HashSet<>();
		for (Map.Entry<PermissionsSubject, Result> entry : receivers.entrySet()) {
			if (entry.getValue() == Result.ALLOW) {
				ret.add(entry.getKey());
			}
		}
		return Collections.unmodifiableSet(ret);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
