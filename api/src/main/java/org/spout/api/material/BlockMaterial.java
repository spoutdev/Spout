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

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.spout.api.component.block.BlockComponent;
import org.spout.api.entity.Entity;
import org.spout.api.event.Cause;
import org.spout.api.event.cause.MaterialCause;
import org.spout.api.event.entity.EntityCollideBlockEvent;
import org.spout.api.event.player.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.basic.Air;
import org.spout.api.material.basic.Solid;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.GenericMath;
import org.spout.api.math.Vector3;
import org.spout.api.resource.SpoutModels;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.api.util.flag.Flag;
import org.spout.physics.ReactDefaults;
import org.spout.physics.collision.shape.CollisionShape;

/**
 * Defines the specific characteristics of a Block
 */
@SuppressWarnings ("unchecked")
public class BlockMaterial extends Material implements Placeable {
	public static final BlockMaterial AIR = new Air();
	public static final BlockMaterial UNBREAKABLE = new Solid("Unbreakable");
	public static final BlockMaterial UNGENERATED = new Solid("Ungenerated").setInvisible();
	public static final BlockMaterial ERROR = new Solid("Missing Plugin");
	public static final BlockMaterial SOLID_BLUE = new Solid("SolidBlue", SpoutModels.SOLID_BLUE);
	public static final BlockMaterial SOLID_BROWN = new Solid("SolidBrown", SpoutModels.SOLID_BROWN);
	public static final BlockMaterial SOLID_GREEN = new Solid("SolidGreen", SpoutModels.SOLID_GREEN);
	public static final BlockMaterial SOLID_LIGHTGREEN = new Solid("SolidLightGreen", SpoutModels.SOLID_LIGHTGREEN);
	public static final BlockMaterial SOLID_RED = new Solid("SolidRed", SpoutModels.SOLID_RED);
	public static final BlockMaterial SOLID_SKYBLUE = new Solid("SolidSkyBlue", SpoutModels.SOLID_SKYBLUE);
	private final Set<Class<? extends BlockComponent>> components;
	private ByteBitSet occlusion = new ByteBitSet(BlockFaces.NESWBT);
	private float hardness = 0F;
	private byte opacity = 0xF;
	private boolean surface = true;
	private boolean invisible = false;
	//Collision
	private CollisionShape shape;
	private float mass = 1;
	private float friction = ReactDefaults.DEFAULT_FRICTION_COEFFICIENT;
	private float restitution = ReactDefaults.DEFAULT_RESTITUTION_COEFFICIENT;
	private boolean isGhost = false;

	public BlockMaterial(short dataMask, String name, String model, CollisionShape shape, Class<? extends BlockComponent>... components) {
		super(dataMask, name, model);
		this.components = ImmutableSet.copyOf(components);
		this.shape = shape;
	}

	public BlockMaterial(String name, int data, Material parent, String model, CollisionShape shape, Class<? extends BlockComponent>... components) {
		super(name, data, parent, model);
		this.components = ImmutableSet.copyOf(components);
		this.shape = shape;
	}

	protected BlockMaterial(String name, short id, CollisionShape shape, Class<? extends BlockComponent>... components) {
		super(name, id);
		this.components = ImmutableSet.copyOf(components);
		this.shape = shape;
	}

	protected BlockMaterial(String name, CollisionShape shape, Class<? extends BlockComponent>... components) {
		super(name);
		this.components = ImmutableSet.copyOf(components);
		this.shape = shape;
	}

	/**
	 * Gets the block material with the given id, or null if none found
	 *
	 * @param id to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial get(short id) {
		Material mat = Material.get(id);
		if (!(mat instanceof BlockMaterial)) {
			return null;
		}

		return (BlockMaterial) mat;
	}

	/**
	 * Gets the block (sub-)material with the given id and data, or null if none found
	 *
	 * @param id to get
	 * @return block, or null if none found
	 */
	public static BlockMaterial get(short id, short data) {
		Material m = Material.get(id, data);
		if (m instanceof BlockMaterial) {
			return (BlockMaterial) m;
		}
		return null;
	}

	/**
	 * Gets the associated block material with it's name. Case-insensitive.
	 *
	 * @param name to lookup
	 * @return material, or null if none found
	 */
	public static BlockMaterial get(String name) {
		Material mat = Material.get(name);
		if (!(mat instanceof BlockMaterial)) {
			return null;
		}

		return (BlockMaterial) mat;
	}

	@Override
	public BlockMaterial getSubMaterial(short data) {
		return (BlockMaterial) super.getSubMaterial(data);
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
	public byte getLightLevel(short data) {
		return 0;
	}

	/**
	 * Gets the amount of light this block emits
	 *
	 * @return light level
	 */
	public byte getLightLevel() {
		return getLightLevel(getData());
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
	 *
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
	 * @param level of opacity, a value from 0 to 15
	 * @return this material
	 */
	public BlockMaterial setOpacity(int level) {
		this.opacity = (byte) GenericMath.clamp(level, 0, 15);
		if (level > 0) {
			surface = true;
		} else {
			surface = false;
		}
		return this;
	}

	/**
	 * Turns this Block Material in a fully opaque block, not letting light through from any side<br> Sets opacity to 15 and sets occlusion to all faces
	 *
	 * @return this Block Material
	 */
	public BlockMaterial setOpaque() {
		this.occlusion.set(BlockFaces.NESWBT);
		return this.setOpacity(15);
	}

	/**
	 * Turns this Block Material in a fully transparent block, letting light through from all sides<br> Sets the opacity to 0 and sets occlusion to none
	 *
	 * @return this Block Material
	 */
	public BlockMaterial setTransparent() {
		this.occlusion.set(BlockFaces.NONE);
		return this.setOpacity(0);
	}

	/**
	 * Sets this Block Material as a surface material.  The height of the highest surface material is considered the surface height.
	 *
	 * @return this Block Material
	 */
	public BlockMaterial setSurface() {
		this.surface = true;
		return this;
	}

	/**
	 * Sets this Block Material as a non-surface material
	 *
	 * @return this Block Material
	 */
	public BlockMaterial setSky() {
		this.surface = false;
		return this;
	}

	/**
	 * True if this block acts as an obstacle when placing a block on it false if not.
	 *
	 * If the block is not an obstacle, placement will replace this block.
	 *
	 * @return if this block acts as a placement obstacle
	 */
	public boolean isPlacementObstacle() {
		return true;
	}

	/**
	 * True if this block requires physic updates when a neighbor block changes, false if not.
	 *
	 * @return if this block requires physics updates
	 */
	public boolean hasPhysics() {
		return false;
	}

	/**
	 * Called when a block near to this material is changed.<br>
	 *
	 * @param oldMaterial the previous material, or null if the update was not due to a material change
	 * @param block that got updated
	 * @return true if the block was updated
	 */
	public void onUpdate(BlockMaterial oldMaterial, Block block) {
	}

	/**
	 * Returns the maximum range of effect for physics updates to this material. This is triggered when the material is set to this material, or when the data is changed. <br> When triggered, all blocks
	 * in this range are queued for updating.
	 *
	 * @return the maximum range of updates
	 */
	public EffectRange getPhysicsRange(short data) {
		return EffectRange.THIS_AND_NEIGHBORS;
	}

	/**
	 * Returns the maximum range of effect for physics updates to this material, when it is replaced by another material.  This is triggered when the material or sub-material (data & dataMask) is
	 * changed. <br> When triggered, all blocks in this range are queued for updating.
	 *
	 * @param data the data the block had when destroyed
	 * @return the maximum range of updates
	 */
	public EffectRange getDestroyRange(short data) {
		return getPhysicsRange(data);
	}

	/**
	 * Gets the maximum distance of any physics effect.  This is used to determine if the update can be handled by the region thread and not to determine if any blocks should be updated.
	 *
	 * @param data the data of the material
	 * @return the maximum range of updates
	 */
	public EffectRange getMaximumPhysicsRange(short data) {
		return getPhysicsRange(data);
	}

	/**
	 * Performs the block destroy procedure without initial flags
	 *
	 * @param block to destroy
	 * @param cause of the destruction
	 * @return True if destroying was successful
	 */
	public boolean destroy(Block block, Cause<?> cause) {
		return this.destroy(block, new HashSet<Flag>(), cause);
	}

	/**
	 * Performs the block destroy procedure
	 *
	 * @param block to destroy
	 * @param flags to initially use for destruction
	 * @return True if destroying was successful
	 */
	public boolean destroy(Block block, Set<Flag> flags, Cause<?> cause) {
		this.getBlockFlags(block, flags);
		if (this.onDestroy(block, cause)) {
			this.onPostDestroy(block, flags);
			return true;
		}
		return false;
	}

	/**
	 * Called when this block has to be destroyed.<br> This function performs the actual destruction of the block.
	 *
	 * @param block that got destroyed
	 * @return true if the destruction occurred
	 */
	public boolean onDestroy(Block block, Cause<?> cause) {
		return block.setMaterial(AIR, cause);
	}

	/**
	 * Called after this block has been destroyed.<br> This function performs possible post-destroy operations, such as effects.
	 *
	 * @param block of the material that got destroyed
	 * @param flags describing the destroy situation
	 */
	public void onPostDestroy(Block block, Set<Flag> flags) {
	}

	/**
	 * Gets all the flags associated with this Material as a Block<br> The flags are added to the input collection
	 *
	 * @param block of this Material
	 * @param flags to add to
	 */
	public void getBlockFlags(Block block, Set<Flag> flags) {
	}

	/**
	 * Gets the occluded faces of this Block Material for the data value specified<br> Occluded faces do not let light though and require rendering behind it at those faces
	 *
	 * @param data value of the material
	 * @return the occluded faces
	 */
	public ByteBitSet getOcclusion(short data) {
		return this.occlusion;
	}

	/**
	 * Sets the occludes faces of this Block Material<br> Occluded faces do not let light though and require rendering behind it at those faces
	 *
	 * @param data of this Block Material
	 * @param faces to make this Block Material occlude
	 * @return this Block Material
	 */
	public BlockMaterial setOcclusion(short data, BlockFaces faces) {
		this.getOcclusion(data).set(faces);
		return this;
	}

	/**
	 * Sets the occludes face of this Block Material<br> Occluded faces do not let light though and require rendering behind it at those faces
	 *
	 * @param data of this Block Material
	 * @param face to make this Block Material occlude
	 * @return this Block Material
	 */
	public BlockMaterial setOcclusion(short data, BlockFace face) {
		this.getOcclusion(data).set(face);
		return this;
	}

	/**
	 * Gets if the a face should be rendered
	 *
	 * @param face the fact to render
	 * @param material the material of the neighbouring block
	 */
	public boolean isFaceRendered(BlockFace face, BlockMaterial material) {
		return true;
	}

	@Override
	public boolean canPlace(Block block, short data, BlockFace against, Vector3 clickedPos, boolean isClickedBlock, Cause<?> cause) {
		return canCreate(block, data, cause);
	}

	@Override
	public void onPlacement(Block block, short data, BlockFace against, Vector3 clickedPos, boolean isClickedBlock, Cause<?> cause) {
		this.onCreate(block, data, cause);
	}

	/**
	 * Checks the block to see if it can be created at that position<br> Orientation-specific checks are performed in the {@link canPlace} method<br> Use this method to see if creation is possible at a
	 * given position when not placed
	 *
	 * @param block this Block Material should be created in
	 * @param data for the material
	 * @param cause of this creation
	 * @return True if creation is possible, False if not
	 */
	public boolean canCreate(Block block, short data, Cause<?> cause) {
		return true;
	}

	/**
	 * Creates this Block Material at a block in the world<br> Orientation-specific changes are performed in the {@link onPlacement} method<br> Use this method to create the block at a given position
	 * when not placed
	 *
	 * @param block to create this Block Material in
	 * @param data for the material
	 * @param cause of this creation
	 */
	public void onCreate(Block block, short data, Cause<?> cause) {
		block.setMaterial(this, data, cause);
	}

	/**
	 * Called when an entity interacts with this block material in the world
	 *
	 * @param entity that is interacting with this material
	 * @param type of interaction
	 * @param clickedFace of the material clicked
	 */
	public void onInteractBy(Entity entity, Block block, Action type, BlockFace clickedFace) {
	}

	/**
	 * Called when the entity collides with a block material
	 *
	 * @param event The collision event, containing the collision information
	 */
	public void onCollided(final EntityCollideBlockEvent event) {
	}

	/**
	 * Returns true if the material is a surface material.  The world's surface height is defined as the y coordinate of the highest surface material in each column.
	 */
	public boolean isSurface() {
		return surface;
	}

	/**
	 * Returns true if the block is completely invisible
	 *
	 * @return True if the block should never be rendered
	 */
	public boolean isInvisible() {
		return this.invisible;
	}

	/**
	 * Turns this material invisible and sets it as non-occluding.  Invisible blocks are not rendered.
	 */
	public BlockMaterial setInvisible() {
		this.invisible = true;
		this.occlusion.set(BlockFaces.NONE);
		return this;
	}

	/**
	 * Returns true if the block is transparent, false if not.
	 *
	 * @return True if opacity is 0, false if more than.
	 */
	public boolean isTransparent() {
		return this.opacity == 0;
	}

	/**
	 * Called by the dynamic block update system.  If a material is changed into a material that it is not compatible with, then this will automatically trigger a block reset.
	 *
	 * @param m the other material
	 * @return true if the two materials are compatible
	 */
	public boolean isCompatibleWith(BlockMaterial m) {
		return (m.getId() == getId() && ((m.getData() ^ getData()) & getDataMask()) == 0);
	}

	/**
	 * Helper method to create a MaterialCause.
	 *
	 * Same as using new MaterialCause(material, block)
	 *
	 * @param block location of the event
	 * @return cause
	 */
	public Cause<BlockMaterial> toCause(Block block) {
		return new MaterialCause<>(this, block);
	}

	/**
	 * Helper method to create a MaterialCause.
	 *
	 * Same as using new MaterialCause(material, block)
	 *
	 * @param block location of the event
	 * @return cause
	 */
	public Cause<BlockMaterial> toCause(Point p) {
		return new MaterialCause<>(this, p.getWorld().getBlock(p));
	}

	public Set<Class<? extends BlockComponent>> getComponents() {
		return components;
	}

	/**
	 * Returns if this BlockMaterial is a ghost object.
	 *
	 * @return True if ghost, false if not
	 */
	public boolean isGhost() {
		return isGhost;
	}

	/**
	 * Sets if this BlockMaterial should be a detector "ghost" material.
	 * <p>
	 * This means any collisions with this BlockMaterial will not incur adjustments for the {@link Entity} which collided: instead callbacks will be alerted and the Entity will be able to move freely
	 * through this BlockMaterial (by default).
	 * <p>
	 * If this BlockMaterial has a null {@link CollisionShape}, this setting will have no effect until a reference is set.
	 *
	 * @param isGhost
	 */
	public void setGhost(final boolean isGhost) {
		this.isGhost = isGhost;
	}

	/**
	 * Get the mass of this BlockMaterial
	 *
	 * @return The mass
	 */
	public float getMass() {
		return mass;
	}

	/**
	 * Sets the mass of this BlockMaterial
	 *
	 * @param mass The new mass
	 * @return This material, for chaining
	 * @throws IllegalArgumentException If provided mass is < 1f
	 */
	public BlockMaterial setMass(final float mass) {
		if (mass < 1) {
			throw new IllegalArgumentException("Mass must be greater than or equal to 1f");
		}
		this.mass = mass;
		return this;
	}

	/**
	 * Get the friction of this BlockMaterial
	 *
	 * @return The friction
	 */
	public float getFriction() {
		return friction;
	}

	/**
	 * Sets the friction of this BlockMaterial
	 *
	 * @param friction The new friction
	 * @return This material, for chaining
	 * @throws IllegalArgumentException If provided friction is < 0f
	 */
	public BlockMaterial setFriction(final float friction) {
		if (friction < 0 || friction > 1) {
			throw new IllegalArgumentException("Friction must be between 0 and 1 (inclusive)");
		}
		this.friction = friction;
		return this;
	}

	/**
	 * Get the restitution of this BlockMaterial
	 *
	 * @return The restitution
	 */
	public float getRestitution() {
		return restitution;
	}

	/**
	 * Sets the restitution of this BlockMaterial
	 *
	 * @param restitution The new restitution
	 * @return This material, for chaining
	 * @throws IllegalArgumentException If provided restitution is < 0f
	 */
	public BlockMaterial setRestitution(final float restitution) {
		if (restitution < 0 || restitution > 1) {
			throw new IllegalArgumentException("Restitution must be between 0 and 1 (inclusive)");
		}
		this.restitution = restitution;
		return this;
	}

	/**
	 * Gets the {@link CollisionShape} this BlockMaterial has.
	 *
	 * @return the collision shape
	 */
	public CollisionShape getShape() {
		return shape;
	}

	/**
	 * Sets the {@link CollisionShape} this BlockMaterial has/
	 *
	 * @param shape The new collision shape
	 * @return This material, for chaining
	 */
	public BlockMaterial setShape(final CollisionShape shape) {
		this.shape = shape;
		return this;
	}
}