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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufs;

import org.spout.api.protocol.MessageCodec;
import org.spout.api.util.ByteBufUtils;
import org.spout.engine.protocol.builtin.message.CommandMessage;

public class CommandCodec extends MessageCodec<CommandMessage> {
	public CommandCodec(int opcode) {
		super(CommandMessage.class, opcode);
	}

	@Override
	public ByteBuf encode(CommandMessage message) {
		ByteBuf buffer = ByteBufs.dynamicBuffer();
		ByteBufUtils.writeString(buffer, message.getCommand());
		ByteBufUtils.writeStringArray(buffer, message.getArguments());
		return buffer;
	}

	@Override
	public CommandMessage decode(ByteBuf buffer) {
		final String command = ByteBufUtils.readString(buffer);
		final String[] args = ByteBufUtils.readStringArray(buffer);
		return new CommandMessage(command, args);
	}
}
