/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.engine.scheduler.parallel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.scheduler.Task;
import org.spout.api.scheduler.TickStage;
import org.spout.engine.scheduler.SpoutTask;
import org.spout.engine.scheduler.SpoutTaskManager;
import org.spout.engine.world.SpoutRegion;

public class ParallelTaskInfo {

	public static final ParallelTaskInfo[] EMPTY_ARRAY = new ParallelTaskInfo[0];

	private final Set<RegionIdPair> children = new HashSet<RegionIdPair>();

	private final Map<SpoutRegion, SpoutTask> regions = new HashMap<SpoutRegion, SpoutTask>();

	private final SpoutTask task;

	private boolean alive = true;

	public ParallelTaskInfo(SpoutTask task) {
		this.task = task;
	}

	public synchronized boolean add(SpoutRegion region) {
		if (regions.containsKey(region)) {
			return false;
		}

		if (!alive) {
			return false;
		}

		SpoutTask newTask = task.getRegionTask(region);
		if (newTask == null) {
			Spout.getLogger().info("Unable to create parallel task for " + task);
		}

		int newId = ((SpoutTaskManager) region.getTaskManager()).schedule(newTask);
		children.add(new RegionIdPair(newId, region));
		regions.put(region, newTask);
		return true;
	}

	public synchronized boolean remove(SpoutRegion region) {
		TickStage.checkStage(TickStage.TICKSTART);
		if (!regions.containsKey(region)) {
			return false;
		}

		boolean success = false;
		final Iterator<RegionIdPair> itr = children.iterator();
		while (itr.hasNext()) {
			RegionIdPair pair = itr.next();
			if (pair.getRegion() == region) {
				success = true;
				itr.remove();
			}
		}

		if (!success) {
			throw new IllegalStateException("Region exists in region map and but not in children map");
		}

		if (regions.remove(region) == null) {
			throw new IllegalStateException("Region exists in region map with contains but cannot be removed");
		}

		return true;
	}

	public synchronized Task getTask(Region r) {
		return regions.get(r);
	}

	public synchronized void stop() {
		alive = false;
		for (RegionIdPair regionId : children) {
			SpoutRegion r = regionId.getRegion();
			if (r != null) {
				r.getTaskManager().cancelTask(regionId.getTaskId());
			}
		}
	}

	public SpoutTask getTask() {
		return task;
	}

}