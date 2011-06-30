package org.bukkitcontrib.block;

import org.bukkit.Chunk;

public interface ContribChunk extends Chunk{
	/**
	 *  Checks if this Chunk is loaded
	 * @return loaded
	 */
    public boolean isLoaded();

    /**
     * Loads this chunk
     * @return chunk loaded
     */
    public boolean load();

    /**
     * Loads this chunk and generates it if it is not generated
     * @param generate 
     * @return chunk loaded
     */
    public boolean load(boolean generate);

    /**
     * Unloads this chunk from memory, saving the data and changes
     * @return unloaded
     */
    public boolean unload();

    /**
     * Unloads this chunk from memory
     * @param save or not to save
     * @return unloaded
     */
    public boolean unload(boolean save);

    /**
     * Unloads this chunk from memory
     * @param save or not to save
     * @param safe unloading, or instant unloading
     * @return unloaded
     */
    public boolean unload(boolean save, boolean safe);
}
