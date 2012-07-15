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
package org.spout.api.geo.cuboid;

import org.spout.api.Source;
import org.spout.api.entity.component.controller.BlockController;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.World;
import org.spout.api.geo.WorldSource;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.range.EffectRange;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.material.source.MaterialState;
import org.spout.api.math.IntVector3;
import org.spout.api.math.Vector3;
import org.spout.api.util.thread.LiveWrite;
import org.spout.api.util.thread.Threadsafe;

public interface Block extends MaterialState, WorldSource {

	/**
	 * Gets the {@link Point} position of this block in the world
	 * 
	 * @return the position
	 */
	public Point getPosition();

	/**
	 * Gets the {@link Chunk} this block is in
	 * 
	 * @return the Chunk
	 */
	public Chunk getChunk();

	/**
	 * Gets the {@link Region} this block is in
	 * 
	 * @return the Region
	 */
	public Region getRegion();

	/**
	 * Gets the {@link World} this block is in
	 * 
	 * @return the World
	 */
	@Override
	public World getWorld();

	/**
	 * Gets the x-coordinate of this block
	 * 
	 * @return the x-coordinate
	 */
	public int getX();

	/**
	 * Gets the y-coordinate of this block
	 * 
	 * @return the y-coordinate
	 */
	public int getY();

	/**
	 * Gets the z-coordinate of this block
	 * 
	 * @return the z-coordinate
	 */
	public int getZ();

	/**
	 * Translates this block using the offset and distance given
	 * 
	 * @param offset BlockFace to translate
	 * @param distance to translate
	 * @return a new Block instance
	 */
	public Block translate(BlockFace offset, int distance);

	/**
	 * Translates this block using the offset given
	 * 
	 * @param offset BlockFace to translate
	 * @return a new Block instance
	 */
	public Block translate(BlockFace offset);

	/**
	 * Translates this block using the offset given
	 * 
	 * @param offset Vector to translate
	 * @return a new Block instance
	 */
	public Block translate(Vector3 offset);

	/**
	 * Translates this block using the offset given
	 * 
	 * @param offset Vector to translate
	 * @return a new Block instance
	 */
	public Block translate(IntVector3 offset);

	/**
	 * Translates this block using the offsets given
	 * 
	 * @param dx offset to translate
	 * @param dy offset to translate
	 * @param dz offset to translate
	 * @return a new Block instance
	 */
	public Block translate(int dx, int dy, int dz);

	/**
	 * Translates this block above or below to the surface<br>
	 * The returned Block is the surface, <b>not</b> the first air block above.
	 * 
	 * @return a new Block instance
	 */
	public Block getSurface();

	/**
	 * Gets the source this block represents
	 * 
	 * @return the source
	 */
	public Source getSource();

	@Override
	public BlockMaterial getMaterial();

	/**
	 * Sets the data of this block
	 *
	 * @param data to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	@Override
	public Block setData(DataSource data);

	/**
	 * Sets the data of this block
	 *
	 * @param data to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	@Override
	public Block setData(int data);

	/**
	 * Adds the value to the data of this block
	 * 
	 * @param data to add
	 * @return this Block
	 * @throws NullPointerException
	 */
	public Block addData(int data);

	/**
	 * Sets the material of this block
	 *
	 * @param material to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	@Override
	public Block setMaterial(MaterialSource material);

	/**
	 * Sets the material and data of this block
	 *
	 * @param material to set to
	 * @param data to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	@Override
	public Block setMaterial(MaterialSource material, DataSource data);

	/**
	 * Sets the material and data of this block
	 *
	 * @param material to set to
	 * @param data to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	@Override
	public Block setMaterial(MaterialSource material, int data);
	
	/**
	 * Sets the given bits in the data for the block<br>
	 * <br>
	 * newData = oldData | (bits)
	 * 
	 * @param bits the bits to set
	 * @return the old data for the block
	 */
	@LiveWrite
	public short setDataBits(int bits);

	/**
	 * Sets the given bits in the data for the block<br>
	 * <br>
	 * newData = oldData | (bits)
	 * <br>or<br>
	 * newData = oldData & ~(bits)
	 * 
	 * @param bits the bits to set or clear
	 * @param set True to set the bits, False to clear
	 * @return the old data for the block
	 */
	@LiveWrite
	public short setDataBits(int bits, boolean set);

	/**
	 * Clears the given bits in the data for the block<br>
	 * <br>
	 * newData = oldData & (~bits)
	 * 
	 * @param bits the bits to clear
	 * @return the old data for the block
	 */
	@LiveWrite
	public short clearDataBits(int bits);

	/**
	 * Gets the data field from the block<br>
	 * <br>
	 * field = (data & bits) >> (shift)<br>
	 * <br>
	 * The shift value used shifts the least significant non-zero bit of bits to the LSB position
	 * 
	 * @param bits the bits of the field
	 * @return the field value
	 */
	@Threadsafe
	public int getDataField(int bits);

	/**
	 * Gets if any of the indicated bits are set.
	 * 
	 * @param bits the bits to check
	 * @return true if any of the given bits are set
	 */
	@Threadsafe
	public boolean isDataBitSet(int bits);

	/**
	 * Sets the data field for the block.  This is the reverse operation to the getDataField method.<br>
	 * <br>
	 * newData = ((value << shift) & bits) | (oldData & (~bits))<br>
	 * <br>
	 * The shift value used shifts the least significant non-zero bit of bits to the LSB position
	 * 
	 * @param bits the bits of the field
	 * @param value the new value of the field
	 * @return the old value of the field
	 */
	@LiveWrite
	@Threadsafe
	public int setDataField(int bits, int value);

	/**
	 * Adds a value to the data field for the block.  This is the reverse operation to the getBlockDataField method.<br>
	 * <br>
	 * newData = (((oldData + (value << shift)) & bits) | (oldData & ~bits))<br>
	 * <br>
	 * The shift value used shifts the least significant non-zero bit of bits to the LSB position
	 * 
	 * @param bits the bits of the field
	 * @param value to add to the value of the field
	 * @return the old value of the field
	 */
	@LiveWrite
	@Threadsafe
	public int addDataField(int bits, int value);

	/**
	 * Gets the current light level of the block<br>
	 * This is both sky and block light<br>
	 * Sky light is affected by the sky light level emitted by the world
	 * 
	 * @return the light level
	 */
	public byte getLight();

	/**
	 * Sets the sky light level to the given light level<br><br>
	 * <b>Note: For persistence, alter block material light levels instead</b>
	 * 
	 * @param light level to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	public Block setSkyLight(byte level);

	/**
	 * Sets the block light level to the given light level<br><br>
	 * <b>Note: For persistence, alter block material light levels instead</b>
	 *
	 * @param light level to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	public Block setBlockLight(byte level);

	/**
	 * Gets the block light level
	 *
	 * @return the block light level
	 */
	public byte getBlockLight();

	/**
	 * Gets the sky light level<br>
	 * The returned value is <b>not</b> affected by the world sky light that is emitted<br>
	 * It is the light level that is actually stored
	 *
	 * @return the sky light level
	 */
	public byte getSkyLightRaw();

	/**
	 * Gets the sky light level<br>
	 * The returned value is affected by the world sky light that is emitted
	 *
	 * @return the sky light level
	 */
	public byte getSkyLight();

	/**
	 * Gets the biome type for this block,
	 * if the world generator used uses biomes.
	 *
	 * @return The biome type for the block
	 */
	public Biome getBiomeType();

	/**
	 * Gets a controller associated with the block, or null if it has none.
	 *
	 * @return block controller
	 */
	public <T extends BlockController> T getController();

	/**
	 * Whether or not the block is associated with a block controller
	 *
	 * @return true if has a controller
	 */
	public boolean hasController();

	/**
	 * Gets if this block is above or at the surface<br>
	 * This is the case if the block comes into contact with the sky
	 * 
	 * @return True if above or at the surface, False if not
	 */
	public boolean isAtSurface();

	/**
	 * Queues a physics update on this block and all blocks within the given range, this method can be called from any thread
	 * 
	 * @return this Block
	 */
	public Block queueUpdate(EffectRange range);
	
	/**
	 * Resets all dynamic material updates queued for this block. This list is checked during the DYNAMIC_BLOCKS part 
	 * of the tick, and will cause the onPlacement method to be called.<br>
	 */
	public void resetDynamic();

	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @return the old update for that block at that time instant, or null if none
	 */
	public DynamicUpdateEntry dynamicUpdate();

	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @param nextUpdate the time for the next update
	 * @return the old update for that block at that time instant, or null if none
	 */
	public DynamicUpdateEntry dynamicUpdate(long nextUpdate);

	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @param nextUpdate the time for the next update
	 * @param hint non-persistent parameter to speed up the update
	 * @return the old update for that block at that time instant, or null if none
	 */
	public DynamicUpdateEntry dynamicUpdate(long nextUpdate, Object hint);
	
	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @param nextUpdate the time for the next update
	 * @param data persistent data to be used for the update
	 * @param hint non-persistent parameter to speed up the update
	 * @return the old update for that block at that time instant, or null if none
	 **/
	public DynamicUpdateEntry dynamicUpdate(long nextUpdate, int data, Object hint);
}
