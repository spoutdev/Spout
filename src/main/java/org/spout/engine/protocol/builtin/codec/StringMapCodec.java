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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.MessageCodec;
import org.spout.engine.protocol.builtin.ChannelBufferUtils;
import org.spout.engine.protocol.builtin.message.StringMapMessage;

public class StringMapCodec extends MessageCodec<StringMapMessage> {
	public StringMapCodec() {
		super(StringMapMessage.class, 0x01);
	}

	@Override
	public ChannelBuffer encode(StringMapMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		buffer.writeInt(message.getMap());
		buffer.writeByte(message.getAction().ordinal());
		buffer.writeInt(message.getElements().size());
		for (Pair<Integer, String> el : message.getElements()) {
			buffer.writeInt(el.getKey());
			ChannelBufferUtils.writeString(buffer, el.getValue());
		}
		return buffer;
	}

	@Override
	public StringMapMessage decode(ChannelBuffer buffer) {
		final int map = buffer.readInt();
		final byte action = buffer.readByte();
		final int elementCount = buffer.readInt();
		List<Pair<Integer, String>> elements = new ArrayList<Pair<Integer, String>>(elementCount);
		for (int i = 0; i < elementCount; ++i) {
			final int key = buffer.readInt();
			final String value = ChannelBufferUtils.readString(buffer);
			elements.add(new ImmutablePair<Integer, String>(key, value));
		}
		return new StringMapMessage(map, action, elements);
	}
}
