/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.protocol.builtin.codec;

import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.builtin.ChannelBufferUtils;
import org.spout.api.protocol.builtin.message.EntityPositionMessage;

/**
 *
 */
public class EntityPositionCodec extends MessageCodec<EntityPositionMessage> {
	public EntityPositionCodec() {
		super(EntityPositionMessage.class, 0x07);
	}

	@Override
	public ChannelBuffer encode(EntityPositionMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(4 + ChannelBufferUtils.TRANSFORM_SIZE);
		buffer.writeInt(message.getEntityId());
		ChannelBufferUtils.writeUUID(buffer, message.getWorldUid());
		ChannelBufferUtils.writeVector3(buffer, message.getPosition());
		ChannelBufferUtils.writeQuaternion(buffer, message.getRotation());
		ChannelBufferUtils.writeVector3(buffer, message.getScale());
		return buffer;
	}

	@Override
	public EntityPositionMessage decode(ChannelBuffer buffer) {
		final int entityId = buffer.readInt();
		final UUID worldUid = ChannelBufferUtils.readUUID(buffer);
		final Vector3 position = ChannelBufferUtils.readVector3(buffer);
		final Quaternion rotation = ChannelBufferUtils.readQuaternion(buffer);
		final Vector3 scale = ChannelBufferUtils.readVector3(buffer);
		return new EntityPositionMessage(entityId, worldUid, position, rotation, scale);
	}
}
