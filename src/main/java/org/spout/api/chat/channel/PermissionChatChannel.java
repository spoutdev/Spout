/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.chat.channel;

import com.google.common.collect.Sets;
import org.spout.api.Spout;
import org.spout.api.command.CommandSource;
import org.spout.api.permissions.PermissionsSubject;

import java.util.Collections;
import java.util.Set;

/**
 * An implementation of {@link ChatChannel} that gets receivers based on all players who have a certain permission
 */
public class PermissionChatChannel extends ChatChannel {
	private final String permission;

	public PermissionChatChannel(String name, String permission) {
		super(name);
		this.permission = permission;
	}

	@Override
	public Set<CommandSource> getReceivers() {
		Set<PermissionsSubject> permsResult = Spout.getEngine().getAllWithNode(permission);
		Set<CommandSource> ret = Sets.newHashSet();

		for (PermissionsSubject subj : permsResult) {
			if (subj instanceof CommandSource) {
				ret.add((CommandSource) subj);
			}
		}

		return Collections.unmodifiableSet(ret);
	}

	@Override
	public boolean isReceiver(CommandSource source) {
		return source.hasPermission(permission);
	}
}
