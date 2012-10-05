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

import java.net.InetAddress;

import org.spout.api.chat.style.ChatStyle;
import org.spout.api.entity.Player;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.entity.EntityChangeWorldEvent;
import org.spout.api.event.player.PlayerBanKickEvent;
import org.spout.api.event.player.PlayerConnectEvent;
import org.spout.api.event.player.PlayerJoinEvent;
import org.spout.api.event.player.PlayerLoginEvent;
import org.spout.api.event.player.PlayerWhitelistKickEvent;
import org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent;
import org.spout.api.event.storage.PlayerLoadEvent;
import org.spout.api.util.access.BanType;
import org.spout.engine.SpoutServer;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.world.SpoutWorld;

public class SpoutServerListener implements Listener {
	private final SpoutServer server;

	public SpoutServerListener(SpoutServer server) {
		this.server = server;
	}

	@EventHandler(order = Order.MONITOR)
	public void onPlayerConnect(PlayerConnectEvent event) {
		if (event.isCancelled()) {
			return;
		}
		//Create the player
		final SpoutPlayer player = (SpoutPlayer) server.addPlayer(event.getPlayerName(), (SpoutSession<?>) event.getSession(), event.getViewDistance());

		if (player != null) {
			//load players
			server.getEventManager().callEvent(new PlayerLoadEvent(player));
			//Call PlayerJoinEvent
			PlayerLoginEvent loginEvent = server.getEventManager().callEvent(new PlayerLoginEvent(player));
			if (!loginEvent.isAllowed()) {
				if (loginEvent.getMessage() != null) {
					player.kick(loginEvent.getMessage());
				} else {
					player.kick();
				}
			} else {
				server.getEventManager().callEvent(new PlayerJoinEvent(player, ChatStyle.CYAN, player.getDisplayName(), ChatStyle.CYAN, " has joined the game"));
			}
		} else {
			event.getSession().disconnect("Player is already online");
		}
	}

	@EventHandler(order = Order.EARLIEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		PlayerBanKickEvent banEvent = null;
		PlayerWhitelistKickEvent whitelistEvent = null;
		InetAddress address = p.getAddress();
		if (address == null) {
			event.disallow("Invalid IP Address!");
		} else if (server.getAccessManager().isBanned(BanType.PLAYER, p.getName())) {
			banEvent = server.getEventManager().callEvent(new PlayerBanKickEvent(p, BanType.PLAYER, server.getAccessManager().getBanMessage(BanType.PLAYER)));
		} else if (server.getAccessManager().isBanned(BanType.IP, address.getHostAddress())) {
			banEvent = server.getEventManager().callEvent(new PlayerBanKickEvent(p, BanType.IP, server.getAccessManager().getBanMessage(BanType.IP)));
		} else if (server.getAccessManager().isWhitelistEnabled() && !server.getAccessManager().isWhitelisted(p.getName())) {
			whitelistEvent = server.getEventManager().callEvent(new PlayerWhitelistKickEvent(p, server.getAccessManager().getWhitelistMessage()));
		}

		if (banEvent != null && !banEvent.isCancelled()) {
			event.disallow(!banEvent.getMessage().getPlainString().equals("") ? banEvent.getMessage() : server.getAccessManager().getBanMessage(banEvent.getBanType()));
			return;
		}

		if (whitelistEvent != null && !whitelistEvent.isCancelled()) {
			event.disallow(!whitelistEvent.getMessage().getPlainString().equals("") ? whitelistEvent.getMessage() : server.getAccessManager().getWhitelistMessage());
			return;
		}

		if (server.rawGetAllOnlinePlayers().size() >= server.getMaxPlayers()) {
			event.disallow("Server is full!");
		}
	}

	@EventHandler(order = Order.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getMessage() != null) {
			server.broadcastMessage(event.getMessage());
		}
	}

	@EventHandler(order = Order.EARLIEST)
	public void onGetAllWithNode(PermissionGetAllWithNodeEvent event) {
		for (Player player : server.getOnlinePlayers()) {
			event.getReceivers().put(player, Result.DEFAULT);
		}
		event.getReceivers().put(server.getCommandSource(), Result.DEFAULT);
	}

	@EventHandler(order = Order.EARLIEST)
	public void onEntityChangeWorld(EntityChangeWorldEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		if (event.getPrevious().equals(event.getTarget())) {
			return;
		}
		((SpoutWorld) event.getPrevious()).removePlayer((Player) event.getEntity());
		((SpoutWorld) event.getTarget()).addPlayer((Player) event.getEntity());
	}
}
