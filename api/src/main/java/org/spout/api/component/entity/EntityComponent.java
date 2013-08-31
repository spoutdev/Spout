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
package org.spout.api.component.entity;

import java.util.Random;

import org.spout.api.Engine;
import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.entity.Entity;
import org.spout.api.event.entity.EntityCollideEvent;
import org.spout.api.event.entity.EntityInteractEvent;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.math.GenericMath;

/**
 * Represents a component who shapes the logic behind an {@link Entity}.
 */
public abstract class EntityComponent extends Component {
	@Override
	public final boolean attachTo(ComponentOwner owner) {
		if (!(owner instanceof Entity)) {
			throw new IllegalStateException("EntityComponents may only be attached to Entities.");
		}
		return super.attachTo(owner);
	}

	@Override
	public Entity getOwner() {
		return (Entity) super.getOwner();
	}

	/**
	 * Returns a deterministic random number generator
	 *
	 * @return random the random generator
	 */
	public final Random getRandom() {
		return GenericMath.getRandom();
	}

	public final Engine getEngine() {
		final Entity owner = getOwner();
		if (owner == null) {
			throw new IllegalStateException("Can not access the engine w/o an owner");
		}
		return owner.getEngine();
	}

	/**
	 * Called when the owner is spawned into the {@link org.spout.api.geo.World}. <p> This should be used for setting up the owner with initial logic as the owner is only spawned once. For executing
	 * logic that happens each time the owner is attached with this EntityComponent, {@see org.spout.api.component.Component #onAttached()}.
	 */
	public void onSpawned(final EntitySpawnEvent event) {
	}

	/**
	 * Called when the owner comes within range of another owner with an attached {@link ObserverComponent}. <p> TODO EntityObservedEvent
	 */
	public void onObserved() {
	}

	/**
	 * Called when the owner is out of range of any owners with attached {@link ObserverComponent}s. <p> TODO EntityUnObservedEvent
	 */
	public void onUnObserved() {
	}

	/**
	 * Called when the owning {@link Entity} has incurred a collision.
	 */
	public void onCollided(final EntityCollideEvent<?> event) {

	}

	/**
	 * Called when the owner is interacted.
	 *
	 * @param event {@see org.spout.api.event.entity.EntityInteractEvent}
	 */
	public void onInteract(final EntityInteractEvent<?> event) {
	}
}
