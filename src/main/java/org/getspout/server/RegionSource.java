package org.getspout.server;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableConcurrentTripleIntHashMap;

public class RegionSource {
	
	/**
	 * The snapshot manager
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * A map of loaded regions, mapped to their x and z values.
	 */	
	private final SnapshotableConcurrentTripleIntHashMap<Region> loadedRegions = new SnapshotableConcurrentTripleIntHashMap<Region>(snapshotManager);

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
	@SnapshotRead
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
	@SnapshotRead
	public Region getRegion(int x, int y, int z) {
		return loadedRegions.get(x, y, z);
	}
	
	/**
	 * Gets the region associated with the region x, y, z coordinates
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return region, if it is loaded and exists
	 */
	@LiveRead
	public Region getLiveRegion(int x, int y, int z) {
		return loadedRegions.getLive(x, y, z);
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
	@LiveRead
	public Region getRegion(int x, int y, int z, boolean load) {
		Region region = loadedRegions.getLive(x, y, z);
		
		if (region != null || !load) {
			return region;
		} else {
			region = new SpoutRegion(world, x << Region.REGION_SIZE_BITS, y << Region.REGION_SIZE_BITS, z << Region.REGION_SIZE_BITS, this);
			Region current = loadedRegions.putIfAbsent(x,  y, z, region);
			
			if (current != null) {
				return current;
			} else {
				if (!((SpoutRegion)region).getManager().getExecutor().startExecutor()) {
					throw new IllegalStateException("Unable to start region executor");
				}
				return region;
			}
		}
	}
	
	/**
	 * True if there is a region loaded at the region x, y, z coordinates
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return true if there is a region loaded
	 */
	@SnapshotRead
	public boolean hasRegion(int x, int y, int z) {
		return loadedRegions.get(x, y, z) != null;
	}
	
	/**
	 * True if there is a region loaded at the region x, y, z coordinates
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return true if there is a region loaded
	 */
	@LiveRead
	public boolean hasRegionLive(int x, int y, int z) {
		return loadedRegions.getLive(x, y, z) != null;
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
		Region region = loadedRegions.getLive(x, y, z);

		if (region == null) {
			return null;
		} else {
			((SpoutRegion)region).getManager().getExecutor().haltExecutor();
			
			if (save) {
				((SpoutRegion)region).save();
			}
			
			loadedRegions.remove(x, y, z, region);

			return region;
		}
	}

}
