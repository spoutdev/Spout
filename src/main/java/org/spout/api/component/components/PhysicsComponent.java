/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.component.components;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.MotionState;
import org.spout.api.math.Vector3;

public abstract class PhysicsComponent extends EntityComponent {
	private float mass;

	@Override
	public boolean isDetachable() {
		return false;
	}

	/**
	 * Gets the mass of the entity.
	 * @return the mass
	 */
	public float getMass() {
		return mass;
	}

	/**
	 * Sets the mass of the entity.
	 * @param mass new mass of the entity
	 */
	public void setMass(float mass) {
		this.mass = mass;
	}

	/**
	 * Gets the collision object which holds the collision shape and is used to calculate physics such as velocity, intertia,
	 * etc. All PhysicsComponents are guaranteed to have a valid object.
	 * @return the CollisionObject
	 */
	public abstract CollisionObject getCollisionObject();

	/**
	 * Gets the live collision object. Caution must be used as it is not stable and is subject to changes from plugins.
	 * @return the live CollisionObject
	 */
	public abstract CollisionObject getCollisionObjectLive();

	/**
	 * Sets the live CollisionObject.
	 * @param collisionObject the new live CollisionObject
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public abstract void setCollisionObject(CollisionObject collisionObject);

	/**
	 * Gets the MotionState. A MotionState is the "bridge" between Bullet and Spout in-which Bullet tells Spout that the
	 * object has moved and to update your transforms accordingly.
	 * @return the MotionState
	 */
	public abstract MotionState getMotionState();

	/**
	 * Gets the shape used to define the volume. Shapes range anywhere from a standard AABB bounding box to a basic
	 * sphere shape. The shape is important as it is used to calculate collisions, inertia, and other characteristics.
	 * @return The CollisionShape
	 */
	public abstract CollisionShape getCollisionShape();

	/**
	 * Sets the shape of the volume.
	 * @param shape the new CollisionShape
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public abstract void setCollisionShape(CollisionShape shape);

	/**
	 * Gets the angular velocity.
	 * @return the angular velocity
	 */
	public abstract Vector3 getAngularVelocity();

	/**
	 * Gets the live angular velocity. Caution must be used as it is not stable and is subject to changes from
	 * plugins.
	 * @return the live angular velocity
	 */
	public abstract Vector3 getAngularVelocityLive();

	/**
	 * Gets the linear velocity.
	 * @return the linear velocity
	 */
	public abstract Vector3 getLinearVelocity();

	/**
	 * Gets the live linear velocity. Caution must be used as it is not stable and is subject to changes from
	 * plugins.
	 * @return the live linear velocity
	 */
	public abstract Vector3 getLinearVelocityLive();

	/**
	 * Sets the live angular velocity.
	 * @param velocity the new angular velocity
	 */
	public abstract void setAngularVelocity(Vector3 velocity);

	/**
	 * Sets the live linear velocity.
	 * @param velocity the new linear velocity
	 */
	public abstract void setLinearVelocity(Vector3 velocity);

	/**
	 * Sets both the live angular and linear velocities.
	 * @param velocity the new live angular and linear velocity
	 */
	public abstract void setVelocity(Vector3 velocity);

	/**
	 * Returns if both the angular and linear velocities have changed "dirty" since the last tick.
	 * @return true if dirty, false if not
	 */
	public abstract boolean isVelocityDirty();

	/**
	 * Returns if the CollisionObject has changed "dirty" since the last tick
	 * @return true if dirty, false if not
	 */
	public abstract boolean isCollisionObjectDirty();
}