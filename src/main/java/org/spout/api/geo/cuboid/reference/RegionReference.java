package org.spout.api.geo.cuboid.reference;

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;

public interface RegionReference {
	
	/**
	 * Gets the Region referred to by this reference, loading if required
	 * 
	 * @return
	 */
	public Region get();
	
	/**
	 * Gets the Region referred to by this reference
	 * 
	 * @param loadopt to control whether to load and/or generate the region, if needed
	 * @return
	 */
	public Region get(LoadOption loadOpt);
	
	/**
	 * Gets the block at the given packed location
	 * 
	 * @param packed the coords in packed int form
	 * @return
	 */
	public BlockMaterial getBlockMaterial(int packed);
	
	/**
	 * Gets the block at the given packed coords
	 * 
	 * @param packed the coords in packed int form
	 * @param loadopt to control whether to load and/or generate the region/chunk, if needed
	 * @return
	 */
	public BlockMaterial getBlockMaterial(int packed, LoadOption loadOpt);
	
	/**
	 * Sets the block at the given packed coords
	 * 
	 * @param packed the coords in packed int form
	 * @param material the block material
	 * @return
	 */
	public BlockMaterial setBlockMaterial(int packed, BlockMaterial material);
	
	/**
	 * Gets the block at the given packed coords
	 * 
	 * @param packed the coords in packed int form
	 * @param material the block material
	 * @param loadopt to control whether to load and/or generate the region/chunk, if needed
	 * @return
	 */
	public BlockMaterial setBlockMaterial(int packed, BlockMaterial material, LoadOption loadOpt);
	
	/**
	 * Gets the int packed location for the given coordinates.  The coordinates must 
	 * be in the region or one of its 26 neighbours for correction operation.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public int getPackedCoords(int x, int y, int z);
	
}
