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

import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.datatable.ManagedMap;
import org.spout.api.datatable.delta.DeltaMap;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.SpoutToStringStyle;

public class WorldChangeMessage extends DatatableMessage {
	private final String worldName;
	private final UUID worldUUID;
	private final Vector3 position;
	private final Quaternion rotation;
	private final Vector3 scale;

	public WorldChangeMessage(World world, Transform playerTransform, ManagedMap data) {
		this(world.getName(), world.getUID(), playerTransform, data.getDeltaMap().serialize(), data.getDeltaMap().getType());
	}

	public WorldChangeMessage(String worldName, UUID worldUUID, Transform playerTransform, byte[] compressedData, DeltaMap.DeltaType type) {
		super(compressedData, type);
		this.worldName = worldName;
		this.worldUUID = worldUUID;
		// This MUST copy as a Vector3 for tests
		this.position = new Vector3(playerTransform.getPosition());
		this.rotation = playerTransform.getRotation();
		this.scale = playerTransform.getScale();
	}

	public WorldChangeMessage(String worldName, UUID worldUUID, Vector3 position, Quaternion rotation, Vector3 scale, byte[] compressedData, DeltaMap.DeltaType type) {
		super(compressedData, type);
		this.worldName = worldName;
		this.worldUUID = worldUUID;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public UUID getWorldUUID() {
		return worldUUID;
	}

	public String getWorldName() {
		return worldName;
	}

	public Transform getPlayerTransform() {
		if (Spout.getPlatform() == Platform.CLIENT) {
			return new Transform(new Point(position, ((Client) Spout.getEngine()).getWorld()), rotation, scale);
		} else {
			return new Transform(new Point(position, ((Server) Spout.getEngine()).getWorld(worldUUID)), rotation, scale);
		}
	}

	public Vector3 getPosition() {
		return position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.appendSuper(super.toString())
				.append("worldName", worldName)
				.append("worldUuid", worldUUID)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(85, 87)
				.append(getCompressedData())
				.append(worldName)
				.append(worldUUID)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WorldChangeMessage) {
			final WorldChangeMessage other = (WorldChangeMessage) obj;
			return new EqualsBuilder()
					.append(getCompressedData(), other.getCompressedData())
					.append(worldName, other.worldName)
					.append(worldUUID, other.worldUUID)
					.isEquals();
		} else {
			return false;
		}
	}
}
