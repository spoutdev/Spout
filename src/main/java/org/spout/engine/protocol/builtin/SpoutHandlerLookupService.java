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
package org.spout.engine.protocol.builtin;

import org.spout.api.protocol.HandlerLookupService;
import org.spout.engine.protocol.builtin.message.AddEntityMessage;
import org.spout.engine.protocol.builtin.message.BlockUpdateMessage;
import org.spout.engine.protocol.builtin.message.ChunkDataMessage;
import org.spout.engine.protocol.builtin.message.ClickMessage;
import org.spout.engine.protocol.builtin.message.CommandMessage;
import org.spout.engine.protocol.builtin.message.CuboidBlockUpdateMessage;
import org.spout.engine.protocol.builtin.message.EntityDatatableMessage;
import org.spout.engine.protocol.builtin.message.EntityPositionMessage;
import org.spout.engine.protocol.builtin.message.LoginMessage;
import org.spout.engine.protocol.builtin.message.PlayerInputMessage;
import org.spout.engine.protocol.builtin.message.RemoveEntityMessage;
import org.spout.engine.protocol.builtin.message.StringMapMessage;
import org.spout.engine.protocol.builtin.message.WorldChangeMessage;
import org.spout.engine.protocol.builtin.handler.AddEntityMessageHandler;
import org.spout.engine.protocol.builtin.handler.BlockUpdateMessageHandler;
import org.spout.engine.protocol.builtin.handler.ChunkDataMessageHandler;
import org.spout.engine.protocol.builtin.handler.ClickMessageHandler;
import org.spout.engine.protocol.builtin.handler.CommandMessageHandler;
import org.spout.engine.protocol.builtin.handler.CuboidBlockUpdateMessageHandler;
import org.spout.engine.protocol.builtin.handler.EntityDatatableMessageHandler;
import org.spout.engine.protocol.builtin.handler.EntityPositionMessageHandler;
import org.spout.engine.protocol.builtin.handler.LoginMessageHandler;
import org.spout.engine.protocol.builtin.handler.PlayerInputMessageHandler;
import org.spout.engine.protocol.builtin.handler.RemoveEntityMessageHandler;
import org.spout.engine.protocol.builtin.handler.StringMapMessageHandler;
import org.spout.engine.protocol.builtin.handler.WorldChangeMessageHandler;

public class SpoutHandlerLookupService extends HandlerLookupService {
	public SpoutHandlerLookupService() {
		try {
			bind(LoginMessage.class, LoginMessageHandler.class);
			bind(StringMapMessage.class, StringMapMessageHandler.class);
			bind(WorldChangeMessage.class, WorldChangeMessageHandler.class);
			bind(CommandMessage.class, CommandMessageHandler.class);
			bind(AddEntityMessage.class, AddEntityMessageHandler.class);
			bind(RemoveEntityMessage.class, RemoveEntityMessageHandler.class);
			bind(EntityDatatableMessage.class, EntityDatatableMessageHandler.class);
			bind(EntityPositionMessage.class, EntityPositionMessageHandler.class);
			bind(ChunkDataMessage.class, ChunkDataMessageHandler.class);
			bind(BlockUpdateMessage.class, BlockUpdateMessageHandler.class);
			bind(CuboidBlockUpdateMessage.class, CuboidBlockUpdateMessageHandler.class);
			bind(ClickMessage.class, ClickMessageHandler.class);
			bind(PlayerInputMessage.class, PlayerInputMessageHandler.class);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
