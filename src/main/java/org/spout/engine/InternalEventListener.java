/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
package org.spout.engine;

import org.spout.api.ChatColor;
import org.spout.api.Spout;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.player.PlayerBanKickEvent;
import org.spout.api.event.player.PlayerConnectEvent;
import org.spout.api.event.player.PlayerJoinEvent;
import org.spout.api.event.player.PlayerLoginEvent;
import org.spout.api.event.server.BanChangeEvent.BanType;
import org.spout.api.event.storage.PlayerLoadEvent;
import org.spout.api.player.Player;
import org.spout.engine.net.SpoutSession;
import org.spout.engine.player.SpoutPlayer;

public class InternalEventListener implements Listener {
	private final SpoutServer server;

	public InternalEventListener(SpoutServer server) {
		this.server = server;
	}

	@EventHandler(order = Order.MONITOR)
	public void onPlayerConnect(PlayerConnectEvent event) {
		if (event.isCancelled()) {
			return;
		}
		//Create the player
		final Player player = server.addPlayer(event.getPlayerName(), (SpoutSession) event.getSession());

		if (player != null) {
			PlayerLoadEvent loadEvent = Spout.getEngine().getEventManager().callEvent(new PlayerLoadEvent(player));
			if (!loadEvent.isLoaded()) {

			}
			PlayerLoginEvent loginEvent = Spout.getEngine().getEventManager().callEvent(new PlayerLoginEvent(player));
			if (!loginEvent.isAllowed()) {
				if (loginEvent.getMessage() != null) {
					player.kick(loginEvent.getMessage(), false);
				} else {
					player.kick();
				}
			} else {
				Spout.getEngine().getEventManager().callDelayedEvent(new PlayerJoinEvent(player, ChatColor.CYAN + player.getDisplayName() + ChatColor.CYAN + " has joined the game"));
			}
		} else {
			event.getSession().disconnect("Player is already online", true);
		}
	}

	@EventHandler(order = Order.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		SpoutPlayer p = (SpoutPlayer) event.getPlayer();

		PlayerBanKickEvent banEvent = null;
		
		if(server.isPlayerBanned(p.getName())) {
			banEvent = server.getEventManager().callEvent(new PlayerBanKickEvent(p, BanType.PLAYER, server.getBanMessage(p.getName())));
		}
		
		if(server.isIpBanned(p.getAddress().getHostAddress())) {
			banEvent = server.getEventManager().callEvent(new PlayerBanKickEvent(p, BanType.IP, server.getBanMessage(p.getAddress().getHostAddress())));
		}
		
		if(banEvent != null && !banEvent.isCancelled()) {
			p.kick(!banEvent.getMessage().equals("") ? banEvent.getMessage() : (banEvent.getBanType() == BanType.PLAYER) ? server.getBanMessage(p.getName()) : server.getIpBanMessage(p.getAddress().getHostAddress()), false);
			return;
		}
		
		if (server.rawGetAllOnlinePlayers().size() >= server.getMaxPlayers()) {
			p.kick("Server is full!", false);
			return;
		}

		if (event.getMessage() != null) {
			server.broadcastMessage(event.getMessage());
		}
	}

}
