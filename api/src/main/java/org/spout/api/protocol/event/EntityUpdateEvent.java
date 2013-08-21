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
package org.spout.api.protocol.event;

import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.event.HandlerList;
import org.spout.api.event.ProtocolEvent;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.protocol.reposition.RepositionManager;

public class EntityUpdateEvent extends ProtocolEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Entity entity;
	private final Transform transform;
	private final UpdateAction action;
	private final RepositionManager rm;
	private final boolean fullSync;

	public EntityUpdateEvent(Entity entity, Transform transform, UpdateAction action, RepositionManager rm) {
		this(entity, transform, action, rm, action == UpdateAction.ADD || action == UpdateAction.REMOVE);
	}

	public EntityUpdateEvent(Entity entity, Transform transform, UpdateAction action, RepositionManager rm, boolean fullSync) {
		this.entity = entity;
		this.transform = transform;
		this.action = action;
		this.rm = rm;
		this.fullSync = fullSync;
	}

	public int getEntityId() {
		return entity.getId();
	}

	/**
	 * A shortcut method to get the entity that this message refers to
	 *
	 * @return
	 */
	public Entity getEntity() {
		return entity;
	}

	public Transform getTransform() {
		return transform;
	}

	public UpdateAction getAction() {
		return action;
	}

	public RepositionManager getRepositionManager() {
		return rm;
	}

	public boolean isFullSync() {
		return fullSync;
	}

	public enum UpdateAction {
		// TODO; protocol - use UpdatAction.POSITION?
		/**
		 * Signals for the client to spawn a new entity. (S -> C)
		 */
		ADD,
		/**
		 * Signals for the engine to update the entity's transform. S -> C for all entities. C -> S for players (to verify client movement)
		 */
		TRANSFORM,
		/**
		 * Signals for the engine to update the entity's position. S -> C for all entities. C -> S for players (to verify client movement)
		 *
		 * CURRENTLY UNIMPLEMENTED - deprecated until implemented
		 */
		@Deprecated
		POSITION,
		/**
		 * Signals the client to remove the entity. (S -> C)
		 */
		REMOVE;
		
		public boolean isUpdate() {
			return this == POSITION || this == TRANSFORM;
		}
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}