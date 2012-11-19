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
	@Override
	public boolean isDetachable() {
		return false;
	}

	/**
	 * Gets the friction of the entity.
	 * @return the friction
	 */
	public abstract float getFriction();

	/**
	 * Sets the friction of the entity.
	 * @param friction new friction of the object
	 */
	public abstract void setFriction(float friction);

	/**
	 * Gets the mass of the entity.
	 * @return the mass
	 */
	public abstract float getMass();

	/**
	 * Sets the mass of the entity.
	 * @param mass new mass of the entity
	 */
	public abstract void setMass(float mass);

	/**
	 * Gets the collision object which holds the collision shape and is used to calculate physics such as velocity, intertia,
	 * etc. All PhysicsComponents are guaranteed to have a valid object.
	 * @return the CollisionObject
	 */
	public abstract CollisionObject getCollisionObject();

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
	 * Gets the linear velocity.
	 * @return the linear velocity
	 */
	public abstract Vector3 getLinearVelocity();

	public abstract void applyImpulse(Vector3 impulse);

	public abstract void applyImpulse(Vector3 impulse, Vector3 relativePos);

	public abstract void applyForce(Vector3 impulse);

	public abstract void applyForce(Vector3 force, Vector3 relativePos);

	/**
	 * Returns if both the angular and linear velocities have changed "dirty" since the last tick.
	 * @return true if dirty, false if not
	 */
	public abstract boolean isVelocityDirty();
}