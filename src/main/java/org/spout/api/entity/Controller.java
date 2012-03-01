/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.entity;

import org.spout.api.inventory.Inventory;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.EntityProtocolStore;
import org.spout.api.util.StringMap;

public abstract class Controller {

	private static final StringMap protocolMap = new StringMap(null, new MemoryStore<Integer>(), 0, 256);

	private static final EntityProtocolStore entityProtocolStore = new EntityProtocolStore();

	public EntityProtocol getEntityProtocol(int protocolId) {
		return entityProtocolStore.getEntityProtocol(protocolId);
	}

	public static void setEntityProtocol(int protocolId, EntityProtocol protocol) {
		entityProtocolStore.setEntityProtocol(protocolId, protocol);
	}

	public static int getProtocolId(String protocolName) {
		return protocolMap.register(protocolName);
	}

	protected Entity parent;

	public void attachToEntity(Entity e) {
		parent = e;
	}

	public abstract void onAttached();

	/**
	 * Called when the entity dies.
	 *
	 * Called just before the preSnapshot method.
	 */
	public void onDeath() {
	}

	/**
	 *
	 * @param dt the number of seconds since last update
	 */
	public abstract void onTick(float dt);

	/**
	 * Called when this controller is being synced with the client.
	 */
	public void onSync() {

	}

	/**
	 * Called just before a snapshot update.
	 *
	 * This is intended purely as a monitor based step.
	 *
	 * NO updates should be made to the entity at this stage.
	 *
	 * It can be used to send packets for network update.
	 */
	public void preSnapshot() {
	}

	/**
	 * Called just before the pre-snapshot stage.<br>
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
	 */
	public void finalizeTick() {
	}

	public Inventory createInventory(int size) {
		return new Inventory(size);
	}
}
