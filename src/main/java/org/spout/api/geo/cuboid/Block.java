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
import org.spout.api.entity.BlockController;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.InsertionPolicy;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.material.source.MaterialState;
import org.spout.api.math.Vector3;
import org.spout.api.util.thread.LiveWrite;
import org.spout.api.util.thread.Threadsafe;

public interface Block extends MaterialState {

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
	 * Sets the x-coordinate of this block
	 * 
	 * @param x coordinate to set to
	 * @return a new Block instance
	 */
	public Block setX(int x);

	/**
	 * Sets the y-coordinate of this block
	 * 
	 * @param y coordinate to set to
	 * @return a new Block instance
	 */
	public Block setY(int y);

	/**
	 * Sets the z-coordinate of this block
	 * 
	 * @param z coordinate to set to
	 * @return a new Block instance
	 */
	public Block setZ(int z);

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
	 * Translates this block using the offsets given
	 * 
	 * @param dx offset to translate
	 * @param dy offset to translate
	 * @param dz offset to translate
	 * @return a new Block instance
	 */
	public Block translate(int dx, int dy, int dz);

	/**
	 * Gets the source this block represents
	 * 
	 * @return the source
	 */
	public Source getSource();

	/**
	 * Sets the source this block represents
	 * 
	 * @param source to set to
	 * @return a clone of this block with the new source set
	 */
	public Block setSource(Source source);
	
	@Override
	public BlockMaterial getMaterial();

	@Override
	@Deprecated
	public BlockMaterial getSubMaterial();

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
	public short setBlockDataBits(short bits);
	
	/**
	 * Clears the given bits in the data for the block<br>
	 * <br>
	 * newData = oldData & (~bits)
	 * 
	 * @param bits the bits to clear
	 * @return the old data for the block
	 */
	@LiveWrite
	public short clearBlockDataBits(short bits);
	
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
	public int getBlockDataField(int bits);
	
	/**
	 * Sets the data field from the block.  This is the reverse operation to the getBlockDataField method.<br>
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
	public int setBlockDataField(int bits, int value);

	/**
	 * Gets the block light level
	 *
	 * @return the block light level
	 * @throws NullPointerException
	 */
	public byte getLight();

	/**
	 * Sets the block light level to the given light level
	 *
	 * @param light level to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	public Block setLight(byte level);

	/**
	 * Gets the sky light level
	 *
	 * @return the sky light level
	 * @throws NullPointerException
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
	 * Sets the sky light level to the given light level
	 *
	 * @param light level to set to
	 * @return this Block
	 * @throws NullPointerException
	 */
	public Block setSkyLight(byte level);

	/**
	 * Gets a controller associated with the block, or null if it has none.
	 *
	 * @return block controller
	 */
	public BlockController getController();

	/**
	 * Sets the entity associated with the block.
	 *
	 * @param controller to set to, or null to clear it
	 * @return this Block
	 */
	public Block setController(BlockController controller);

	/**
	 * Whether or not the block is associated with a block controller
	 *
	 * @return true if has a controller
	 */
	public boolean hasController();

	/**
	 * Performs a physics update on this block and the neighboring blocks
	 * 
	 * @return this Block
	 */
	public Block update();

	/**
	 * Performs a physics update on this block and/or neighboring blocks
	 * 
	 * @param True to update neighboring blocks, False to update only this block
	 * @return this Block
	 */
	public Block update(boolean around);

	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @return this Block
	 */
	public Block dynamicUpdate();

	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @param delay to wait before updating
	 * @return this Block
	 */
	public Block dynamicUpdate(long delay);
	
	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @param delay to wait before updating
	 * @param policy the insertion policy
	 * @return this Block
	 */
	public Block dynamicUpdate(long delay, InsertionPolicy policy);

	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @param delay to wait before updating
	 * @param hint parameter to use during the update
	 * @return this Block
	 */
	public Block dynamicUpdate(long delay, Object hint);
	
	/**
	 * Queues a dynamic update on this block<br>
	 * The Block Material must be dynamic for this to function.
	 * 
	 * @param delay to wait before updating
	 * @param policy the insertion policy
	 * @param hint parameter to use during the update
	 * @return this Block
	 */
	public Block dynamicUpdate(long delay, InsertionPolicy policy, Object hint);
}
