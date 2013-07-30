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

import org.spout.api.ai.AStarNode;
import org.spout.math.vector.Vector3;

public class VectorNode extends AStarNode implements PathPoint {
	private float blockCost = -1;
	final BlockSource blockSource;
	List<PathCallback> callbacks;
	private final BlockExaminer[] examiners;
	final Vector3 location;

	public VectorNode(Vector3 location, BlockSource source, BlockExaminer... examiners) {
		this.location = location; // TODO: possibly set to blockX values.
		this.blockSource = source;
		this.examiners = examiners == null ? new BlockExaminer[] {} : examiners;
	}

	@Override
	public void addCallback(PathCallback callback) {
		if (callbacks == null) {
			callbacks = Lists.newArrayList();
		}
		callbacks.add(callback);
	}

	boolean at(Vector3 goal) {
		return location.distanceSquared(goal) <= 10;
	}

	@Override
	public Path buildPlan() {
		Iterable<VectorNode> parents = getParents();
		return new Path(parents);
	}

	public float distance(VectorNode to) {
		return (float) location.distanceSquared(to.location);
	}

	private float getBlockCost() {
		if (blockCost == -1) {
			blockCost = 0;
			for (BlockExaminer examiner : examiners) {
				blockCost += examiner.getCost(blockSource, this);
			}
		}
		return blockCost;
	}

	@Override
	public Iterable<AStarNode> getNeighbours() {
		List<AStarNode> nodes = Lists.newArrayList();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					Vector3 mod = location.add(x, y, z);
					if (mod.equals(location)) {
						continue;
					}
					VectorNode sub = getNewNode(mod);
					if (!isPassable(sub)) {
						continue;
					}
					nodes.add(sub);
				}
			}
		}
		return nodes;
	}

	private VectorNode getNewNode(Vector3 mod) {
		return new VectorNode(mod, blockSource, examiners);
	}

	public float heuristicDistance(Vector3 goal) {
		return (float) location.distanceSquared(goal) + getBlockCost();
	}

	private boolean isPassable(PathPoint mod) {
		for (BlockExaminer examiner : examiners) {
			boolean passable = examiner.isPassable(blockSource, mod);
			if (!passable) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Vector3 getVector() {
		return location;
	}
}