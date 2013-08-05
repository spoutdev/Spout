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
package org.spout.api.component.block;

import org.spout.api.component.BlockComponentOwner;
import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.entity.Entity;
import org.spout.api.event.entity.EntityInteractEvent;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;

public abstract class BlockComponent extends Component {
	@Override
	public final boolean attachTo(ComponentOwner owner) {
		if (!(owner instanceof BlockComponentOwner)) {
			throw new IllegalStateException("BlockComponents may only be attached to a BlockComponentOwner.");
		}
		return super.attachTo(owner);
	}

	@Override
	public final BlockComponentOwner getOwner() {
		return (BlockComponentOwner) super.getOwner();
	}

	/**
	 * Gets the position of this block component
	 *
	 * @return position
	 */
	public Point getPoint() {
		final BlockComponentOwner owner = getOwner();
		if (owner == null) {
			throw new IllegalStateException("This component has no owner and therefore no point");
		}
		return new Point(owner.getWorld(), owner.getX(), owner.getY(), owner.getZ());
	}

	/**
	 * Gets the {@link Block} who owns this component.
	 *
	 * The structure of BlockComponents differ from the other {@link ComponentOwner}s. {@link BlockComponentOwner} is what does BlockComponent management but Block itself owns the block. To keep things
	 * easy to access, this convenience method is provided.
	 *
	 * @return the block associated with the BlockComponentOwner
	 */
	public Block getBlock() {
		final BlockComponentOwner owner = getOwner();
		if (owner == null) {
			throw new IllegalStateException("This component has no owner and therefore no block");
		}
		return owner.getBlock();
	}

	/**
	 * Called when the owning {@link org.spout.api.geo.cuboid.Block} is collided with an {@link Entity}.
	 *
	 * @param point the point where collision occurred.
	 * @param entity the entity that collided with the owner <p> TODO EntityCollideBlockEvent
	 */
	public void onCollided(Point point, Entity entity) {

	}

	/**
	 * Called when the owner is interacted.
	 *
	 * @param event the event which was fired, resolved, and now passed on to the components.
	 */
	public void onInteract(final EntityInteractEvent<?> event) {
	}
}
