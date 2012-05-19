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

import org.spout.api.collision.BoundingBox;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionStrategy;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.entity.BlockController;
import org.spout.api.entity.Entity;
import org.spout.api.entity.type.ControllerType;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.basic.BasicAir;
import org.spout.api.material.basic.BasicSkyBox;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.util.flag.ByteFlagContainer;

public class BlockMaterial extends Material implements Placeable {

	public static final BlockMaterial AIR = register(new BasicAir(), 0);
	public static final BlockMaterial SOLID = register(new BlockMaterial("solid").setHardness(1.f), 10000);
	public static final BlockMaterial UNBREAKABLE = register(new BlockMaterial("Unbreakable").setHardness(100.f), 10001);
	public static final BlockMaterial SKYBOX = register(new BasicSkyBox(), 10002);
	public static final BlockMaterial ERROR = register(new BlockMaterial("Missing Plugin").setHardness((100.f)), 10003);
	
	public BlockMaterial(String name) {
		super(name);
	}

	public BlockMaterial(String name, int data, Material parent) {
		super(name, data, parent);
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

	private ByteFlagContainer occlusion = new ByteFlagContainer(BlockFaces.NESWB);
	private float hardness = 0F;
	private float friction = 0F;
	private byte opacity = 0xF;
	private byte lightLevel = 0;
	private final CollisionModel collision = new CollisionModel(new BoundingBox(0F, 0F, 0F, 1F, 1F, 1F));
	private ControllerType controller = null;

	/**
	 * Sets the block controller associated with this material<br>
	 * Future calls to getController will return an instance of this block controller.
	 * 
	 * @param controller type to set to
	 * @return This block material
	 */
	public BlockMaterial setController(ControllerType controller) {
		this.controller = controller;
		return this;
	}

	/**
	 * Gets whether this Block Material has a Block Controller associated with it.
	 * @return True if it has a block controller
	 */
	public boolean hasController() {
		return this.controller != null;
	}

	/**
	 * Gets the block controller associated with this material from a block<br>
	 * If the block controller set is null or does not match, a new instance is created and set on the block and returned
	 * 
	 * @param block to get the Block Controller of
	 * @return The Block Controller
	 */
	public BlockController getController(Block block) {
		return getController(block, true);
	}

	/**
	 * Gets the block controller associated with this material from a block<br>
	 * If the block controller set is null or does not match, null is returned<br>
	 * If forced is set True, the controller is forcibly replaced with the one set in this material and is returned
	 * 
	 * @param block to get the Block Controller of
	 * @param forced whether to force-convert the controller if not found or invalid
	 * @return The Block Controller
	 */
	public BlockController getController(Block block, boolean forced) {
		if (this.controller == null) {
			throw new IllegalStateException("Can not obtain the block controller because no controller type is set for this material.");
		}
		BlockController controller = block.getController();
		if (controller != null) {
			Class<?> clazz = this.controller.getControllerClass();
			if (clazz.isAssignableFrom(controller.getClass())) {
				return controller;
			}
		}
		if (forced) {
			controller = (BlockController) this.controller.createController();
			block.setController(controller);
			return controller;
		} else {
			return null;
		}
	}

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
		if (this.hasController()) {
			block.setController(null);
		}
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
		this.occlusion.set(value ? BlockFaces.NESWBT : BlockFaces.NONE);
		return this;
	}

	/**
	 * Sets if a certain face of the block occludes
	 * @param face to set it of
	 * @param value whether it occludes
	 * @return this block material
	 */
	public BlockMaterial setOccludes(BlockFace face, boolean value) {
		if (face == BlockFace.THIS) {
			throw new IllegalArgumentException("Can not set occlusion state on block face THIS.");
		}
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
	
	@Override
	public boolean canPlace(Block block, short data, BlockFace against, boolean isClickedBlock) {
		return true;
	}
	
	@Override
	public boolean onPlacement(Block block, short data, BlockFace against, boolean isClickedBlock) {
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
