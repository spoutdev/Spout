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
package org.spout.engine.protocol.builtin;

import java.net.InetSocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.command.Command;
import org.spout.api.component.entity.NetworkComponent;
import org.spout.api.command.CommandArguments;
import org.spout.api.event.object.EventableListener;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.protocol.ClientSession;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.ServerSession;
import org.spout.api.protocol.Session;
import org.spout.api.util.SyncedMapEvent;
import org.spout.api.util.SyncedMapRegistry;
import org.spout.api.util.SyncedStringMap;
import org.spout.engine.protocol.builtin.message.CommandMessage;
import org.spout.engine.protocol.builtin.message.LoginMessage;
import org.spout.engine.protocol.builtin.message.SyncedMapMessage;

/**
 * The protocol used in SpoutClient
 */
public class SpoutProtocol extends Protocol {
	public static final int ENTITY_PROTOCOL_ID = NetworkComponent.getProtocolId(SpoutProtocol.class.getName());
	public static final SpoutProtocol INSTANCE = new SpoutProtocol();
	public static final DefaultedKey<Integer> PLAYER_ENTITY_ID = new DefaultedKeyImpl<Integer>("playerEntityId", -1);
	public static final int PROTOCOL_VERSION = 0;
	public static final int DEFAULT_PORT = 13756;

	public SpoutProtocol() {
		super("Spout", DEFAULT_PORT, new SpoutCodecLookupService(), new SpoutHandlerLookupService());
	}

	@Override
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

	@Override
	public ChannelBuffer writeHeader(MessageCodec<?> codec, ChannelBuffer data) {
		ChannelBuffer buf = ChannelBuffers.buffer(6);
		buf.writeShort(codec.getOpcode());
		buf.writeInt(data.writerIndex());
		return buf;
	}

	@Override
	public Message getKickMessage(String message) {
		Command cmd = Spout.getCommandManager().getCommand("disconnect", false);
		if (cmd != null) {
			return getCommandMessage(cmd, new CommandArguments(message.split(" ")));
		}
		return null;
	}

	@Override
	public Message getCommandMessage(Command command, CommandArguments args) {
		return new CommandMessage(command, args.toArray());
	}

	@Override
	public Message getIntroductionMessage(String playerName, InetSocketAddress addr) {
		return new LoginMessage(playerName, PROTOCOL_VERSION);
	}

	@Override
	public void initializeServerSession(final ServerSession session) {
		session.setNetworkSynchronizer(new SpoutServerNetworkSynchronizer(session));
		//TODO Ensure this is right, very important
		SyncedMapRegistry.getRegistrationMap().registerListener(new EventableListener<SyncedMapEvent>() {
			@Override
			public void onEvent(SyncedMapEvent event) {
				session.send(new SyncedMapMessage(event.getAssociatedObject().getId(), SyncedMapEvent.Action.ADD, event.getModifiedElements()));
			}
		});
		session.send(new SyncedMapMessage(SyncedMapRegistry.REGISTRATION_MAP, SyncedMapEvent.Action.SET, SyncedMapRegistry.getRegistrationMap().getItems()));
		for (SyncedStringMap map : SyncedMapRegistry.getAll()) {
			session.send(new SyncedMapMessage(map.getId(), SyncedMapEvent.Action.SET, map.getItems()));
		}
	}

	@Override
	public void initializeClientSession(final ClientSession session) {
		session.setNetworkSynchronizer(new SpoutClientNetworkSynchronizer((Session) session));
	}
}
