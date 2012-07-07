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
package org.spout.api.material;

/**
 * Defines the characteristics of Blocks or Items.
 */
import java.util.Arrays;

import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.math.MathHelper;
import org.spout.api.model.Model;
import org.spout.api.util.LogicUtil;

public abstract class Material extends MaterialRegistry implements MaterialSource {
	private final short id;
	private final short data;
	private final String name;
	private final boolean isSubMaterial;
	private final Material parent;
	private Model model;
	private String displayName;
	private int maxStackSize = 64;
	private short maxData = Short.MAX_VALUE;
	private Material[] submaterials = null;
	private Material[] submaterialsContiguous = null;
	private volatile boolean submaterialsDirty = true;
	private final short dataMask;

	/**
	 * Creates and registers a material
	 * 
	 * @param name of the material
	 */
	public Material(String name) {
		this.isSubMaterial = false;
		this.displayName = name;
		this.name = getClass().getCanonicalName() + "_" + name.replace(' ', '_');
		this.parent = this;
		this.data = 0;
		this.id = (short) MaterialRegistry.register(this);
		this.dataMask = 0;
	}
	
	/**
	 * Creates and registers a material
	 * 
	 * @param dataMask for the material
	 * @param name of the material
	 */
	public Material(short dataMask, String name) {
		this.isSubMaterial = false;
		this.displayName = name;
		this.name = getClass().getCanonicalName() + "_" + name.replace(' ', '_');
		this.parent = this;
		this.data = 0;
		this.id = (short) MaterialRegistry.register(this);
		this.dataMask = dataMask;
	}

	/**
	 * Creates and registers a material
	 * 
	 * @param name of the material
	 * @param data
	 * @param parent material
	 */
	public Material(String name, int data, Material parent) {
		this.isSubMaterial = true;
		this.displayName = name;
		this.name = name.replace(' ', '_');
		this.parent = parent;
		this.data = (short) data;
		this.id = (short) MaterialRegistry.register(this);
		this.dataMask = parent.getDataMask();
	}
	
	/**
	 * Creates a material with a reserved id
	 * 
	 * @param name of the material
	 * @param id to reserve
	 */
	protected Material(String name, short id) {
		this.isSubMaterial = true;
		this.displayName = name;
		this.name = name.replace(' ', '_');
		this.parent = this;
		this.data = 0;
		this.id = (short) MaterialRegistry.register(this, id);
		this.dataMask = 0;
	}
	
	/**
	 * Creates a material with a reserved id
	 * 
	 * @param name of the material
	 * @param id to reserve
	 */
	protected Material(short dataMask, String name, short id) {
		this.isSubMaterial = true;
		this.displayName = name;
		this.name = name.replace(' ', '_');
		this.parent = this;
		this.data = 0;
		this.id = (short) MaterialRegistry.register(this, id);
		this.dataMask = dataMask;
	}

	public final short getId() {
		return this.id;
	}

	/**
	 * Gets the data value associated with this material. if this material does not have 
	 * or is not a sub material, then (getData() & getDataMask()) is equal to zero.  
	 * 
	 * @return data value
	 */
	public final short getData() {
		return this.data;
	}
	
	/**
	 * Gets the data mask for this material, and sub-materials.  When determining 
	 * sub-material, this mask is applied to the data before the comparison is performed.
	 * 
	 * @return data mask
	 */
	public final short getDataMask() {
		return this.dataMask;
	}

	/**
	 * Checks if this material is a sub material or not
	 * 
	 * @return true if it is a sub material
	 */
	public final boolean isSubMaterial() {
		return isSubMaterial;
	}

	/**
	 * Checks if this material has other materials mapped by data
	 * 
	 * @return true if this material has sub materials
	 */
	public final boolean hasSubMaterials() {
		return this.submaterials != null;
	}

	/**
	 * Gets all sub materials of this material
	 * 
	 * @return an array of sub materials
	 */
	public final Material[] getSubMaterials() {
		if (this.submaterials == null) {
			return new Material[0];
		}

		if (submaterialsDirty) {
			int materialCount = 0;
			for (int i = 0; i < this.submaterials.length; i++) {
				if (this.submaterials[i] != null) {
					materialCount++;
				}
			}
			Material[] newSubmaterials = new Material[materialCount];
			materialCount = 0;
			for (int i = 0; i < this.submaterials.length; i++) {
				if (this.submaterials[i] != null) {
					newSubmaterials[materialCount++] = this.submaterials[i];
				}
			}
			this.submaterialsContiguous = newSubmaterials;
			submaterialsDirty = false;
		}
		Material[] sm = submaterialsContiguous;
		return Arrays.copyOf(sm, sm.length);
	}

	/**
	 * Recursively gets the sub material mapped to the data value specified
	 * 
	 * @param data to search for
	 * @return the sub material, or this material if not found
	 */
	public Material getSubMaterial(short data) {
		if (this.hasSubMaterials()) {
			short maskedData = (short)(data & dataMask);
			Material material = this.submaterials[maskedData];
			if (material != null) {
				return material.getSubMaterial(maskedData);
			}
		}
		return this;
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
	public final void registerSubMaterial(Material material) {
		submaterialsDirty = true;
		try {
			int data = material.data & 0xFFFF;
			if ((data & dataMask) != data) {
				throw new IllegalArgumentException("Sub material data value has non-zero bits outside data mask");
			}
			if (material.isSubMaterial) {
				if (material.getParentMaterial() == this) {
					if (this.submaterials == null) {
						this.submaterials = new Material[16];
					}
					if (data >= this.submaterials.length) {
						int newSize = MathHelper.roundUpPow2(data + (data >> 1));
						Material[] newSubmaterials = new Material[newSize];
						for (int i = 0; i < submaterials.length; i++) {
							newSubmaterials[i] = submaterials[i];
						}
						this.submaterials = newSubmaterials;
					}
					if (this.submaterials[data] == null) {
						this.submaterials[data] = material;
					} else {
						throw new IllegalArgumentException("Two sub material registered for the same data value");
					}
				} else {
					throw new IllegalArgumentException("Sub Material is registered to a material different than the parent!");
				}
			} else {
				throw new IllegalArgumentException("Material is not a valid sub material!");
			}
		} finally {
			submaterialsDirty = true;
		}
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
	public final String getName() {
		return this.name;
	}

	/**
	 * Gets the display name of this material
	 * 
	 * @return the display name
	 */
	public final String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Sets the display name of this material
	 * 
	 * @param name the new display name
	 */
	public final void setDisplayName(String name) {
		this.displayName = name;
	}

	/**
	 * Sets the model of this material
	 * 
	 * @param model the new model
	 */
	public final Material setModel(Model model) {
		this.model = model;
		return this;
	}

	/**
	 * Gets the current model of this material
	 * 
	 * @return the current model
	 */
	public final Model getModel() {
		return this.model;
	}

	/**
	 * Gets the maximum size of a stack of this material
	 * 
	 * @return the current max size
	 */
	public final int getMaxStackSize() {
		return this.maxStackSize;
	}

	/**
	 * Sets the maximum size of a stack of this material
	 * 
	 * @param newValue the new maximum stack size
	 */
	public final void setMaxStackSize(int newValue) {
		this.maxStackSize = newValue;
	}

	/**
	 * Gets the maximum data a stack of this material can have
	 */
	public final short getMaxData() {
		return this.maxData;
	}

	/**
	 * Sets the maximum of the data value this material can have
	 * @param newValue the new maximum data
	 */
	public final void setMaxData(short newValue) {
		this.maxData = newValue;
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
	 * @param block that got interacted with
	 * @param type of interaction
	 * @param clickedface of the block
	 */
	public void onInteract(Entity entity, Block block, Action type, BlockFace clickedface) {
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

	@Override
	public boolean isMaterial(Material... materials) {
		return LogicUtil.equalsAny(this, materials);
	}

	/**
	 * Checks whether this material equals any of the materials given
	 * @param materials to compare to
	 * @return whether it equals one of the materials
	 */
	public boolean equals(Material... materials) {
		return this.isMaterial(materials);
	}

	@Override
	public String toString() {
		return "Material {" + getName() + "}";
	}
}
