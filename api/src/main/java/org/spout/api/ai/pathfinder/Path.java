/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.api.ai.pathfinder;

import java.util.List;

import com.google.common.collect.Lists;

import org.spout.api.ai.Plan;
import org.spout.api.ai.pathfinder.PathPoint.PathCallback;
import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.math.vector.Vector3;

public class Path implements Plan<Entity> {
	private int index = 0;
	private final PathEntry[] path;

	Path(Iterable<VectorNode> unfiltered) {
		this.path = cull(unfiltered);
	}

	private PathEntry[] cull(Iterable<VectorNode> unfiltered) {
		// TODO: possibly expose cullability in an API
		List<PathEntry> path = Lists.newArrayList();
		for (VectorNode node : unfiltered) {
			if (node.callbacks != null) {
				continue;
			}
			Vector3 vector = node.location;
			path.add(new PathEntry(vector, node.callbacks));
		}
		return path.toArray(new PathEntry[path.size()]);
	}

	public Vector3 getCurrentVector() {
		return path[index].point;
	}

	@Override
	public boolean isComplete() {
		return index >= path.length;
	}

	@Override
	public void update(Entity entity) {
		if (isComplete()) {
			return;
		}
		PathEntry entry = path[index];
		if (entry.hasCallbacks()) {
			Block block = entry.getBlockUsingWorld(entity.getWorld());
			for (PathCallback callback : entry.callbacks) {
				callback.run(entity, block);
			}
		}
		++index;
	}

	private static class PathEntry {
		final Iterable<PathCallback> callbacks;
		final Vector3 point;

		private PathEntry(Vector3 point, List<PathCallback> callbacks) {
			this.point = point;
			this.callbacks = callbacks;
		}

		private Block getBlockUsingWorld(World world) {
			return world.getBlock(point.getX(), point.getY(), point.getZ());
		}

		private boolean hasCallbacks() {
			return callbacks != null;
		}
	}
}