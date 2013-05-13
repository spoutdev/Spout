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
package org.spout.api.component.type;

import org.spout.api.component.BlockComponentHolder;
import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.discrete.Point;

public class BlockComponent extends Component {
	public BlockComponent() {
	}

	@Override
	public final BlockComponentHolder getOwner() {
		return (BlockComponentHolder) super.getOwner();
	}

	@Override
	public final boolean attachTo(ComponentOwner owner) {
		if (owner instanceof BlockComponentHolder) {
			return super.attachTo(owner);
		} else {
			return false;
		}
	}

	/**
	 * Gets the position of this block component
	 * @return position
	 */
	public Point getPosition() {
		if (getOwner() == null) {
			throw new IllegalStateException("Must have an attached owner!");
		}
		return new Point(getOwner().getWorld(), getOwner().getX(), getOwner().getY(), getOwner().getZ());
	}

	/**
	 * Called when a player interacts with this BlockMaterial
	 * @param entity that interacted with this component
	 * @param type action that the entity took on this component
	 */
	public void onInteract(Entity entity, Action type) {
	}
}
