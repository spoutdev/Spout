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
import org.spout.api.material.Material;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.EntityProtocolStore;
import org.spout.api.util.StringMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Controller implements EntityComponent {
	private static final EntityProtocolStore entityProtocolStore = new EntityProtocolStore();
	private static final StringMap protocolMap = new StringMap(null, new MemoryStore<Integer>(), 0, 256);
	private Entity parent;


	
	/**
	 * Called when this controller is attached to an entity.
	 * @param e entity this controller will be attached to.
	 */
	public void attachToEntity(Entity e) {
		this.parent = e;
	}

	/**
	 * Called when this controller is detached from the entity (normally due to the entity dieing or being removed from the world).
	 * Occurs before the Pre-Snapshot of the tick.
	 */
	public void onDeath() {
	}

	/**
	 * Called when this controller is being synced with the client. Occurs before Pre-Snapshot of the tick.
	 */
	public void onSync() {
	}

	/**
	 * TODO: This method needs to be moved to a more appropriate place.
	 */
	public Inventory createInventory(int size) {
		return new Inventory(size);
	}

	/**
	 * Gets the parent Entity associated with this controller.
	 *
	 * @return parent Entity
	 */
	public Entity getParent() {
		return parent;
	}

	public EntityProtocol getEntityProtocol(int protocolId) {
		return entityProtocolStore.getEntityProtocol(this.getClass(), protocolId);
	}

	/**
	 *
	 * @param controller
	 * @param protocolId
	 * @param protocol
	 */
	public static void setEntityProtocol(Class<? extends Controller> controller, int protocolId, EntityProtocol protocol) {
		entityProtocolStore.setEntityProtocol(controller, protocolId, protocol);
	}

	/**
	 *
	 * @param protocolName
	 * @return
	 */
	public static int getProtocolId(String protocolName) {
		return protocolMap.register(protocolName);
	}

	/**
	 * TODO: These methods should be given the appropriate annotation that makes it clear they shouldn't be used by plugins.
	 */
	/**
	 * Called just before a snapshot update. This is intended purely as a monitor based step.
	 * NO updates should be made to the entity at this stage. It can be used to send packets for network update.
	 */
	public void preSnapshot() {
	}

	/**
	 * Called just before the pre-snapshot stage.
	 * This stage can make changes but they should be checked to make sure they
	 * are non-conflicting.
	 */
	public void finalizeTick() {
	}
	
	public void onCollide(Entity other) {
		
	}
	
	public void onCollide(Material other){
		
	}
	
	
}
