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
package org.spout.engine.world.collision;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.voxel.CollisionSnapshot;
import com.bulletphysics.collision.shapes.voxel.VoxelPhysicsWorld;

import org.spout.api.collision.CollisionStrategy;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;

import org.spout.engine.world.SpoutBlock;
import org.spout.engine.world.SpoutRegion;

public class SpoutPhysicsWorld implements VoxelPhysicsWorld {

	private final SpoutRegion simulation;

	public SpoutPhysicsWorld(SpoutRegion simulation) {
		if (simulation == null) {
			throw new IllegalArgumentException("Region cannot be null for physics!");
		}
		this.simulation = simulation;
	}

	@Override
	public CollisionSnapshot getCollisionShapeAt(int x, int y, int z) {
		final SpoutBlock block = (SpoutBlock) simulation.getBlock(x, y, z);
		return new SpoutVoxelCollisionSnapshot(block.getMaterial(), simulation, block.getPosition());
	}

	private static class SpoutVoxelCollisionSnapshot implements CollisionSnapshot {

		private final boolean isColliding, isBlocking;
		private final CollisionShape shape;
		private final Vector3 position;
		private final Region region;

		public SpoutVoxelCollisionSnapshot(BlockMaterial material, Region region, Vector3 position) {
			this.shape = material.getCollisionShape();
			this.isColliding = shape != null && material.getCollisionModel().getStrategy() != CollisionStrategy.NOCOLLIDE;
			this.isBlocking = shape != null && material.getCollisionModel().getStrategy() == CollisionStrategy.SOLID;
			this.position = position;
			this.region = region;
		}

		@Override
		public boolean isColliding() {
			return isColliding;
		}

		@Override
		public Object getUserData() {
			return region.getBlock(position);
		}

		@Override
		public CollisionShape getCollisionShape() {
			return shape;
		}

		@Override
		public Vector3f getCollisionOffset() {
			//This is the offset of the material shape from the center of the voxel (for a cube of 1, 1, 1 in size, this should be 0, 0, 0)
			return new Vector3f(0, 0, 0);
		}

		@Override
		public boolean isBlocking() {
			return isBlocking;
		}
	}

}