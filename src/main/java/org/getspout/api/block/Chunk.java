/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.block;

import org.getspout.api.World;
import org.getspout.api.entity.Entity;
import org.getspout.api.material.CustomBlockMaterial;

public interface Chunk {

	/**
	 * Gets a block from this chunk
	 *
	 * @param x 0-15
	 * @param y 0-127
	 * @param z 0-15
	 * @return the Block
	 */
	public Block getBlockAt(int x, int y, int z);

	/**
	 * Gets the world containing this chunk
	 *
	 * @return Parent World
	 */
	public World getWorld();

	/**
	 * Gets the X-coordinate of this chunk
	 *
	 * @return X-coordinate
	 */
	public int getX();

	/**
	 * Gets the Z-coordinate of this chunk
	 *
	 * @return Z-coordinate
	 */
	public int getZ();

	/**
	 * Checks if the chunk is loaded.
	 *
	 * @return True if it is loaded.
	 */
	public boolean isLoaded();

	/**
	 * Loads the chunk.
	 *
	 * @return true if the chunk has loaded successfully, otherwise false
	 */
	public boolean load();


	/**
	 * Loads the chunk.
	 *
	 * @param generate Whether or not to generate a chunk if it doesn't already exist
	 * @return true if the chunk has loaded successfully, otherwise false
	 */
	public boolean load(boolean generate);

	/**
	 * Unloads and optionally saves the Chunk
	 *
	 * @return true if the chunk has unloaded successfully, otherwise false
	 */	 
	public boolean unload();

	/**
	 * Unloads and optionally saves the Chunk
	 *
	 * @param save Controls whether the chunk is saved
	 * @return true if the chunk has unloaded successfully, otherwise false
	 */
	public boolean unload(boolean save);

	/**
	 * Unloads and optionally saves the Chunk
	 *
	 * @param save Controls whether the chunk is saved
	 * @param safe Controls whether to unload the chunk when players are nearby
	 * @return true if the chunk has unloaded successfully, otherwise false
	 */
	public boolean unload(boolean save, boolean safe);

	/**
	 * Get a list of all entities in the chunk.
	 * @return The entities.
	 */
	public Entity[] getEntities();
	
	/**
	 * Gets the custom block ids that are used for the chunk at (x, z).
	 * 
	 * It may be null if there are no custom block ids.
	 * 
	 * Modifying this array <b>will</b> change the contents of this chunk.
	 * 
	 * @return custom block ids
	 */
	public short[] getCustomBlockIds();

	/**
	 * Sets the custom block ids that are used for the chunk at (x, z).
	 * 
	 * This array should be 32768 in length.
	 * 
	 * Modifying this array will <b>override</b> the contents of this chunk.
	 * 
	 * @param ids the custom block ids
	 */
	public void setCustomBlockIds(short[] ids);
	
	/**
	 * Gets the custom block id at this x, y, z location.
	 * 
	 * If no custom block exists, it will return zero,
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return custom block id
	 */
	public short getCustomBlockId(int x, int y, int z);
	
	/**
	 * Sets the custom block id at this x, y, z location
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param id to set
	 * @return the previous id at the location
	 */
	public short setCustomBlockId(int x, int y, int z, short id);
	
	/**
	 * Sets the custom block at this x, y, z location
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param custom block to set
	 * @return the previous custom block at the location, or null if none existed.
	 */
	public CustomBlockMaterial setCustomBlockId(int x, int y, int z, CustomBlockMaterial block);

}
