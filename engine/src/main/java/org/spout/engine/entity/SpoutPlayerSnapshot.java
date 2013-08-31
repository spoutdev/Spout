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
package org.spout.engine.entity;

import java.util.List;
import java.util.UUID;

import org.spout.api.component.Component;
import org.spout.api.entity.Player;
import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.geo.discrete.Transform;

public class SpoutPlayerSnapshot extends SpoutEntitySnapshot implements PlayerSnapshot {
	private final String name;

	public SpoutPlayerSnapshot(Player p) {
		super(p);
		name = p.getName();
	}

	public SpoutPlayerSnapshot(PlayerSnapshot snapshot) {
		this(snapshot.getUID(), snapshot.getTransform(), snapshot.getWorldUID(), null, snapshot.getComponents(), snapshot.getName());
		this.getDataMap().putAll(snapshot.getDataMap());
	}

	public SpoutPlayerSnapshot(UUID id, Transform t, UUID worldId, byte[] dataMap, List<Class<? extends Component>> types, String name) {
		super(id, t, worldId, dataMap, types);
		this.name = name;
	}

	@Override
	public boolean isSavable() {
		return true;
	}

	@Override
	public Player getReference() {
		return (Player) super.getReference();
	}

	@Override
	public String getName() {
		return name;
	}
}
