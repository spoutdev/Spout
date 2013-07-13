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
package org.spout.engine.protocol.builtin.codec;

import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.MessageCodec;

import org.spout.engine.protocol.builtin.ChannelBufferUtils;
import org.spout.engine.protocol.builtin.message.WorldChangeMessage;

public class WorldChangeCodec extends MessageCodec<WorldChangeMessage> {
	public WorldChangeCodec() {
		super(WorldChangeMessage.class, 0x02);
	}

	@Override
	public ChannelBuffer encode(WorldChangeMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.writeString(buffer, message.getWorldName());
		ChannelBufferUtils.writeUUID(buffer, message.getWorldUUID());
		ChannelBufferUtils.writeVector3(buffer, message.getPosition());
		ChannelBufferUtils.writeQuaternion(buffer, message.getRotation());
		ChannelBufferUtils.writeVector3(buffer, message.getScale());
		buffer.writeInt(message.getCompressedData().length);
		buffer.writeBytes(message.getCompressedData());
		return buffer;
	}

	@Override
	public WorldChangeMessage decode(ChannelBuffer buffer) {
		final String worldName = ChannelBufferUtils.readString(buffer);
		final UUID worldUUID = ChannelBufferUtils.readUUID(buffer);
		final Vector3 position = ChannelBufferUtils.readVector3(buffer);
		final Quaternion rotation = ChannelBufferUtils.readQuaternion(buffer);
		final Vector3 scale = ChannelBufferUtils.readVector3(buffer);
		final byte[] compressedData = new byte[buffer.readInt()];
		buffer.readBytes(compressedData);
		return new WorldChangeMessage(worldName, worldUUID, position, rotation, scale, compressedData);
	}
}
