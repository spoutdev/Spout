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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.builtin.message.PlayerInputMessage;

public class PlayerInputCodec extends MessageCodec<PlayerInputMessage> {
	public static final byte FORWARD_BIT = 1 << 0,
			BACK_BIT = 1 << 1,
			LEFT_BIT = 1 << 2,
			RIGHT_BIT = 1 << 3,
			MOUSEWHEEL_UP_BIT = 1 << 4,
			MOUSEWHEEL_DOWN_BIT = 1 << 5;

	public PlayerInputCodec() {
		super(PlayerInputMessage.class, 0x0C, true);
	}

	@Override
	public ChannelBuffer encode(PlayerInputMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(5);
		byte actions = 0;
		if (message.isFwd()) {
			actions |= FORWARD_BIT;
		}

		if (message.isBack()) {
			actions |= BACK_BIT;
		}

		if (message.isLeft()) {
			actions |= LEFT_BIT;
		}

		if (message.isRight()) {
			actions |= RIGHT_BIT;
		}

		if (message.isMouseWheelUp()) {
			actions |= MOUSEWHEEL_UP_BIT;
		}

		if (message.isMouseWheelDown()) {
			actions |= MOUSEWHEEL_DOWN_BIT;
		}

		buffer.writeByte(actions);
		buffer.writeShort(message.getMouseDx());
		buffer.writeShort(message.getMouseDy());
		return buffer;
	}

	@Override
	public PlayerInputMessage decode(ChannelBuffer buffer) {
		final byte actions = buffer.readByte();
		boolean forward = (actions & FORWARD_BIT) != 0;
		boolean back = (actions & BACK_BIT) != 0;
		boolean left = (actions & LEFT_BIT) != 0;
		boolean right = (actions & RIGHT_BIT) != 0;
		boolean mouseWheelUp = (actions & MOUSEWHEEL_UP_BIT) != 0;
		boolean mouseWheelDown = (actions & MOUSEWHEEL_DOWN_BIT) != 0;
		final short mouseDx = buffer.readShort();
		final short mouseDy = buffer.readShort();
		return new PlayerInputMessage(forward, back, left, right, mouseWheelUp, mouseWheelDown, mouseDx, mouseDy);
	}
}
