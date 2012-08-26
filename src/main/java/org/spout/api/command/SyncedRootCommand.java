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
package org.spout.api.command;

import java.util.HashSet;
import java.util.Set;

import org.spout.api.Engine;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.Named;
import org.spout.api.util.StringMap;

/**
 * Variant of a root command that is synced with clients through a StringMap
 */
public class SyncedRootCommand extends RootCommand {
	private final StringMap commandNameRegistration;

	public SyncedRootCommand(Engine owner) {
		super(owner);
		commandNameRegistration = new StringMap(null, new MemoryStore<Integer>(), 0, Integer.MAX_VALUE, "commands" + getPreferredName());
	}

	public String getChildName(int id) {
		return commandNameRegistration.getString(id);
	}

	public Command getChild(int id) {
		final String name = getChildName(id);
		if (name == null) {
			return null;
		}
		return getChild(name);
	}

	@Override
	public Set<String> getChildNames() {
		return new HashSet<String>(commandNameRegistration.getKeys());
	}

	@Override
	public SimpleCommand addSubCommand(Named owner, String name) {
		SimpleCommand cmd = super.addSubCommand(owner, name);
		cmd.setId(commandNameRegistration.register(cmd.getPreferredName()));
		return cmd;
	}
}
