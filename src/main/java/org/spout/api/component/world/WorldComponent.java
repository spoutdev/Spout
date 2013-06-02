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
package org.spout.api.component.world;

import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.event.entity.EntityChangeWorldEvent;
import org.spout.api.event.entity.EntityDespawnEvent;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.geo.World;

public abstract class WorldComponent extends Component {
	@Override
	public boolean attachTo(ComponentOwner owner) {
		if (!(owner instanceof World)) {
			throw new IllegalStateException("WorldComponents are only allowed to be attached to Worlds.");
		}
		return super.attachTo(owner);
	}

	@Override
	public World getOwner() {
		return (World) super.getOwner();
	}

	/**
	 * Called when an {@link org.spout.api.entity.Entity} enters the owning {@link World}.
	 * <p/>
	 * This is only fired when the entity enters this owning world from another owning world. If you want to
	 * perform logic on a spawning entity for this owning world, {@see #onSpawn(org.spout.api.event.entity.EntitySpawnEvent)}.
	 * @param event see {@link org.spout.api.event.entity.EntityChangeWorldEvent}
	 */
	public void onEnter(final EntityChangeWorldEvent event) {
	}

	/**
	 * Called when an {@link org.spout.api.entity.Entity} exits the owning {@link World} for another.
	 * <p/>
	 * This is only fired when the entity will leave this owning world for another owning world. If you want to perform
	 * logic on a de-spawning entity, {@see #onDespawn(org.spout.api.event.entity.EntityDespawnEvent)}.
	 * @param event see {@link org.spout.api.event.entity.EntityChangeWorldEvent}
	 */
	public void onExit(final EntityChangeWorldEvent event) {
	}

	/**
	 * Called when an {@link org.spout.api.entity.Entity} spawns in the owning {@link World}.
	 * <p/>
	 * This is only fired when the entity is initially spawned into the owning world. If you want to perform login
	 * on an entity coming from another world, {@see #onEnter(org.spout.api.event.entity.EntityChangeWorldEvent)} or
	 * if you want to perform logic on an entity being loaded, {@see EntityComponent#onAttached()}.
	 * @param event see {@link org.spout.api.event.entity.EntitySpawnEvent}
	 */
	public void onSpawn(final EntitySpawnEvent event) {
	}

	/**
	 * Called when an {@link org.spout.api.entity.Entity} de-spawns from the owning {@link World}.
	 * <p/>
	 * This is only fired when the entity is de-spawned from the owning world ({@see org.spout.api.component.entity.EntityComponent#onDetached()}.
	 * If you want to perform logic on an entity moving from this owning world to another owning world, {@see #onExit(org.spout.api.event.entity.EntityChangeWorldEvent)}.
	 * @param event {@see org.spout.api.event.entity.EntitySpawnEvent}
	 */
	public void onDespawn(final EntityDespawnEvent event) {
	}
}
