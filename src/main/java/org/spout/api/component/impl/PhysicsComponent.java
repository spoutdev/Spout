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
package org.spout.api.component.impl;

import com.bulletphysics.collision.shapes.CollisionShape;

import org.spout.api.component.type.EntityComponent;
import org.spout.api.math.Vector3;

public abstract class PhysicsComponent extends EntityComponent {
	/**
	 * Gets the restitution of the entity.
	 * <p>
	 * Note: Values are greater than or equal to zero
	 * </p>
	 * @return the restitution
	 */
	public abstract float getRestitution();

	/**
	 * Sets the restitution of the entity.
	 * <p>
	 * Restitution is the amount of restoring force applied when an object
	 * encounters a barrier. 
	 * <br><br>
	 * Ex: A restitution value of 1 will result in an equal resulting
	 * force when colliding with an object. A value of 2 will result in
	 * a double force when colliding, whereas a value of 0.5F will result
	 * in half the force.
	 * </p>
	 * @param restitution new restitution of the object
	 */
	public abstract void setRestitution(float restitution);

	/**
	 * Gets the angular damping of the entity.
	 * <p>
	 * Angular damping is how the body rotates through the world.
	 * Values are between 0F and 1F (inclusive) and values > 0F of angular damping 
	 * will cause bodies cease rotation more quickly than if just affected by friction.
	 * </p>
	 * @return the angular damping
	 */
	public abstract float getAngularDamping();

	/**
	 * Gets the linear damping of the entity.
	 * <p>
	 * Linear damping is how the body moves through the world at any given direction.
	 * Values are between 0F and 1F (inclusive) and values > 0F of linear damping 
	 * will cause bodies to come to rest more quickly than if just affected by friction.
	 * </p>
	 * @return the linear damping
	 */
	public abstract float getLinearDamping();

	/**
	 * Sets the damping of the entity.
	 * <p>
	 * For more on linear damping and angular damping, see {@link #getLinearDamping()} and {@link #getAngularDamping()}.
	 * </p>
	 * @param linearDamping new linear damping of the object
	 * @param angularDamping new angular damping of the object
	 */
	public abstract void setDamping(float linearDamping, float angularDamping);

	/**
	 * Gets the friction of the entity.
	 * <p>
	 * Friction values are between -10F and 10F (inclusive). Friction is similar to linear
	 * and angular damping, except that it only has effect when a body is in contact with another
	 * body. The default value is 0.5F.
	 * <p>
	 * @return the friction
	 */
	public abstract float getFriction();

	/**
	 * Sets the friction of the entity.
	 * <p>
	 * See {@link #getFriction()} for details on friction.
	 * </p>
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

	/**
	 * Sets the angular velocity.
	 * @param velocity the angular velocity
	 */
	public abstract void setAngularVelocity(Vector3 velocity);

	/**
	 * Sets the linear velocity.
	 * @param velocity the linear velocity
	 */
	public abstract void setLinearVelocity(Vector3 velocity);

	public abstract void applyImpulse(Vector3 impulse);

	public abstract void applyImpulse(Vector3 impulse, Vector3 relativePos);

	public abstract void applyForce(Vector3 impulse);

	public abstract void applyForce(Vector3 force, Vector3 relativePos);

	/**
	 * Returns if both the angular and linear velocities have changed "dirty" since the last tick.
	 * @return true if dirty, false if not
	 */
	public abstract boolean isVelocityDirty();

	/**
	 * Returns if both the angular and linear velocities have changed "dirty" since the last tick.
	 * @return true if dirty, false if not
	 */
	public abstract boolean isAngularVelocityDirty();

	/**
	 * Returns if both the angular and linear velocities have changed "dirty" since the last tick.
	 * @return true if dirty, false if not
	 */
	public abstract boolean isLinearVelocityDirty();
}