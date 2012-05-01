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

import org.spout.api.collision.BoundingBox;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionStrategy;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.basic.BasicAir;
import org.spout.api.material.basic.BasicSkyBox;
import org.spout.api.material.block.BlockFace;
import org.spout.api.util.flag.ByteFlagContainer;

public class BlockMaterial extends Material {

	public static final BlockMaterial AIR = register(new BasicAir());
	public static final BlockMaterial SOLID = register(new BlockMaterial("solid", 10000).setHardness(1.f));
	public static final BlockMaterial UNBREAKABLE = register(new BlockMaterial("Unbreakable", 10001).setHardness(100.f));
	public static final BlockMaterial SKYBOX = register(new BasicSkyBox());
	public static final BlockMaterial ERROR = register(new BlockMaterial("Missing Plugin", 10003).setHardness((100.f)));
	
	public BlockMaterial(String name, int typeId) {
		super(name, typeId);
	}

	public BlockMaterial(String name, int typeId, int data, Material parent) {
		super(name, typeId, data, parent);
	}

	/**
	 * Gets the block at the given id, or null if none found
	 * 
	 * @param id to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial get(short id) {
		Material mat = Material.get(id);
		if (mat instanceof BlockMaterial) {
			return (BlockMaterial) mat;
		} else {
			return null;
		}
	}

	/**
	 * Gets the associated block material with it's name. Case-insensitive.
	 * 
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static BlockMaterial get(String name) {
		Material mat = Material.get(name);
		if (mat instanceof BlockMaterial) {
			return (BlockMaterial) mat;
		} else {
			return null;
		}
	}

	private ByteFlagContainer occlusion = new ByteFlagContainer(BlockFace.MASK_ALL);
	private float hardness = 0F;
	private float friction = 0F;
	private byte opacity = 0xF;
	private byte lightLevel = 0;
	private final CollisionModel collision = new CollisionModel(new BoundingBox(0F, 0F, 0F, 1F, 1F, 1F));

	@Override
	public BlockMaterial getSubMaterial(short data) {
		return (BlockMaterial) super.getSubMaterial(data);
	}
	
	/**
	 * Gets the friction of this block
	 * 
	 * @return friction value
	 */
	public float getFriction() {
		return this.friction;
	}

	/**
	 * Sets the friction of this block
	 * 
	 * @param slip friction value
	 * @return this material
	 */
	public BlockMaterial setFriction(float slip) {
		this.friction = slip;
		return this;
	}

	/**
	 * Gets the hardness of this block
	 * 
	 * @return hardness value
	 */
	public float getHardness() {
		return this.hardness;
	}

	/**
	 * Sets the hardness of this block
	 * 
	 * @param hardness hardness value
	 * @return this material
	 */
	public BlockMaterial setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}

	/**
	 * Gets the amount of light this block emits
	 * 
	 * @return light level
	 */
	public byte getLightLevel() {
		return this.lightLevel;
	}

	/**
	 * Sets the amount of light this block emits
	 * 
	 * @param level
	 * @return this material
	 */
	public BlockMaterial setLightLevel(byte level) {
		this.lightLevel = level;
		return this;
	}

	/**
	 * Gets the amount of light blocked by this block.
	 * 
	 * 0xF (15) represents a fully opaque block.
	 * 
	 * @return opacity
	 */
	public byte getOpacity() {
		return this.opacity;
	}
	
	/**
	 * Returns true if the block is opaque, false if not.
	 * @return True if opacity is 15, false if less than.
	 */
	public boolean isOpaque() {
		return this.opacity == 0xF;
	}

	/**
	 * Sets the amount of light blocked by this block.
	 * 
	 * 0xF (15) represents a fully opaque block.
	 * 
	 * @param level of opacity
	 * @return this material
	 */
	public BlockMaterial setOpacity(byte level) {
		this.opacity = level;
		return this;
	}

	/**
	 * True if this block acts as an obstacle when placing a block on it false
	 * if not.
	 * 
	 * If the block is not an obstacle, placement will replace this block.
	 * 
	 * @return if this block acts as a placement obstacle
	 */
	public boolean isPlacementObstacle() {
		return true;
	}
	
	/**
	 * True if this block requires physic updates when a neighbor block changes,
	 * false if not.
	 * 
	 * @return if this block requires physics updates
	 */
	public boolean hasPhysics() {
		return false;
	}

	/**
	 * Called when a block adjacent to this material is changed.
	 * 
	 * @param block that got updated
	 */
	public void onUpdate(Block block) {
	}

	/**
	 * Called when this block has been destroyed.
	 * 
	 * @param block that got destroyed
	 */
	public void onDestroy(Block block) {
		block.setMaterial(AIR).update();
	}

	/**
	 * Gets the bounding box area of this material
	 * 
	 * @return area
	 */
	public CollisionVolume getBoundingArea() {
		return this.collision.getVolume();
	}
	
	/**
	 * Gets the collision model associated with this block material
	 * 
	 * @return the collision model
	 */
	public CollisionModel getCollisionModel() {
		return this.collision;
	}
		
	/**
	 * True if this block has collision,
	 * false if not.
	 * 
	 * @return if this block has collision
	 */
	public boolean hasCollision() {
		return this.collision.getStrategy() != CollisionStrategy.NOCOLLIDE;
	}
	
	/**
	 * True if this block is a solid block
	 * false if not.
	 * 
	 * @return if this block has collision
	 */
	public boolean isSolid() {
		return this.collision.getStrategy() == CollisionStrategy.SOLID;
	}
	
	/**
	 * Sets if this block occludes all faces
	 * @param value whether it occludes
	 * @return this block material
	 */
	public BlockMaterial setOccludes(boolean value) {
		this.occlusion.set(value ? BlockFace.MASK_ALL : BlockFace.MASK_NONE);
		return this;
	}
	
	/**
	 * Sets if a certain face of the block occludes
	 * @param face to set it of
	 * @param value whether it occludes
	 * @return this block material
	 */
	public BlockMaterial setOccludes(BlockFace face, boolean value) {
		this.occlusion.set(face, value);
		return this;
	}
	
	/**
	 * Gets if a certain block face occludes
	 * @param face to get it of
	 * @return if the block face occludes
	 */
	public boolean occludes(BlockFace face) {
		return this.occlusion.get(face);
	}
	
	/**
	 * Gets if this block material has occlusion
	 * @return whether there is occlusion
	 */
	public boolean occludes() {
		return this.occlusion.isDirty();
	}
	
	/**
	 * Sets the collision strategy to use for this block
	 * 
	 * @param strategy
	 * @return this block material
	 */
	public BlockMaterial setCollision(CollisionStrategy strategy) {
		this.collision.setStrategy(strategy);
		return this;
	}
	
	/**
	 * Called when this block is about to be placed (before {@link onPlacement}), 
	 * checking if placement is allowed or not.
	 * 
	 * @param block to place
	 * @param data block data to use during placement
	 * @param against face against the block is placed
	 * @return true if placement is allowed
	 */
	public boolean canPlace(Block block, short data, BlockFace against) {
		return true;
	}
	
	/**
	 * Called when this block is placed, handles the actual placement.
	 * 
	 * @param block to affect
	 * @param data block data to use during placement
	 * @param against face against the block is placed
	 * @return true if placement is handled
	 */
	public boolean onPlacement(Block block, short data, BlockFace against) {
		block.setMaterial(this, data).update(true);
		return true;
	}

	/**
	 * Called when an entity interacts with this block material in the world
	 *
	 * @param entity that is interacting with this material
	 * @param position of the block interacted by the entity
	 * @param type of interaction
	 * @param clickedFace of the material clicked
	 */
	public void onInteractBy(Entity entity, Block block, Action type, BlockFace clickedFace) {
	}
}
