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

import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.EntityProtocolStore;
import org.spout.api.util.StringMap;

public class NetworkComponent extends EntityComponent {

	public static final int UNREGISTERED_ID = -1;
	private static final StringMap protocolMap = new StringMap(null, new MemoryStore<Integer>(), 0, 256, "componentProtocols");

	private int id = UNREGISTERED_ID;
	private final EntityProtocolStore protocolStore = new EntityProtocolStore();

	public NetworkComponent() {
	}

	/**
	 * @return id of the entity.
	 */
	public int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
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
}
