/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event.server;

import java.net.InetAddress;
import org.getspout.api.event.HandlerList;

/**
 * Called when the server is pinged in a client's server list.
 */
public class ServerListPingEvent {
	private static final HandlerList handlers = new HandlerList();

	private InetAddress address;

	private String motd;

	private int numPlayers;

	private int maxPlayers;

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
