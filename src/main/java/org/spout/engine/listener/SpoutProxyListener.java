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
package org.spout.engine.listener;

import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.player.PlayerConnectEvent;
import org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent;
import org.spout.api.player.Player;
import org.spout.engine.SpoutProxy;
import org.spout.engine.protocol.SpoutSession;

public class SpoutProxyListener implements Listener {
	private final SpoutProxy server;

	public SpoutProxyListener(SpoutProxy server) {
		this.server = server;
	}

	@EventHandler(order = Order.MONITOR)
	public void onPlayerConnect(PlayerConnectEvent event) {
		if (event.isCancelled()) {
			return;
		}
		final Player player = server.addPlayer(event.getPlayerName(), (SpoutSession) event.getSession(), event.getViewDistance());
		//Create the player
		server.connect(event.getPlayerName(), event.getSession());
	}

	@EventHandler(order = Order.EARLIEST)
	public void onGetAllWithNode(PermissionGetAllWithNodeEvent event) {
		for (Player player : server.getOnlinePlayers()) {
			event.getReceivers().put(player, Result.DEFAULT);
		}
		event.getReceivers().put(server.getConsole(), Result.DEFAULT);
	}

}
