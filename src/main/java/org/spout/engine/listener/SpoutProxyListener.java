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
package org.spout.engine.listener;

import org.spout.api.entity.Player;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.player.PlayerConnectEvent;
import org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent;

import org.spout.engine.SpoutProxy;
import org.spout.engine.protocol.SpoutServerSession;
import org.spout.engine.protocol.SpoutSession;

public class SpoutProxyListener implements Listener {
	private final SpoutProxy proxy;

	public SpoutProxyListener(SpoutProxy proxy) {
		this.proxy = proxy;
	}

	@EventHandler(order = Order.MONITOR)
	public void onPlayerConnect(PlayerConnectEvent event) {
		if (event.isCancelled()) {
			return;
		}
		proxy.addPlayer(event.getPlayerName(), (SpoutServerSession<?>) event.getSession(), event.getViewDistance());
		//Create the player
		proxy.connect(event.getPlayerName(), event.getSession());
	}

	@EventHandler(order = Order.EARLIEST)
	public void onGetAllWithNode(PermissionGetAllWithNodeEvent event) {
		for (Player player : proxy.getOnlinePlayers()) {
			event.getReceivers().put(player, Result.DEFAULT);
		}
		event.getReceivers().put(proxy.getCommandSource(), Result.DEFAULT);
	}
}
