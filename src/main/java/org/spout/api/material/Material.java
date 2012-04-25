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

import java.util.HashMap;
import java.util.Map;

import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.GenericMaterialData;
import org.spout.api.material.source.MaterialData;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.model.Model;

public abstract class Material extends MaterialRegistry implements MaterialSource {
	private final short id;
	private final short data;
	private final String name;
	private final boolean isSubMaterial;
	private final Material parent;
	private Model model;
	private String displayName;
	private int maxStackSize = 64;
	private Map<Short, Material> submaterials = null;

	public Material(String name, int typeId) {
		this.isSubMaterial = false;
		this.id = (short) typeId;
		this.displayName = name;
		this.name = name.replace(' ', '_');
		this.parent = this;
		this.data = 0;
	}

	public Material(String name, int typeId, int data, Material parent) {
		this.isSubMaterial = true;
		this.id = (short) typeId;
		this.displayName = name;
		this.name = name.replace(' ', '_');
		this.parent = parent;
		this.data = (short) data;
	}

	public short getId() {
		return this.id;
	}

	/**
	 * Gets the data value associated with this material. Will return 0 if this
	 * material does not have or is not a sub material.
	 * 
	 * @return data value
	 */
	public short getData() {
		return this.data;
	}

	/**
	 * Checks if this material is a sub material or not
	 * 
	 * @return true if it is a sub material
	 */
	public boolean isSubMaterial() {
		return isSubMaterial;
	}

	/**
	 * Checks if this material has other materials mapped by data
	 * 
	 * @return true if this material has sub materials
	 */
	public boolean hasSubMaterials() {
		return this.submaterials != null;
	}

	/**
	 * Gets all sub materials of this material
	 * 
	 * @return an array of sub materials
	 */
	public Material[] getSubMaterials() {
		if (this.hasSubMaterials()) {
			return this.submaterials.values().toArray(new Material[0]);
		} else {
			return new Material[0];
		}
	}

	/**
	 * Recursively gets the sub material mapped to the data value specified
	 * 
	 * @param data to search for
	 * @return the sub material, or this material if not found
	 */
	public Material getSubMaterial(short data) {
		if (this.hasSubMaterials()) {
			Material material = this.submaterials.get(data);
		    if (material == null) {
		    	return this;
		    } else {
		    	return material.getSubMaterial(data);
		    }
		} else {
			return this;
		}
	}
	
	@Override
	public Material getSubMaterial() {
		return this; //no way around it unfortunately
	}

	/**
	 * Registers the sub material for this material
	 * 
	 * @param material to register
	 */
	public void registerSubMaterial(Material material) {
		if (material.isSubMaterial) {
			if (material.getParentMaterial() == this) {
				if (this.submaterials == null) {
					this.submaterials = new HashMap<Short, Material>();
				}
				this.submaterials.put(material.getData(), material);
			} else {
				throw new IllegalArgumentException("Sub Material is registered to a material different than the parent!");
			}
		} else {
			throw new IllegalArgumentException("Material is not a valid sub material!");
		}
	}
	
	@Override
	public MaterialData createData() {
		return this.createData(this.getData());
	}
	
	/**
	 * Constructs a new material data using the data specified
	 * 
	 * @param data the data to use during construction
	 * @return a new {@link MaterialData} instance
	 */
	public MaterialData createData(short data) {
		return new GenericMaterialData(this, data);
	}

	@Override
	public Material getMaterial() {
		return this;
	}

	/**
	 * Gets the parent of this sub material
	 * 
	 * @return the material of the parent
	 */
	public Material getParentMaterial() {
		return this.parent;
	}
	
	/**
	 * Gets the root parent of this sub materia;
	 * 
	 * @return the material root
	 */
	public Material getRoot() {
		return this.parent == this ? this : this.parent.getRoot();
	}

	/**
	 * Gets the name of this material
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the display name of this material
	 * 
	 * @return the display name
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Sets the display name of this material
	 * 
	 * @param name the new display name
	 */
	public void setDisplayName(String name) {
		this.displayName = name;
	}

	/**
	 * Sets the model of this material
	 * 
	 * @param model the new model
	 */
	public Material setModel(Model model) {
		this.model = model;
		return this;
	}

	/**
	 * Gets the current model of this material
	 * 
	 * @return the current model
	 */
	public Model getModel() {
		return this.model;
	}

	/**
	 * Gets the maximum size of a stack of this material
	 * 
	 * @return the current max size
	 */
	public int getMaxStackSize() {
		return this.maxStackSize;
	}

	/**
	 * Sets the maximum size of a stack of this material
	 * 
	 * @param newValue the new maximum stack size
	 */
	public void setMaxStackSize(int newValue) {
		this.maxStackSize = newValue;
	}
	
	/**
	 * Fired when this material is being rendered in the inventory
	 * 
	 */
	public void onInventoryRender() {
	}
	
	/**
	 * Called when an entity interacts with the world while wielding this material
	 * @param entity that interacted
	 * @param position of the block
	 * @param type of interaction
	 * @param clickedface of the block
	 */
	public void onInteract(Entity entity, Point position, Action type, BlockFace clickedface) {
	}
	
	/**
	 * Called when an entity interacts with another entity while wielding this material
	 * 
	 * @param entity that is interacting with the entity
	 * @param other entity that was interacted with
	 * @param type of interaction
	 */
	public void onInteract(Entity entity, Entity other, Action type) {
	}
	
	/**
	 * Called when an entity interacts with nothing (air) while wielding this material
	 * 
	 * @param entity that is interacting
	 * @param type of interaction
	 */
	public void onInteract(Entity entity, Action type) {
	}
}
