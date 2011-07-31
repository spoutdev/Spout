package org.getspout.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface ContribBlock extends Block{
	
	/**
	 * Set's the block type safely, appearing instantly and seamlessly to all players
	 * Can be used on main thread, but recommended for async threads
	 * @param type to set the block to
	 */
	public void setTypeAsync(Material type);
	
	/**
	 * Set's the block type id safely, appearing instantly and seamlessly to all players
	 * Can be used on main thread, but recommended for async threads
	 * @param type id to set the block to
	 */
	public void setTypeIdAsync(int type);
	
	/**
	 * Set's the block data safely, appearing instantly and seamlessly to all players
	 * Can be used on main thread, but recommended for async threads
	 * @param data to set the block to
	 */
	public void setDataAsync(byte data);
	
	/**
	 * Set's the block type id and data safely, appearing instantly and seamlessly to all players
	 * Can be used on main thread, but recommended for async threads
	 * @param type to set the block to
	 * @param data to set the block to
	 */
	public void setTypeIdAndDataAsync(int type, byte data);
	
	@Override
	public ContribBlock getFace(BlockFace face);
	
	@Override
	public ContribBlock getFace(BlockFace face, int distance);
	
	@Override
	public ContribBlock getRelative(int modX, int modY, int modZ);
	
	@Override
	public ContribBlock getRelative(BlockFace face);
	
	@Override
	public ContribChunk getChunk();

}
