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

import java.util.Random;

import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.GenericMath;

/**
 * Represents an attachment to a entity that can respond to Ticks.
 */
public abstract class EntityComponent extends Component {
	@Override
	public Entity getOwner() {
		return (Entity) super.getOwner();
	}

	@Override
	public final boolean attachTo(ComponentOwner holder) {
		if (holder instanceof Entity) {
			return super.attachTo(holder);
		} else {
			return false;
		}
	}

	/**
	 * <<<<<<< HEAD
	 * Returns a deterministic random number generator
	 * @return random
	 *         >>>>>>> scene
	 */
	public final Random getRandom() {
		return GenericMath.getRandom();
	}

	/**
	 * Called when the parent entity is spawned into the world.
	 */
	public void onSpawned() {
	}

	/**
	 * Called when the entity changes from unobserved to observed.
	 */
	public void onObserved() {
	}

	/**
	 * Called when the entity changes from observed to unobserved.
	 */
	public void onUnObserved() {
	}

	/**
	 * Called when the entity collides with another entity.
	 * @param colliderPoint The point where this entity collided with the other entity
	 * @param collidedPoint The point where the other entity collided with this entity
	 * @param entity The other entity that was collided with this entity
	 */
	public void onCollided(Point colliderPoint, Point collidedPoint, Entity entity) {

	}

	/**
	 * Called when the entity collides with a block.
	 * @param colliderPoint The point where this entity collided with the material
	 * @param collidedPoint The point where the material was collided with the entity
	 * @param block The block this entity collided with
	 */
	public void onCollided(Point colliderPoint, Point collidedPoint, Block block) {

	}

	/**
	 * Called when the entity is interacted with.
	 * @param action being performed
	 * @param source performing the action
	 */
	public void onInteract(Action action, Entity source) {

	}
}
