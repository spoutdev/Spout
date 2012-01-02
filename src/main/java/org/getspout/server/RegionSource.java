package org.getspout.server;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.util.map.TInt24TripleObjectHashMap;

public class RegionSource {
	/**
	 * A map of loaded regions, mapped to their x and z values.
	 */
	private final TInt24TripleObjectHashMap<Region> loadedRegions = new TInt24TripleObjectHashMap<Region>(100);
	
	/**
	 * World associated with this region source
	 */
	private final World world;
	
	public RegionSource(World world) {
		this.world = world;
	}
	
	/**
	 * Gets the region associated with the block x, y, z coordinates
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return region, if it is loaded and exists
	 */
	public Region getRegionFromBlock(int x, int y, int z) {
		return getRegion(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS);
	}
	
	/**
	 * Gets the region associated with the region x, y, z coordinates
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return region, if it is loaded and exists
	 */
	public Region getRegion(int x, int y, int z) {
		return loadedRegions.get(x, y, z);
	}
	
	/**
	 * Gets the region associated with the region x, y, z coordinates <br/>
	 * 
	 * Will load or generate a region if requested.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param load whether to load or generate the region if one does not exist at the coordinates
	 * @return region
	 */
	public Region getRegion(int x, int y, int z, boolean load) {
		if (!load || loadedRegions.containsKey(x, y, z)) {
			return loadedRegions.get(x, y, z);
		}
		Region region = new SpoutRegion(world, x << Region.REGION_SIZE_BITS, y << Region.REGION_SIZE_BITS, z << Region.REGION_SIZE_BITS, this);
		loadedRegions.put(x,  y, z, region);
		return region;
	}
	
	/**
	 * True if there is a region loaded at the region x, y, z coordinates
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return true if there is a region loaded
	 */
	public boolean hasRegion(int x, int y, int z) {
		return loadedRegions.containsKey(x, y, z);
	}
	
	/**
	 * Unloads and returns the region, if one exists at the coordinates.
	 * 
	 * @param x the x coordinate
	 * @param y the x coordinate
	 * @param z the z coordinate
	 * @param save whether to save the region data
	 * @return region that was unloaded, or null if none existed
	 */
	public Region unloadRegion(int x, int y, int z, boolean save){
		Region region = loadedRegions.remove(x, y, z);
		if (region != null){
			region.unload(save);
		}
		return region;
	}

}
