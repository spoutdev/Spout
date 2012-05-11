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
package org.spout.api.geo.cuboid;

import org.spout.api.Source;
import org.spout.api.entity.BlockController;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.block.BlockFace;
import org.spout.api.material.source.DataSource;
import org.spout.api.material.source.MaterialSource;
import org.spout.api.material.source.MaterialState;
import org.spout.api.math.Vector3;

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
}
