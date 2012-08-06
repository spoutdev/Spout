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
package org.spout.api.protocol;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.apache.commons.lang3.tuple.Pair;
import org.jboss.netty.buffer.ChannelBuffer;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.command.Command;
import org.spout.api.exception.UnknownPacketException;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.player.Player;
import org.spout.api.util.StringMap;

public abstract class Protocol {
	private static final ConcurrentHashMap<String, Protocol> map = new ConcurrentHashMap<String, Protocol>();

	private final StringMap dynamicPacketLookup;
	private final CodecLookupService codecLookup;
	private final HandlerLookupService handlerLookup;
	private final String name;
	private final int defaultPort;

	public Protocol(String name, int defaultPort, CodecLookupService codecLookup, HandlerLookupService handlerLookup) {
		this.codecLookup = codecLookup;
		this.handlerLookup = handlerLookup;
		this.defaultPort = defaultPort;
		this.name = name;
		this.dynamicPacketLookup = new StringMap(null, new MemoryStore<Integer>(), Integer.MAX_VALUE, Integer.MAX_VALUE, this.name + "ProtocolDynamicPackets");
	}

	/**
	 * Gets the handler lookup service associated with this Protocol
	 *
	 * @return the handler lookup service
	 */
	public HandlerLookupService getHandlerLookupService() {
		return handlerLookup;
	}

	/**
	 * Gets the codec lookup service associated with this Protocol
	 *
	 * @return the codec lookup service
	 */
	public CodecLookupService getCodecLookupService() {
		return codecLookup;
	}

	/**
	 * Gets the name of the Protocol
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The default port is the port used when autogenerating default bindings for this
	 * protocol and in the client when no port is given.
	 *
	 * @return The default port
	 */
	public int getDefaultPort() {
		return defaultPort;
	}

	/**
	 * Register a custom packet with this protocol
	 *
	 * @param codecClazz The packet's codec
	 * @param <T> The type of Message this codec handles
	 * @param <C> The codec's type
	 * @return The instantiated codec
	 */
	public <T extends Message, C extends MessageCodec<T>> C registerPacket(Class<C> codecClazz, MessageHandler<T> handler) {
		try {
			C codec = getCodecLookupService().bind(codecClazz, dynamicPacketLookup);
			if (handler != null) {
				getHandlerLookupService().bind(codec.getType(), handler);
			}
			return codec;
		} catch (InstantiationException e) {
			Spout.getLogger().log(Level.SEVERE, "Error registering codec " + codecClazz + ": ", e);
			return null;
		} catch (IllegalAccessException e) {
			Spout.getLogger().log(Level.SEVERE, "Error registering codec " + codecClazz + ": ", e);
			return null;
		} catch (InvocationTargetException e) {
			Spout.getLogger().log(Level.SEVERE, "Error registering codec " + codecClazz + ": ", e);
			return null;
		}
	}

	public List<Pair<Integer, String>> getDynamicallyRegisteredPackets() {
		return dynamicPacketLookup.getItems();
	}

	/**
	 * Allows applying a wrapper to messages with dynamically allocated id's, in case this protocol needs to provide special treatment for them.
	 *
	 * @param dynamicMessage The message with a dynamically-allocated codec
	 * @return The new message
	 */
	public <T extends Message> Message getWrappedMessage(boolean upstream, T dynamicMessage) throws IOException {
		return dynamicMessage;
	}

	/**
	 * Read a packet header from the buffer. If a codec is not available and packet length is known, skip ahead in the buffer and return null.
	 * If packet length is not known, throw a {@link org.spout.api.exception.UnknownPacketException}
	 *
	 * @param buf The buffer to read from
	 * @return The correct codec
	 * @throws UnknownPacketException when the opcode does not have an associated codec and the packet length is unknown
	 */
	public abstract MessageCodec<?> readHeader(ChannelBuffer buf) throws UnknownPacketException;

	/**
	 * Writes a packet header to a new buffer.
	 *
	 * @param codec The codec the message was written with
	 * @param data The data from the encoded message
	 * @return The buffer with the packet header
	 */
	public abstract ChannelBuffer writeHeader(MessageCodec<?> codec, ChannelBuffer data);

	/**
	 * Gets a packet for kicking a player
	 *
	 * @param message The kick reason
	 * @return The kick message
	 */
	public abstract Message getKickMessage(ChatArguments message);

	/**
	 * Gets a command packet for a given {@link Command} and {@link ChatArguments}
	 *
	 * @param command The command to execute
	 * @return The command packet
	 */
	public abstract Message getCommandMessage(Command command, ChatArguments arguments);

	/**
	 * Gets the introduction message that the client sends to the server on connect
	 *
	 * @param playerName the name of the player
	 * @return the message, or null if there is no message
	 */
	public abstract Message getIntroductionMessage(String playerName);

	/**
	 * Set up the initial data for the given session.
	 * This method is called in between {@link org.spout.api.event.player.PlayerLoginEvent}
	 * and {@link org.spout.api.event.player.PlayerJoinEvent}. Game plugins should have set
	 *
	 * @param session The session to set data for
	 */
	public abstract void initializeSession(Session session);
	
	/**
	 * Sets the player controller for the given entity.  The method is called while the player 
	 * is being spawned by the server in response to a {@link org.spout.api.event.player.PlayerConnectEvent}
	 * 
	 * @param player
	 */
	public abstract void setPlayerController(Player player);

	/**
	 * Registers a Protocol for a particular id value
	 *
	 * @param id the id of the protocol
	 * @param protocol the Protocol
	 */
	public static void registerProtocol(String id, Protocol protocol) {
		map.put(id, protocol);
	}

	/**
	 * Registers a Protocol under its name
	 *
	 * @param protocol the Protocol
	 */
	public static void registerProtocol(Protocol protocol) {
		map.put(protocol.getName(), protocol);
	}

	/**
	 * Gets the Protocol associated with a particular id
	 *
	 * @param id the id
	 * @return the Protocol
	 */
	public static Protocol getProtocol(String id) {
		return map.get(id);
	}

	/**
	 * Returns all protocols currently registered.
	 * The returned collection is unmodifiable.
	 *
	 * @return All registered protocols
	 */
	public static Collection<Protocol> getProtocols() {
		return Collections.unmodifiableCollection(map.values());
	}
}
