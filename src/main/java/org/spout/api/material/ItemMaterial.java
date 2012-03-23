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
package org.spout.api.material;

import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.block.BlockFace;

public abstract class ItemMaterial extends Material {

	public ItemMaterial(String name, int type, int data, Material parent) {
		super(name, type, data, parent);
	}

	public ItemMaterial(String name, int type) {
		super(name, type);
	}

	/**
	 * Gets the item at the given id, or null if none found
	 * 
	 * @param id to get
	 * @return item or null if none found
	 */
	public static ItemMaterial get(short id) {
		Material mat = Material.get(id);
		if (mat instanceof ItemMaterial) {
			return (ItemMaterial) mat;
		} else {
			return null;
		}
	}

	/**
	 * Gets the associated item material with it's name. Case-insensitive.
	 * 
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static ItemMaterial get(String name) {
		Material mat = Material.get(name);
		if (mat instanceof ItemMaterial) {
			return (ItemMaterial) mat;
		} else {
			return null;
		}
	}

	/**
	 * Fired when this item is being rendered in the inventory
	 * 
	 */
	public abstract void onInventoryRender();

	/**
	 * Fired when an entity interacts with the world
	 * 
	 * @param entity that is interacting with the world
	 * @param position of the interaction
	 * @param type of interaction
	 */
	public abstract void onInteract(Entity entity, Point position, Action type, BlockFace clickedFace);

	/**
	 * Fired when an entity interacts with another entity
	 * 
	 * @param entity that is interacting with the world
	 * @param other entity that was interacted with
	 */
	public abstract void onInteract(Entity entity, Entity other);
}
