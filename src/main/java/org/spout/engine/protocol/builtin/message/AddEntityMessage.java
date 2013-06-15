/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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

import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.Spout;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.api.util.SpoutToStringStyle;

public class AddEntityMessage extends SpoutMessage {
	private final int entityId;
	private final UUID worldUid;
	private final Vector3 pos, scale;
	private final Quaternion rotation;

	public AddEntityMessage(int entityId, UUID worldUid, Transform transform, RepositionManager rm) {
		this(entityId, worldUid, transform.getPosition(), transform.getRotation(), transform.getScale(), rm);
	}

	public AddEntityMessage(int entityId, UUID worldUid, Vector3 pos, Quaternion rotation, Vector3 scale, RepositionManager rm) {
		this.entityId = entityId;
		this.worldUid = worldUid;
		this.pos = rm.convert(pos);
		this.rotation = rotation;
		this.scale = scale;
	}

	public int getEntityId() {
		return entityId;
	}

	public UUID getWorldUid() {
		return worldUid;
	}

	public Vector3 getPosition() {
		return pos;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public Transform getTransform() {
		return new Transform(new Point(pos, Spout.getEngine().getWorld(worldUid)), rotation, scale);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("entityId", entityId)
				.append("worldUid", worldUid)
				.append("pos", pos)
				.append("scale", scale)
				.append("rotation", rotation)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(65, 29)
				.append(entityId)
				.append(worldUid)
				.append(pos)
				.append(scale)
				.append(rotation)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AddEntityMessage) {
			final AddEntityMessage other = (AddEntityMessage) obj;
			return new EqualsBuilder()
					.append(entityId, other.entityId)
					.append(worldUid, other.worldUid)
					.append(pos, other.pos)
					.append(scale, other.scale)
					.append(rotation, other.rotation)
					.isEquals();
		} else {
			return false;
		}
	}
}
