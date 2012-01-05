/*
 * This file is part of Spout (http://www.getspout.org/).
 *
 * The Spout is licensed under the SpoutDev license version 1.
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
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.getspout.server;

import org.getspout.api.Spout;
import org.getspout.api.event.EventHandler;
import org.getspout.api.event.Listener;
import org.getspout.api.event.Order;
import org.getspout.api.event.player.PlayerConnectEvent;
import org.getspout.api.event.player.PlayerJoinEvent;
import org.getspout.api.player.Player;
import org.getspout.server.net.SpoutSession;
import org.getspout.server.player.SpoutPlayer;

public class InternalEventListener implements Listener {
	private final SpoutServer server;

	public InternalEventListener(SpoutServer server) {
		this.server = server;
	}

	@EventHandler(event = PlayerConnectEvent.class, order = Order.MONITOR)
	public void onPlayerConnect(PlayerConnectEvent event) {
		if(event.isCancelled()) return;
		//Create the player
		final Player player = server.addPlayer(event.getPlayerName(), (SpoutSession)event.getSession());

		if (player != null) {
			Spout.getGame().getEventManager().callDelayedEvent(new PlayerJoinEvent(player));
		} else {
			event.getSession().disconnect("Player is already online");
		}
	}

	@EventHandler(event = PlayerJoinEvent.class, order = Order.LATEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		SpoutPlayer p = (SpoutPlayer)event.getPlayer();

		if(server.rawGetAllOnlinePlayers().size() >= server.getMaxPlayers()) {
			//TODO Kick with message
			p.disconnect();
		}

		server.broadcastMessage(p.getName() + " has Connected");
	}

}
