/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.spout.api.Spout;
import org.spout.api.event.world.RegionLoadEvent;
import org.spout.api.event.world.RegionUnloadEvent;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.util.map.concurrent.TSyncInt21TripleObjectHashMap;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.server.util.thread.snapshotable.SnapshotManager;

public class RegionSource implements Iterable<Region> {

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
	public SpoutRegion getRegionFromBlock(int x, int y, int z) {
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
	public SpoutRegion getRegionFromBlock(int x, int y, int z, boolean load) {
		int shifts = Region.REGION_SIZE_BITS + Chunk.CHUNK_SIZE_BITS;
		return getRegion(x >> shifts, y >> shifts, z >> shifts, load);
	}

	@DelayedWrite
	public void removeRegion(final SpoutRegion r) {
		if (!r.getWorld().equals(world)) {
			return;
		}

		// TODO - this should probably be in a separate scheduler stage, as it has to fire first

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
					
					Spout.getEventManager().callDelayedEvent(new RegionUnloadEvent(world, r));
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
	public SpoutRegion getRegion(int x, int y, int z) {
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
	 * @param load whether to load or generate the region if one does not exist
	 *            at the coordinates
	 * @return region
	 */
	@LiveRead
	public SpoutRegion getRegion(int x, int y, int z, boolean load) {
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
	 * @param load whether to load or generate the region if one does not exist
	 *            at the coordinates
	 * @param generate all the chunks inside of the region immediately
	 * @return region
	 */
	@LiveRead
	public SpoutRegion getRegion(int x, int y, int z, boolean load, boolean generate) {
		SpoutRegion region = (SpoutRegion) loadedRegions.get(x, y, z);

		if (region != null || !load) {
			return region;
		} else {
			region = new SpoutRegion(world, x, y, z, this);
			SpoutRegion current = (SpoutRegion) loadedRegions.putIfAbsent(x, y, z, region);

			if (current != null) {
				return current;
			} else {
				if (!region.getManager().getExecutor().startExecutor()) {
					throw new IllegalStateException("Unable to start region executor");
				}
				
				Spout.getEventManager().callDelayedEvent(new RegionLoadEvent(world, region));
				
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

	/**
	 * Gets an unmodifiable collection of all loaded regions.
	 *
	 * @return collection of all regions
	 */
	public Collection<Region> getRegions() {
		return Collections.unmodifiableCollection(loadedRegions.valueCollection());
	}

	@Override
	public Iterator<Region> iterator() {
		return getRegions().iterator();
	}
}
