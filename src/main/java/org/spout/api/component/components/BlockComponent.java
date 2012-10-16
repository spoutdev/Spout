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
package org.spout.api.component.components;

import org.spout.api.component.ChunkComponentOwner;
import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;

public class BlockComponent extends Component {
	public BlockComponent() {
	}

	@Override
	public final ChunkComponentOwner getOwner() {
		return (ChunkComponentOwner) super.getOwner();
	}

	@Override
	public final boolean attachTo(ComponentOwner owner) {
		if (owner instanceof ChunkComponentOwner) {
			return super.attachTo(owner);
		} else {
			return false;
		}
	}

	@Override
	public final boolean isDetachable() {
		return false;
	}

	/**
	 * Called when a player interacts with this BlockMaterial
	 *
	 * @param entity that interacted with this component
	 * @param type action that the entity took on this component
	 */
	public void onInteract(Entity entity, Action type) {
	}
}
