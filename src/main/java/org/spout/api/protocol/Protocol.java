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

import java.util.concurrent.ConcurrentHashMap;

public abstract class Protocol {
	private static final ConcurrentHashMap<String, Protocol> map = new ConcurrentHashMap<String, Protocol>();

	private final CodecLookupService codecLookup;
	private final HandlerLookupService handlerLookup;
	private final String name;

	public Protocol(String name, CodecLookupService codecLookup, HandlerLookupService handlerLookup) {
		this.codecLookup = codecLookup;
		this.handlerLookup = handlerLookup;
		this.name = name;
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
	 * Gets a message for kicking a player
	 *
	 * @param message
	 * @return
	 */
	public abstract Message getKickMessage(Object... message);

	/**
	 * Gets a chat message for a given string
	 *
	 * @param message
	 * @return
	 */
	public abstract Message getChatMessage(Object... message);

	/**
	 * Gets the introduction message that the client sends to the server on connect
	 *
	 * @param playerName the name of the player
	 * @return the message, or null if there is no message
	 */
	public abstract Message getIntroductionMessage(String playerName);

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
	 * Gets the Protocol associated with a particular id
	 *
	 * @param id the id
	 * @return the Protocol
	 */
	public static Protocol getProtocol(String id) {
		return map.get(id);
	}
}
