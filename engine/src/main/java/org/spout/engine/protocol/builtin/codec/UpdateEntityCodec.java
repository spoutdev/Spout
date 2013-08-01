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
package org.spout.engine.protocol.builtin.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.spout.api.geo.discrete.Transform;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.event.UpdateEntityEvent.UpdateAction;
import org.spout.api.protocol.reposition.NullRepositionManager;
import org.spout.api.util.ChannelBufferUtils;
import org.spout.engine.protocol.builtin.message.UpdateEntityMessage;

public class UpdateEntityCodec extends MessageCodec<UpdateEntityMessage> {
	public UpdateEntityCodec(int opcode) {
		super(UpdateEntityMessage.class, opcode);
	}

	@Override
	public ChannelBuffer encode(UpdateEntityMessage message) {
		ChannelBuffer buffer = null;
		switch (message.getAction()) {
			case REMOVE:
				buffer = ChannelBuffers.buffer(4);
				buffer.writeByte(message.getAction().ordinal());
				buffer.writeInt(message.getEntityId());
				break;
			case ADD:
			case TRANSFORM:
				buffer = ChannelBuffers.buffer(5 + ChannelBufferUtils.UUID_SIZE + ChannelBufferUtils.VECTOR3_SIZE * 2 + ChannelBufferUtils.QUATERNINON_SIZE);
				buffer.writeByte(message.getAction().ordinal());
				buffer.writeInt(message.getEntityId());
				ChannelBufferUtils.writeTransform(buffer, message.getTransform());
				break;
			case POSITION:
				throw new UnsupportedOperationException("Position is unimplemented!");
			default:
				throw new IllegalArgumentException("Unknown UpdateAction!");
		}
		return buffer;
	}

	@Override
	public UpdateEntityMessage decode(ChannelBuffer buffer) {
		final byte actionByte = buffer.readByte();
		if (actionByte < 0 || actionByte >= UpdateAction.values().length) {
			throw new IllegalArgumentException("Unknown response ID " + actionByte);
		}

		final UpdateAction action = UpdateAction.values()[actionByte];
		final int entityId;
		final Transform transform;
		switch (action) {
			case REMOVE:
				entityId = buffer.readInt();
				transform = null;
				break;
			case ADD:
			case TRANSFORM:
				entityId = buffer.readInt();
				transform = ChannelBufferUtils.readTransform(buffer);
				break;
			case POSITION:
				throw new UnsupportedOperationException("Position is unimplemented!");
			default:
				throw new IllegalArgumentException("Unknown UpdateAction!");
		}

		return new UpdateEntityMessage(entityId, transform, action, NullRepositionManager.getInstance());
	}
}
