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
package org.spout.api.protocol.builtin.handler;

import org.spout.api.event.player.ClientPlayerConnectedEvent;
import org.spout.api.event.player.PlayerConnectEvent;
import org.spout.api.player.Player;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.builtin.SpoutProtocol;
import org.spout.api.protocol.builtin.message.LoginMessage;

public class LoginMessageHandler extends MessageHandler<LoginMessage> {

	@Override
	public void handleServer(Session session, Player player, LoginMessage message) {
		session.getEngine().getEventManager().callEvent(new PlayerConnectEvent(session, message.getPlayerName()));
		session.setState(Session.State.GAME);
		session.send(false, new LoginMessage("", session.getPlayer().getEntity().getId()));
	}

	@Override
	public void handleClient(Session session, Player player, LoginMessage message) {
		session.getDataMap().put(SpoutProtocol.PLAYER_ENTITY_ID, message.getProtocolVersion());
		session.setState(Session.State.GAME);
		session.getEngine().getEventManager().callEvent(new ClientPlayerConnectedEvent(session, message.getProtocolVersion()));
	}
}
