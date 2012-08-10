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
package org.spout.api.entity;

import java.io.Serializable;

import org.spout.api.entity.controller.type.ControllerType;
import org.spout.api.event.player.PlayerInteractEvent;
import org.spout.api.map.DefaultedMap;
import org.spout.api.tickable.Tickable;

public interface Controller extends ComponentHolder, Tickable {
	/**
	 * Called when this entity is attached to an entity.
	 * <br/><br/>
	 * If this entity was serialized and deserialized, any serializable
	 * information stored in {@link #getDataMap()} will be available.
	 */
	public abstract void onAttached();

	/**
	 * Called when this entity is detached from the entity (normally due to the entity dieing or being removed from the world).
	 * Occurs before the Pre-Snapshot of the tick.
	 */
	public void onDeath();

	/**
	 * Called just before the pre-snapshot stage.
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
	 */
	public void finalizeTick();

	/**
	 * Is called when an Entity interacts with this Controller
	 * @param entity that interacted
	 * @param type of interaction
	 */
	public void onInteract(Entity entity, PlayerInteractEvent.Action type);

	/**
	 * Returns the type of entity
	 * @return entity type
	 */
	public ControllerType getType();

	/**
	 * Gets a map of persistent string mapped serializable values attached to this entity.
	 * This map can be used to store any data relevant to the entity.
	 * <br/> <br/>
	 * This map is thread-safe, and will be saved between restarts if the entity {@link #isSavable()}.
	 * @return thread-safe persistent storage map
	 */
	public DefaultedMap<String, Serializable> getDataMap();

	/**
	 * Called immediately <i>before</i> a entity and it's parent entity are
	 * serialized. This method is intended as the last chance to store serializable
	 * information inside of the entity data map (see: {@link #getDataMap()})
	 * <br/><br/>
	 * <b>Note:</b> This will never occur is {@link #isSavable()} is false. <br/>
	 * <b>Note:</b> onSave occurs during Copy Snapshot. During this stage
	 * all live values are copied to their stable snapshot. Data
	 * is unstable so no reads are permitted during this stage.
	 */
	public void onSave();

	/**
	 * True if this entity and it's parent entity should be saved.
	 * @return save
	 */
	public boolean isSavable();

	/**
	 * Is important is a hint to the entity manager that this entity should be
	 * considered important in regards to ticks and collisions. When unimportant
	 * entities are far away from players or present in empty worlds, they will be
	 * ticked less frequently, and may have more lax collisions. This allows regions
	 * to optimize and only tick and collide entities that seem critical to players,
	 * the regions near players, and the game logic of the world.
	 * <p/>
	 * Important controllers are exempt from these optimizations and will be ticked
	 * on schedule and treated as if it were a player or observer. This will make the
	 * entity more expensive in terms of performance, and importance should not be
	 * given to non-players and non-observers lightly. In general, there should be
	 * very few cases where importance needs to be adjusted manually.
	 * <p/>
	 * <b>Note:</b> If a entity is an observer, it is always considered
	 * to be important. Players are also always considered important.
	 * @return important
	 */
	public boolean isImportant();

	/**
	 * Gets the entity this entity is attached to.
	 * @return The entity this entity is attached to
	 */
	public Entity getParent();

	/**
	 * Attaches to the entity and sets the parent as that entity.
	 * @param parent The Entity this entity controls
	 */
	public void attachToEntity(Entity parent);
}
