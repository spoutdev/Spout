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
package org.spout.api.component.components;

import java.util.Set;

import org.spout.api.entity.Player;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.EntityProtocolStore;
import org.spout.api.protocol.event.ProtocolEvent;
import org.spout.api.util.StringMap;

public class NetworkComponent extends EntityComponent {
	public static final int UNREGISTERED_ID = -1;
	private static final StringMap protocolMap = new StringMap(null, new MemoryStore<Integer>(), 0, 256, "componentProtocols");
	private final EntityProtocolStore protocolStore = new EntityProtocolStore();

	public NetworkComponent() {
	}

	@Override
	public boolean isDetachable() {
		return false;
	}

	/**
	 * Returns the {@link EntityProtocol} for the given protocol id for this type of entity
	 * @param protocolId The protocol id (retrieved using {@link #getProtocolId(String)})
	 * @return The entity protocol for the specified id.
	 */
	public EntityProtocol getEntityProtocol(int protocolId) {
		return protocolStore.getEntityProtocol(protocolId);
	}

	/**
	 * Registers {@code protocol} with this ControllerType's EntityProtocolStore
	 * @param protocolId The protocol id (retrieved using {@link #getProtocolId(String)})
	 * @param protocol The protocol to set
	 */
	public void setEntityProtocol(int protocolId, EntityProtocol protocol) {
		protocolStore.setEntityProtocol(protocolId, protocol);
	}

	/**
	 * @param protocolName The name of the protocol class to get an id for
	 * @return The id for the specified protocol class
	 */
	public static int getProtocolId(String protocolName) {
		return protocolMap.register(protocolName);
	}

	/**
	 * Sends a protocol event to specific players.
	 * @param event to send
	 */
	public void callProtocolEvent(ProtocolEvent event, Player... players) {
		for (Player player : players) {
			player.getNetworkSynchronizer().callProtocolEvent(event);
		}
	}

	/**
	 * Sends a protocol event to players observing this holder
	 * @param event to send
	 * @param ignoreHolder If true, the holder will be excluded from being sent the protocol event (only valid if the holder has a NetworkSynchronier i.e. Player)
	 */
	public void callProtocolEvent(ProtocolEvent event, boolean ignoreHolder) {
		Set<? extends Player> players = getOwner().getChunk().getObservingPlayers();
		if (getOwner() instanceof Player && ignoreHolder) {
			if (players.contains(getOwner())) {
				players.remove(getOwner());
			}
		}
		Player[] thePlayers = players.toArray(new Player[players.size()]);
		callProtocolEvent(event, thePlayers);
	}

	/**
	 * Sends a protocol event to players observing this holder and the holder itself
	 * @param event to send
	 */
	public void callProtocolEvent(ProtocolEvent event) {
		callProtocolEvent(event, false);
	}
}
