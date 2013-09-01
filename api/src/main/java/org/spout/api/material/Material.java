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
package org.spout.api.material;

/**
 * Defines the characteristics of Blocks or Items.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.event.player.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.block.BlockFace;
import org.spout.api.model.Model;
import org.spout.api.render.effect.MeshEffect;
import org.spout.api.resource.SpoutModels;
import org.spout.api.util.LogicUtil;
import org.spout.api.util.flag.Flag;
import org.spout.api.util.flag.FlagSingle;
import org.spout.math.GenericMath;

public abstract class Material extends MaterialRegistry {
	private final short id;
	private final short data;
	private final String name;
	private final boolean isSubMaterial;
	private final Material parent;
	private final Material root;
	private final Model model;
	private String displayName;
	private int maxStackSize = 64;
	private short maxData = Short.MAX_VALUE;
	private final AtomicReference<Material[]> subMaterials;
	private Material[] submaterialsContiguous = null;
	private volatile boolean submaterialsDirty = true;
	private final short dataMask;
	private final FlagSingle useFlag = new FlagSingle();
	private final List<MeshEffect> meshEffects = new ArrayList<>();

	/**
	 * Creates a material with a dataMask, name
	 */
	public Material(short dataMask, String name, String model) {
		this.isSubMaterial = false;
		this.displayName = name;
		this.name = getClass().getCanonicalName() + "_" + name.replace(' ', '_');
		this.parent = this;
		this.data = 0;
		this.id = (short) MaterialRegistry.register(this);
		this.subMaterials = MaterialRegistry.getSubMaterialReference(this.id);
		this.dataMask = dataMask;
		this.root = this;
		if (model == null) {
			model = SpoutModels.DEFAULT_MODEL;
		}
		if (Spout.getEngine().getPlatform() == Platform.CLIENT) {
			this.model = (Model) Spout.getFileSystem().getResource(model);
		} else {
			this.model = null;
		}
	}

	/**
	 * Creates and registers a material
	 *
	 * @param name of the material
	 */
	public Material(String name) {
		this((short) 0, name);
	}

	public Material(String name, String model) {
		this((short) 0, name, model);
	}
	/**
	 * Creates and registers a material
	 *
	 * @param dataMask for the material
	 * @param name of the material
	 */
	public Material(short dataMask, String name) {
		this(dataMask, name, SpoutModels.DEFAULT_MODEL);
	}

	/**
	 * Creates and registers a sub material
	 *
	 * @param name of the material
	 * @param parent material
	 */
	public Material(String name, int data, Material parent) {
		this(name, data, parent, null);
	}

	/**
	 * Creates and registers a sub material
	 *
	 * @param name of the material
	 * @param parent material
	 */
	public Material(String name, int data, Material parent, String model) {
		this.isSubMaterial = true;
		this.displayName = name;
		this.name = name.replace(' ', '_');
		this.parent = parent;
		this.data = (short) data;
		this.id = (short) MaterialRegistry.register(this);
		this.subMaterials = MaterialRegistry.getSubMaterialReference(this.id);
		this.dataMask = parent.getDataMask();
		this.root = parent.getRoot();
		if (model == null) {
			model = SpoutModels.DEFAULT_MODEL;
		}
		if (Spout.getEngine().getPlatform() == Platform.CLIENT) {
			this.model = (Model) Spout.getFileSystem().getResource(model);
		} else {
			this.model = null;
		}
	}

	/**
	 * Creates a material with a reserved id
	 *
	 * @param name of the material
	 * @param id to reserve
	 */
	protected Material(String name, short id) {
		this.isSubMaterial = false;
		this.displayName = name;
		this.name = name.replace(' ', '_');
		this.parent = this;
		this.data = 0;
		this.id = (short) MaterialRegistry.register(this, id);
		this.subMaterials = MaterialRegistry.getSubMaterialReference(this.id);
		this.dataMask = 0;
		this.root = this;
		if (Spout.getEngine().getPlatform() == Platform.CLIENT) {
			this.model = (Model) Spout.getFileSystem().getResource(SpoutModels.DEFAULT_MODEL);
		} else {
			this.model = null;
		}
	}

	/**
	 * Add a BatchEffect
	 */
	public void addMeshEffect(MeshEffect batchEffect) {
		meshEffects.add(batchEffect);
	}

	public final short getId() {
		return this.id;
	}

	/**
	 * Gets the data value associated with this material. if this material does not have or is not a sub material, then (getData() & getDataMask()) is equal to zero.
	 *
	 * @return data value
	 */
	public final short getData() {
		return this.data;
	}

	/**
	 * Gets the data mask for this material, and sub-materials.  When determining sub-material, this mask is applied to the data before the comparison is performed.
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
		return this.subMaterials.get().length > 1;
	}

	/**
	 * Gets all sub materials of this material
	 *
	 * @return an array of sub materials
	 */
	public final Material[] getSubMaterials() {
		if (submaterialsDirty) {
			int materialCount = 0;
			Material[] sm = subMaterials.get();
			for (int i = 0; i < sm.length; i++) {
				if (sm[i] != null) {
					materialCount++;
				}
			}
			Material[] newSubmaterials = new Material[materialCount];
			materialCount = 0;
			for (int i = 0; i < sm.length; i++) {
				if (sm[i] != null) {
					newSubmaterials[materialCount++] = sm[i];
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
		short maskedData = (short) (data & dataMask);
		return subMaterials.get()[maskedData];
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
					boolean success = false;
					while (!success) {
						Material[] sm = subMaterials.get();
						if (data >= sm.length) {
							int newSize = GenericMath.roundUpPow2(data + (data >> 1) + 1);
							Material[] newSubmaterials = new Material[newSize];
							System.arraycopy(sm, 0, newSubmaterials, 0, sm.length);
							success = subMaterials.compareAndSet(sm, newSubmaterials);
						} else {
							success = true;
						}
					}
					Material[] sm = subMaterials.get();
					if (sm[data] == null) {
						sm[data] = material;
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

	@Deprecated
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
	 * Gets the root parent of this sub material
	 *
	 * @return the material root
	 */
	public Material getRoot() {
		return this.root;
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
	 *
	 * @param newValue the new maximum data
	 */
	public final void setMaxData(short newValue) {
		this.maxData = newValue;
	}

	/**
	 * Gets the Flag associated when using this Material
	 *
	 * @return Material use flag
	 */
	public Flag getUseFlag() {
		return this.useFlag;
	}

	/**
	 * Gets all the flags associated with this Material as an Item<br> The flags are added to the input collection
	 *
	 * @param item stack of this Material
	 * @param flags to add to
	 */
	public void getItemFlags(ItemStack item, Set<Flag> flags) {
		flags.add(this.getUseFlag());
	}

	/**
	 * Fired when this material is being rendered in the inventory
	 */
	public void onInventoryRender() {
	}

	/**
	 * Called when an entity interacts with the world while wielding this material
	 *
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

	public boolean isMaterial(Material... materials) {
		if (LogicUtil.equalsAny(this, materials)) {
			return true;
		}
		if (this.getRoot() != this && LogicUtil.equalsAny(this.getRoot(), materials)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Material) {
			return other == this;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Material {" + getName() + "}";
	}

	public Collection<MeshEffect> getMeshEffects() {
		return Collections.unmodifiableCollection(meshEffects);
	}

	/**
	 * Indicates that the dataMask covers the least significant bits.<br> <br> This method is used when verifying that the dataMask is set correctly
	 */
	public boolean hasLSBDataMask() {
		return true;
	}
}
