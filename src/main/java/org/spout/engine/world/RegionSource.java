/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.world;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.event.world.RegionLoadEvent;
import org.spout.api.event.world.RegionUnloadEvent;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.scheduler.TaskManager;
import org.spout.api.scheduler.TickStage;
import org.spout.api.util.map.concurrent.TripleIntObjectMap;
import org.spout.api.util.map.concurrent.TripleIntObjectReferenceArrayMap;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.LiveRead;
import org.spout.engine.scheduler.SpoutParallelTaskManager;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;

public class RegionSource implements Iterable<Region> {
	private final static int REGION_MAP_BITS = 5;

	private final static AtomicInteger regionsLoaded = new AtomicInteger(0);
	private final static AtomicInteger warnThreshold = new AtomicInteger(Integer.MAX_VALUE);
	/**
	 * A map of loaded regions, mapped to their x and z values.
	 */
	private final TripleIntObjectMap<Region> loadedRegions;
	/**
	 * World associated with this region source
	 */
	private final SpoutWorld world;

	public RegionSource(SpoutWorld world) {
		this.world = world;
		loadedRegions = new TripleIntObjectReferenceArrayMap<Region>(REGION_MAP_BITS);
	}

	@DelayedWrite
	public void removeRegion(final SpoutRegion r) {
		TickStage.checkStage(TickStage.SNAPSHOT);
		
		if (!r.getWorld().equals(world)) {
			return;
		}

		// removeRegion is called during snapshot copy on the Region thread (when the last chunk is removed)
		// Needs re-syncing to a safe moment
		(world.getEngine().getScheduler()).scheduleCoreTask(new Runnable() {
			@Override
			public void run() {
				if (r.isEmpty()) {
					if (r.attemptClose()) {
						int x = r.getX();
						int y = r.getY();
						int z = r.getZ();
						boolean success = loadedRegions.remove(x, y, z, r);
						if (success) {
							if (!world.getEngine().getScheduler().removeAsyncManager(r)) {
								throw new IllegalStateException("Failed to de-register the region from the scheduler");
							}
							TaskManager tm = Spout.getEngine().getParallelTaskManager();
							SpoutParallelTaskManager ptm = (SpoutParallelTaskManager)tm;
							ptm.unRegisterRegion(r);

							TaskManager tmWorld = world.getParallelTaskManager();
							SpoutParallelTaskManager ptmWorld = (SpoutParallelTaskManager)tmWorld;
							ptmWorld.unRegisterRegion(r);

							if (regionsLoaded.decrementAndGet() < 0) {
								Spout.getLogger().info("Regions loaded dropped below zero");
							}

							Spout.getEventManager().callDelayedEvent(new RegionUnloadEvent(world, r));
							
							r.unlinkNeighbours();
						} else {
							Spout.getLogger().info("Tried to remove region " + r + " but region removal failed");
						}
					} else {
						Spout.getLogger().info("Unable to close region file, streams must be open");
					}
				} else {
					Spout.getLogger().info("Region was not empty when attempting to remove, active chunks returns " + r.getNumLoadedChunks());
				}
			}
		});
	}

	/**
	 * Gets the region associated with the region x, y, z coordinates <br/>
	 * <p>
	 * Will load or generate a region if requested.
	 * @param x    the x coordinate
	 * @param y    the y coordinate
	 * @param z    the z coordinate
	 * @param loadopt whether to load or generate the region if one does not exist
	 *             at the coordinates
	 * @return region
	 */
	@LiveRead
	// TODO RegionSource no longer generates regions; make this more of a client/server mold
	public SpoutRegion getRegion(int x, int y, int z, LoadOption loadopt) {
		if (loadopt != LoadOption.NO_LOAD) {
			TickStage.checkStage(~TickStage.SNAPSHOT);
		}
		
		SpoutRegion region = (SpoutRegion) loadedRegions.get(x, y, z);

		if (region != null) {
			return region;
		}

		if (!loadopt.loadIfNeeded()) {
			return null;
		}

		/* If not generating region, and it doesn't exist yet, we're done */
		if ((!loadopt.generateIfNeeded()) && (!SpoutRegion.regionFileExists(world, x, y, z))) {
			return null;
		}

		region = new SpoutRegion(world, x, y, z, this);
		SpoutRegion current = (SpoutRegion) loadedRegions.putIfAbsent(x, y, z, region);

		if (current != null) {
			return current;
		}
		
		((SpoutScheduler)Spout.getScheduler()).addAsyncManager(region);

		int threshold = warnThreshold.get();
		if (regionsLoaded.getAndIncrement() > threshold) {
			Spout.getLogger().info("Warning: number of spout regions exceeds " + threshold + " when creating (" +
                x + ", " + y + ", " + z + ")");
			Thread.dumpStack();
			warnThreshold.addAndGet(10);
		}

		TaskManager tm = Spout.getEngine().getParallelTaskManager();
		SpoutParallelTaskManager ptm = (SpoutParallelTaskManager)tm;
		ptm.registerRegion(region);

		TaskManager tmWorld = world.getParallelTaskManager();
		SpoutParallelTaskManager ptmWorld = (SpoutParallelTaskManager)tmWorld;
		ptmWorld.registerRegion(region);

		Spout.getEventManager().callDelayedEvent(new RegionLoadEvent(world, region));

		return region;
	}

	/**
	 * True if there is a region loaded at the region x, y, z coordinates
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
