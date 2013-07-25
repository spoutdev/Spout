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
package org.spout.engine.world.collision;

import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.engine.world.SpoutRegion;
import org.spout.physics.body.ImmobileRigidBody;
import org.spout.physics.body.RigidBodyMaterial;
import org.spout.physics.collision.shape.CollisionShape;
import org.spout.physics.engine.linked.LinkedWorldInfo;
import org.spout.physics.math.Matrix3x3;

public final class SpoutLinkedWorldInfo implements LinkedWorldInfo {
	private SpoutRegion region;

	public SpoutLinkedWorldInfo(SpoutRegion region) {
		this.region = region;
	}

	@Override
	public ImmobileRigidBody getBody(int x, int y, int z) {
		final Block block = region.getBlock(x - 0.5f, y - 0.5f, z - 0.5f);
		final BlockMaterial material = block.getMaterial();
		final CollisionShape shape = material.getShape();
		if (shape == null) {
			return null;
		}
		final Matrix3x3 inertiaTensorLocal = new Matrix3x3();
		final float mass = material.getMass();
		shape.computeLocalInertiaTensor(inertiaTensorLocal, mass);
		final ImmobileRigidBody body = new ImmobileRigidBody(new org.spout.physics.math.Transform(new org.spout.physics.math.Vector3(x + 0.5f, y + 0.5f, z + 0.5f),
				new org.spout.physics.math.Quaternion(0, 0, 0, 1)), mass, inertiaTensorLocal, shape, region.getSimulation().getNextFreeID());
		body.setMaterial(new RigidBodyMaterial(material.getRestitution(), material.getFriction()));
		body.setUserPointer(block); //It is safe to use block as these bodies are destroyed at the end of the physics tick
		return body;
	}
}
