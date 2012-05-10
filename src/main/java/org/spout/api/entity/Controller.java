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

import java.io.Serializable;
import java.util.Map;

import org.spout.api.entity.component.EntityComponent;
import org.spout.api.entity.type.ControllerType;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.inventory.Inventory;

public abstract class Controller extends EntityComponent{
	private final ControllerType type;

	protected Controller(ControllerType type) {
		this.type = type;
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

	public void onCollide(Block other) {

	}

	public ControllerType getType() {
		return type;
	}
	
	/**
	 * Called when the entity information is being saved. 
	 * 
	 * @param map to put information
	 * @return true if information was saved, false if none
	 */
	public boolean save(Map<String, Serializable> map) {
		return false;
	}

	/**
	 * Called when the entity information is being read from files. 
	 * 
	 * @param map to read information from
	 * @return true if information was read, false if not
	 */
	public boolean read(Map<String, Serializable> map) {
		return false;
	}
}
