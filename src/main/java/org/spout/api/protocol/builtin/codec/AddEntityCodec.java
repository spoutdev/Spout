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
package org.spout.api.protocol.builtin.codec;

import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.entity.controller.type.ControllerRegistry;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.builtin.ChannelBufferUtils;
import org.spout.api.protocol.builtin.message.AddEntityMessage;

public class AddEntityCodec extends MessageCodec<AddEntityMessage> {
	public AddEntityCodec() {
		super(AddEntityMessage.class, 0x04);
	}

	@Override
	public ChannelBuffer encode(AddEntityMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(8 + ChannelBufferUtils.UUID_SIZE + ChannelBufferUtils.VECTOR3_SIZE * 2 + ChannelBufferUtils.QUATERNINON_SIZE);
		buffer.writeInt(message.getEntityId());
		buffer.writeInt(message.getType().getId());
		ChannelBufferUtils.writeUUID(buffer, message.getWorldUid());
		ChannelBufferUtils.writeVector3(buffer, message.getPosition());
		ChannelBufferUtils.writeQuaternion(buffer, message.getRotation());
		ChannelBufferUtils.writeVector3(buffer, message.getScale());
		return buffer;
	}

	@Override
	public AddEntityMessage decode(ChannelBuffer buffer) {
		final int entityId = buffer.readInt();
		final int controllerTypeId = buffer.readInt();
		final UUID worldUid = ChannelBufferUtils.readUUID(buffer);
		final Vector3 position = ChannelBufferUtils.readVector3(buffer);
		final Quaternion rotation = ChannelBufferUtils.readQuaternion(buffer);
		final Vector3 scale = ChannelBufferUtils.readVector3(buffer);
		return new AddEntityMessage(entityId, ControllerRegistry.get(controllerTypeId), worldUid, position, rotation, scale);
	}
}
