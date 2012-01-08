package org.getspout.server;

import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.util.map.concurrent.TSyncInt21TripleObjectHashMap;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;

public class RegionSource {

	/**
	 * A map of loaded regions, mapped to their x and z values.
	 */
	private final TSyncInt21TripleObjectHashMap<Region> loadedRegions;
	/**
	 * World associated with this region source
	 */
	private final SpoutWorld world;
	
	public RegionSource(SpoutWorld world, SnapshotManager snapshotManager) {
		this.world = world;
		loadedRegions = new TSyncInt21TripleObjectHashMap<Region>();
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
		return getRegionFromBlock(x, y, z, false);
	}

	/**
	 * Gets the region associated with the block x, y, z coordinates
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param load to load the region
	 * @return region, if it is loaded and exists
	 */
	@LiveRead
	public Region getRegionFromBlock(int x, int y, int z, boolean load) {
		int shifts = (Region.REGION_SIZE_BITS + Chunk.CHUNK_SIZE_BITS);
		return getRegion(x >> shifts, y >> shifts, z >> shifts, load);
	}

	@DelayedWrite
	public void removeRegion(final SpoutRegion r) {
		if (!r.getWorld().equals(world)) {
			return;
		}
		
		// removeRegion is called during snapshot copy on the Region thread (when the last chunk is removed)
		// Needs re-syncing to a safe moment
		world.getServer().getScheduler().scheduleAsyncDelayedTask(null, new Runnable() {
			public void run() {
				int x = r.getX();
				int y = r.getY();
				int z = r.getZ();
				boolean success = loadedRegions.remove(x, y, z, r);
				if (success) {
					r.getManager().getExecutor().haltExecutor();
				}
			}
		});
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
	public Region getRegion(int x, int y, int z) {
		return getRegion(x, y, z, false);
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
		return getRegion(x, y, z, load, false);
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
	 * @param generate all the chunks inside of the region immediately
	 * @return region
	 */
	@LiveRead
	public Region getRegion(int x, int y, int z, boolean load, boolean generate) {
		Region region = loadedRegions.get(x, y, z);

		if (region != null || !load) {
			return region;
		} else {
			region = new SpoutRegion(world, x, y, z, this);
			Region current = loadedRegions.putIfAbsent(x, y, z, region);

			if (current != null) {
				return current;
			} else {
				if (!((SpoutRegion) region).getManager().getExecutor().startExecutor()) {
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
	@LiveRead
	public boolean hasRegion(int x, int y, int z) {
		return loadedRegions.get(x, y, z) != null;
	}

}
