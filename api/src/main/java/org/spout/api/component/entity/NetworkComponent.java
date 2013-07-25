/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.api.component.entity;

import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.EntityProtocolStore;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.event.ProtocolEvent;
import org.spout.api.util.StringToUniqueIntegerMap;

public class NetworkComponent extends EntityComponent {
	private static final StringToUniqueIntegerMap protocolMap = new StringToUniqueIntegerMap(null, new MemoryStore<Integer>(), 0, 256, "componentProtocols");
	private final EntityProtocolStore protocolStore = new EntityProtocolStore();

	public NetworkComponent() {
	}

	@Override
	public boolean isDetachable() {
		return false;
	}

	/**
	 * Returns the {@link EntityProtocol} for the given protocol id for this type of entity
	 *
	 * @param protocolId The protocol id (retrieved using {@link #getProtocolId(String)})
	 * @return The entity protocol for the specified id.
	 */
	public EntityProtocol getEntityProtocol(int protocolId) {
		return protocolStore.getEntityProtocol(protocolId);
	}

	/**
	 * Registers {@code protocol} with this ControllerType's EntityProtocolStore
	 *
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
	 *
	 * @param event to send
	 */
	public void callProtocolEvent(ProtocolEvent event, Player... players) {
		for (Player player : players) {
			NetworkSynchronizer sync = player.getNetworkSynchronizer();
			if (sync != null) {
				sync.callProtocolEvent(event);
			}
		}
	}

	/**
	 * Sends a protocol event to players observing this holder
	 *
	 * @param event to send
	 * @param ignoreHolder If true, the holder will be excluded from being sent the protocol event (only valid if the holder has a NetworkSynchronier i.e. Player)
	 */
	public void callProtocolEvent(ProtocolEvent event, boolean ignoreHolder) {
		try {
			Set<? extends Player> players = getOwner().getChunk().getObservingPlayers();
			Player[] thePlayers;
			if (getOwner() instanceof Player && ignoreHolder && players.contains(getOwner())) {
				thePlayers = new Player[players.size() - 1];
			} else {
				thePlayers = new Player[players.size()];
			}
			int index = 0;
			for (Player p : players) {
				if (!ignoreHolder || getOwner() != p) {
					thePlayers[index++] = p;
				}
			}
			callProtocolEvent(event, thePlayers);
		} catch (NullPointerException npe) {
			//NPE logging to diagnose VANILLA-338
			Spout.getLogger().info("Exception handling protocol event: " + npe.getClass().getSimpleName() + "\n" +
					"    Owner: " + getOwner() + "\n" +
					"    Chunk: " + (getOwner() != null ? getOwner().getChunk() : null) + "\n" +
					"    Position: " + (getOwner() != null ? getOwner().getPhysics().getPosition() : null) + "\n" +
					"    Is Owner Alive: " + (getOwner() != null ? getOwner().isRemoved() : "owner is null") + "\n");
			npe.printStackTrace();
		}
	}

	/**
	 * Sends a protocol event to players observing this holder and the holder itself
	 *
	 * @param event to send
	 */
	public void callProtocolEvent(ProtocolEvent event) {
		callProtocolEvent(event, false);
	}
}
