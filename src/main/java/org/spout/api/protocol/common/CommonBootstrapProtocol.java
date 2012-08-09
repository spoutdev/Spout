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
package org.spout.api.protocol.common;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.command.Command;
import org.spout.api.exception.UnknownPacketException;
import org.spout.api.entity.Player;
import org.spout.api.protocol.CodecLookupService;
import org.spout.api.protocol.HandlerLookupService;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.common.message.CustomDataMessage;

public class CommonBootstrapProtocol extends Protocol {
	private final Protocol defaultProtocol;
	private final boolean expandedOpcodes;

	public CommonBootstrapProtocol(Protocol defaultProtocol, boolean expandedOpcodes) {
		this("CommonBootstrap", defaultProtocol, expandedOpcodes);
	}


	public CommonBootstrapProtocol(String name, Protocol defaultProtocol, boolean expandedOpcodes) {
		this(name, new CommonBootstrapCodecLookupService(), new CommonBootstrapHandlerLookupService(), defaultProtocol, expandedOpcodes);
	}

	public CommonBootstrapProtocol(String name, CodecLookupService codecLookup, HandlerLookupService handlerLookup, Protocol defaultProtocol, boolean expandedOpcodes) {
		super(name, -1, codecLookup, handlerLookup);
		this.defaultProtocol = defaultProtocol;
		this.expandedOpcodes = expandedOpcodes;
	}

	private String detectProtocolDefinition(CustomDataMessage message) {
		if (message.getType().equals("AutoProto:HShake")) {
			try {
				return new String(message.getData(), "UTF8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}

	/**
	 * Reads a string from the buffer.
	 * @param buf The buffer.
	 * @param maxLength the maximum length of the string
	 * @return The string.
	 */
	public static String readString(ChannelBuffer buf, int maxLength) {
		int len = buf.readUnsignedShort();

		if (len > maxLength) {
			Spout.getEngine().getLogger().severe("Maximum string length of " + maxLength + " exceeded (" + len + ")");
			return null;
		}
		len = Math.min(maxLength, len);

		char[] characters = new char[len];
		for (int i = 0; i < len; i++) {
			characters[i] = buf.readChar();
		}

		return new String(characters);
	}

	/**
	 * Writes a string to the buffer.
	 * @param buf The buffer.
	 * @param str The string.
	 * @throws IllegalArgumentException if the string is too long
	 *                                  <em>after</em> it is encoded.
	 */
	public static void writeString(ChannelBuffer buf, String str) {
		int len = str.length();
		if (len >= 0x7FFF) {
			throw new IllegalArgumentException("String too long.");
		}

		buf.writeShort(len);
		for (int i = 0; i < len; ++i) {
			buf.writeChar(str.charAt(i));
		}
	}

	public MessageCodec<?> readHeader(ChannelBuffer buf) throws UnknownPacketException {
		int opcode = expandedOpcodes ? buf.readUnsignedShort() : buf.readUnsignedByte();

		MessageCodec<?> codec = getCodecLookupService().find(opcode);
		if (codec == null) {
			throw new UnknownPacketException(opcode);
		}
		return codec;
	}

	public ChannelBuffer writeHeader(MessageCodec<?> codec, ChannelBuffer data) {
		ChannelBuffer opcodeBuf = ChannelBuffers.buffer(expandedOpcodes? 2 : 1);
		if (expandedOpcodes) {
			opcodeBuf.writeShort(codec.getOpcode());
		} else {
			opcodeBuf.writeByte(codec.getOpcode());
		}
		return opcodeBuf;
	}

	@Override
	public Message getKickMessage(ChatArguments message) {
		return defaultProtocol == null ? null : defaultProtocol.getKickMessage(message);
	}


	@Override
	public Message getCommandMessage(Command cmd, ChatArguments message) {
		return defaultProtocol == null ? null : defaultProtocol.getCommandMessage(cmd, message);
	}

	@Override
	public Message getIntroductionMessage(String playerName) {
		return defaultProtocol == null ? null : defaultProtocol.getIntroductionMessage(playerName);
	}

	public void initializeSession(Session session) {
		if (defaultProtocol != null) {
			defaultProtocol.initializeSession(session);
		}
	}


	@Override
	public void setPlayerController(Player player) {
		if (defaultProtocol != null) {
			defaultProtocol.setPlayerController(player);
		}
	}

}
