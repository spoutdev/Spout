/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.component.entity;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import org.spout.api.ai.AStarMachine;
import org.spout.api.ai.pathfinder.BlockExaminer;
import org.spout.api.ai.pathfinder.Path;
import org.spout.api.ai.pathfinder.SpoutBlockSource;
import org.spout.api.ai.pathfinder.VectorGoal;
import org.spout.api.ai.pathfinder.VectorNode;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Vector3;
import org.spout.api.util.concurrent.SpinLock;

public class NavigationComponent extends EntityComponent {
	private final AStarMachine<VectorNode, Path> astar = AStarMachine.createWithDefaultStorage();
	private final Lock lock = new SpinLock();
	private Path plan;
	private Vector3 vector;
	private BlockExaminer[] defaultExaminers;

	public void setDefaultExaminers(BlockExaminer... blockExaminers) {
		this.defaultExaminers = Arrays.copyOf(blockExaminers, blockExaminers.length);
	}

	public void setDestination(Point dest) {
		lock.lock();
		try {
			Point current = getOwner().getScene().getPosition();
			plan = astar.runFully(new VectorGoal(dest), new VectorNode(current, new SpoutBlockSource(current),
					defaultExaminers), 10000);
			if (plan == null || plan.isComplete()) {
				// failed TODO: add an event
				plan = null;
			} else {
				vector = plan.getCurrentVector();
			}
		} finally {
			lock.unlock();
		}
	}

	public void stop() {
		plan = null;
	}

	@Override
	public void onTick(float dt) {
		lock.lock();
		try {
			if (plan == null || plan.isComplete()) {
				plan = null;
				return;
			}
			Transform transform = getOwner().getScene().getTransform();
			Point root = transform.getPosition();
			if (root.distanceSquared(vector) <= 12) {
				plan.update(getOwner());
				if (plan.isComplete()) {
					return;
				}
				vector = plan.getCurrentVector();
			}
			float dX = (vector.getX() - root.getX()) / 20;
			float dY = (vector.getY() - root.getY()) / 20;
			float dZ = (vector.getZ() - root.getZ()) / 20;
			transform.translate(dX, dY, dZ);
			//TODO Rotation
			getOwner().getScene().setTransform(transform);
		} finally {
			lock.unlock();
		}
	}

	public boolean isNavigating() {
		lock.lock();
		try {
			return plan != null;
		} finally {
			lock.unlock();
		}
	}
}