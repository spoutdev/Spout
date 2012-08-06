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
package org.spout.api.protocol.builtin;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.command.Command;
import org.spout.api.entity.component.controller.type.ControllerType;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.builtin.message.CommandMessage;
import org.spout.api.protocol.builtin.message.LoginMessage;
import org.spout.api.protocol.builtin.message.StringMapMessage;
import org.spout.api.util.StringMap;

/**
 * The protocol used in SpoutClient
 */
public class SpoutProtocol extends Protocol {
	public static final int ENTITY_PROTOCOL_ID = ControllerType.getProtocolId(SpoutProtocol.class.getName());
	public static final SpoutProtocol INSTANCE = new SpoutProtocol();
	public static final DefaultedKey<Integer> PLAYER_ENTITY_ID = new DefaultedKeyImpl<Integer>("playerEntityId", -1);
	public static final int PROTOCOL_VERSION = 0;
	public static final int DEFAULT_PORT = 13756;

	public SpoutProtocol() {
		super("Spout", DEFAULT_PORT, new SpoutCodecLookupService(), new SpoutHandlerLookupService());
	}

	public MessageCodec<?> readHeader(ChannelBuffer buf) {
		int id = buf.readUnsignedShort();
		int length = buf.readInt();
		MessageCodec<?> codec = getCodecLookupService().find(id);
		if (codec == null) {
			buf.skipBytes(length);
			return null;
		} else {
			return codec;
		}
	}

	public ChannelBuffer writeHeader(MessageCodec<?> codec, ChannelBuffer data) {
		ChannelBuffer buf = ChannelBuffers.buffer(6);
		buf.writeShort(codec.getOpcode());
		buf.writeInt(data.writerIndex());
		return buf;
	}

	public Message getKickMessage(ChatArguments message) {
		Command cmd = Spout.getEngine().getRootCommand().getChild("disconnect");
		if (cmd != null) {
			return getCommandMessage(cmd, message);
		} else {
			return null;
		}
	}

	public Message getCommandMessage(Command command, ChatArguments message) {
		return new CommandMessage(command, message.getArguments());
	}

	public Message getIntroductionMessage(String playerName) {
		return new LoginMessage(playerName, PROTOCOL_VERSION);
	}

	public void initializeSession(Session session) {
		session.setNetworkSynchronizer(new SpoutNetworkSynchronizer(session));

		session.send(false, new StringMapMessage(StringMapMessage.STRINGMAP_REGISTRATION_MAP, StringMapMessage.Action.SET, StringMap.get(StringMapMessage.STRINGMAP_REGISTRATION_MAP).getItems()));
		for (StringMap map : StringMap.getAll()) {
			session.send(false, new StringMapMessage(map.getId(), StringMapMessage.Action.SET, map.getItems()));
		}
	}
}
