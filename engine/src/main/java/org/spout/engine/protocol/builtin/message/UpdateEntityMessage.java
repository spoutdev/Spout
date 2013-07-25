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
package org.spout.engine.protocol.builtin.message;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.spout.api.geo.discrete.Transform;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.SpoutToStringStyle;

public class UpdateEntityMessage extends SpoutMessage {
	private final int entityId;
	private final Transform transform;
	private final UpdateAction action;

	// TODO: protocol - implement position-only update
	// TODO: possibly combine Entity Datatable message here?
	public UpdateEntityMessage(int entityId, Transform transform, UpdateAction action, RepositionManager rm) {
		this.entityId = entityId;
		this.transform = transform;
		this.action = action;
	}

	public int getEntityId() {
		return entityId;
	}

	/**
	 * @return a copy of the converted transform
	 */
	public Transform getTransform() {
		return transform.copy();
	}

	public UpdateAction getAction() {
		return action;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("entityId", entityId)
				.append("transform", transform)
				.append("action", action)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(37, 59)
				.append(entityId)
				.append(transform)
				.append(action)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UpdateEntityMessage) {
			final UpdateEntityMessage other = (UpdateEntityMessage) obj;
			return new EqualsBuilder()
					.append(entityId, other.entityId)
					.append(transform, other.transform)
					.append(action, other.action)
					.isEquals();
		} else {
			return false;
		}
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
	}
}
