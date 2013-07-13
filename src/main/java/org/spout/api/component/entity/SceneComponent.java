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

import org.spout.api.ClientOnly;
import org.spout.api.collision.BoundingBox;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.thread.annotation.SnapshotRead;
import org.spout.api.util.thread.annotation.Threadsafe;

/**
 * Component that gives the owner the characteristics to be a part of a Scene.
 * <p>
 * A Scene consists of {@link Transform}s which represent the snapshot state, the live state, and
 * the rendering state. This component can be used to manipulate the object within the scene.
 */
public abstract class SceneComponent extends EntityComponent {

	// Transform

	/**
	 * Gets the {@link Transform} this {@link org.spout.api.entity.Entity} had within the last game tick
	 * of the scene.
	 * <p>
	 * The Transform is stable, it is completely impossible for it to be updated.
	 * </p>
	 * @return The Transform as of the last game tick.
	 */
	@SnapshotRead
	public abstract Transform getTransform();

	/**
	 * Gets the {@link Transform} this {@link org.spout.api.entity.Entity} had within the last game tick
	 * of the scene with interpolation applied (so it appears smooth to users).
	 * <p>
	 * The render transform is simply the transform of the entity clients see within the scene.
	 * @return The Transform, interpolated, as of the last game tick.
	 */
	@SnapshotRead
	@ClientOnly
	public abstract Transform getRenderTransform();

	/**
	 * Sets the {@link Transform} for this {@link org.spout.api.entity.Entity}.
	 * <p>
	 * This function sets the live state of the entity's transform, not the snapshot state. As such, its advised
	 * to set the transform lastly else retrieving the transform afterwards within the same tick will not return
	 * expected values (due to potential other plugin changes as well as getTransform() returning snapshot state).
	 * <p>
	 * Additionally, this method also sets the transform for physics as well.
	 * @param transform The new live transform state of this entity.
	 * @return This component, for chaining.
	 */
	public abstract SceneComponent setTransform(Transform transform);

	/**
	 * Returns whether the live transform and snapshot transform are not equal.
	 * @return True if live is different than snapshot, false if the same.
	 */
	public abstract boolean isTransformDirty();

	/**
	 * Gets the {@link Point} representing the location where the {@link org.spout.api.entity.Entity} is within the scene.
	 * <p>
	 * The Point is guaranteed to always be valid.
	 * </p>
	 * @return The Point in the scene.
	 */
	@SnapshotRead
	@Threadsafe
	public abstract Point getPosition();

	/**
	 * Sets the {@link Point} for this {@link org.spout.api.entity.Entity}. This will directly set both the world space
	 * of the Entity as well as physics space. The physics space will be cleared of all forces in the process.
	 * <p>
	 * This function sets the live state of the entity's point, not the snapshot state. As such, its advised to set the
	 * point lastly else retrieving the point afterwards within the same tick will not return expected values (due to
	 * potential other plugin changes as well as getPosition() returning snapshot state).
	 * @param point The new live position state of this entity.
	 * @return This component, for chaining.
	 */
	public abstract SceneComponent setPosition(Point point);

	/**
	 * Determines if this {@link org.spout.api.entity.Entity} has a dirty position.
	 * <p>
	 * A dirty position is when the snapshot position (last tick) and live position are not equal.
	 * @return True if position is dirty, false if not.
	 */
	public abstract boolean isPositionDirty();

	/**
	 * Gets the {@link Quaternion} representing the rotation of the {@link org.spout.api.entity.Entity} within the scene.
	 * <p>
	 * The Quaternion is guaranteed to always be valid.
	 * </p>
	 * @return The Quaternion in the scene.
	 */
	@SnapshotRead
	@Threadsafe
	public abstract Quaternion getRotation();

	/**
	 * Sets the {@link Quaternion} for this {@link org.spout.api.entity.Entity}.
	 * <p>
	 * This functions sets the live state of the entity's quaternion (rotation), not the snapshot state. As such, its advised
	 * to set the quaternion lastly else retrieving the quaternion afterwards within the same tick will not return expected
	 * values (due to potential other plugin changes as well as getRotation() returning snapshot state).
	 * @param rotation The new live quaternion (rotation) of this entity.
	 * @return This component, for chaining.
	 */
	public abstract SceneComponent setRotation(Quaternion rotation);

	/**
	 * Determines if this {@link org.spout.api.entity.Entity} has a dirty rotation.
	 * <p>
	 * A dirty rotation is when the snapshot rotation (last tick) and live rotation are not equal.
	 * @return True if dirty, false if not.
	 */
	public abstract boolean isRotationDirty();

	/**
	 * Gets the {@link Vector3} representing the scale of the {@link org.spout.api.entity.Entity}.
	 * <p>
	 * The Scale is guaranteed to always be valid.
	 * </p>
	 * @return The Scale (Vector3) in the scene.
	 */
	@SnapshotRead
	@Threadsafe
	public abstract Vector3 getScale();

	/**
	 * Sets the {@link Vector3} representing the scale of the {@link org.spout.api.entity.Entity}.
	 * <p>
	 * This functions sets the live state of the entity's scale, not the snapshot state. As such, its advised
	 * to set the scale lastly else retrieving the scale afterwards within the same tick will not return expected
	 * values (due to potential other plugin changes as well as getScale() returning snapshot state).
	 * @param scale The new live scale of this entity.
	 * @return This component, for chaining.
	 */
	public abstract SceneComponent setScale(Vector3 scale);

	/**
	 * Determines if this {@link org.spout.api.entity.Entity} has a dirty scale.
	 * <p>
	 * A dirty scale is when the snapshot scale (last tick) and live scale are not equal.
	 * @return True if dirty, false if not.
	 */
	public abstract boolean isScaleDirty();

	/**
	 * Gets the {@link World} in-which this {@link org.spout.api.entity.Entity} is a part of.
	 * @return The world this entity is in.
	 */
	public abstract World getWorld();

	/**
	 * Determines if this {@link org.spout.api.entity.Entity} has a dirty {@link World}.
	 * <p>
	 * A dirty world is when the snapshot world (last tick) and live world are not equal.
	 * @return True if dirty, false if not.
	 */
	public abstract boolean isWorldDirty();

	/**
	 * Translates this {@link org.spout.api.entity.Entity} from its current {@link Point} to the Point
	 * that is the addition of the {@link Vector3} provided.
	 * <p>
	 * For example, if I want to move an Entity up one (Up being the y-axis), I would do a translate(new Vector3(0, 1, 0));
	 * @param translation A Vector3 which will be added to the current Point (position).
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent translate(Vector3 translation);

	/**
	 * Rotates this {@link org.spout.api.entity.Entity} from its current {@link org.spout.api.math.Quaternion} to the Quaternion
	 * that is the addition of the Quaternion provided.
	 * <p>
	 * For example, if I want to rotate an Entity upwards (which is moving its yaw), I would do a rotate(new Quaternion(0, 1, 0, 0));
	 * <p>
	 * Bear in mind, doing a rotate does so without physics and instead the rotation of the Entity will be directly set within its physics
	 * transform.
	 * </p>
	 * @param rotate A Quaternion which will be added to the current Quaternion (rotation).
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent rotate(Quaternion rotate);

	/**
	 * Scales this {@link org.spout.api.entity.Entity} from its current scale to the {@link Vector3} representing the new scale which is
	 * an addition of the Vector3 provided.
	 * <p>
	 * For example, if I want to scale an Entity to be taller (which is scaling its y-factor), I would do a scale(new Vector3(0, 1, 0));
	 * </p>
	 * @param scale A Vector3 which will be added to the current Vector3 (scale).
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent scale(Vector3 scale);

	// Physics

	/**
	 * Impulse performs a translation of the {@link org.spout.api.entity.Entity} by the {@link Vector3} from the Vector3 offset.
	 * <p>
	 * Impulse is a force across delta time (since last simulation tick). A few rules apply.
	 * - The entity must have a mass > 0 (ie not a static object).
	 * - The offset is in world space. This means the impulse is applied from the offset provided.
	 * - Entities of higher masses need greater impulses to move. Can't get movement to occur? Lower mass or apply greater impulse.
	 * <p>
	 * For example, if I want to propel an entity forward, like a wave of water pushing an entity downstream, I would do the following.
	 * <p>
	 * // Vector3.FORWARD = 0, 0, 1
	 * scene.impulse(Vector3.FORWARD, new Vector3(getPosition().subtract(getTransform().getForward()));
	 * // The above adds 1 to the forwardness of the Entity every simulation step (if applied in onTick for example)
	 * </p>
	 * @param impulse The Vector3 impulse (force) to apply.
	 * @param offset The offset within the world to apply the impulse from.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent impulse(Vector3 impulse, Vector3 offset);

	public abstract SceneComponent impulse(Vector3 impulse);

	/**
	 * Force performs a translation of the {@link org.spout.api.entity.Entity} by the {@link Vector3} from the Vector3 offset.
	 * <p>
	 * Force is, as it sounds, an instant force to the Entity. A few rules apply.
	 * - The entity must have a mass > 0 (ie not a static object).
	 * - The offset is in world space. This means the force is applied from the offset provided.
	 * - Entities of higher masses need greater forces to move. Can't get movement to occur? Lower mass or apply greater force.
	 * <p>
	 * For example, if I want to propel an entity right in an instant, such as simulating a two entities hitting each other, I would do the following.
	 * <p>
	 * // Vector3.RIGHT = 1, 0, 0
	 * scene.force(Vector3.RIGHT, new Vector3(getPosition().subtract(getTransform.getRight()));
	 * // The above forces the Entity's momentum by 1 every simulation step (if applied in onTick for example).
	 * </p>
	 * @param force The Vector3 force to apply.
	 * @param offset The offset within the world to apply the force from.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent force(Vector3 force, Vector3 offset);

	public abstract SceneComponent force(Vector3 force);

	/**
	 * Torque performs a rotation of the {@link org.spout.api.entity.Entity} by the {@link Vector3}.
	 * <p>
	 * The Vector3 is (yaw, pitch, roll) and is an instant change to rotation.
	 * <p>
	 * For example, if I want to rotate the entity to the right, I would do the following.
	 * <p>
	 * //Vector3.RIGHT = 1, 0, 0
	 * scene.torque(Vector3.RIGHT);
	 * //The above rotates the Entity to the right instantly.
	 * @param torque The Vector3 torque to apply
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent torque(Vector3 torque);

	/**
	 * Impulse Torque performs a rotation over a delta of the {@link org.spout.api.entity.Entity} by the {@link Vector3}.
	 * <p>
	 * The Vector3 is (yaw, pitch, roll) and is a change in rotation spread over delta time (since last simulation).
	 * <p>
	 * For example, if I want to rotate the entity to the right over time, I would do the following.
	 * <p>
	 * //Vector3.RIGHT = 1, 0, 0
	 * scene.impulseTorque(Vector3.RIGHT)
	 * //The above rotates the Entity over time to the right.
	 * @param torque Tne Vector3 torque to apply.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent impulseTorque(Vector3 torque);

	/**
	 * Dampens the {@link org.spout.api.entity.Entity}'s movement velocity by the factor provided.
	 * <p>
	 * 0.0f = no dampening, 1.0f = full velocity stop. Any values outside this range will be clamped to the nearest bound.
	 * @param damp The float dampener to apply.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent dampenMovement(float damp);

	/**
	 * Dampens the {@link org.spout.api.entity.Entity}'s rotation velocity by the factor provided.
	 * <p>
	 * 0.0f = no dampening, 1.0f = full velocity stop. Any values outside this range will be clamped to the nearest bound.
	 * @param damp The float dampener to apply.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent dampenRotation(float damp);

	// Physics characteristics

	/**
	 * Gets the friction (slipperiness) of this {@link org.spout.api.entity.Entity}.
	 * <p>
	 * 0.0f = no slipperiness, 1.0f = full slipperiness.
	 * @return the value of the friction.
	 */
	public abstract float getFriction();

	/**
	 * Sets the friction (slipperiness) of this {@link org.spout.api.entity.Entity}.
	 * <p>
	 * 0.0f = no slipperiness, 1.0f = full slipperiness. Any values outside of this range will be clamped to the nearest bound.
	 * @param friction The float friction value this entity will have.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent setFriction(float friction);

	/**
	 * Gets the mass (heaviness) of this {@link org.spout.api.entity.Entity}.
	 * <p>
	 * Mass of 0f means this is a static entity, one in-which doesn't move.
	 * @return the value of the mass.
	 */
	public abstract float getMass();

	/**
	 * Gets the restitution (bounciness) of this {@link org.spout.api.entity.Entity}.
	 * <p>
	 * 0.0f = no bounciness, 1.0f = full bounciness.
	 * @return the value of the restitution.
	 */
	public abstract float getRestitution();

	/**
	 * Sets the restitution (bounciness) of this {@link org.spout.api.entity.Entity}.
	 * <p>
	 * 0.0f = no bounciness, 1.0f = full bounciness. Any values outside of this range will be clamped to the nearest bound.
	 * @param restitution The float restitution value this entity will have.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent setRestitution(float restitution);

	/**
	 * Gets the movement velocity of this {@link org.spout.api.entity.Entity}.
	 * @return the current velocity as a {@link Vector3}.
	 */
	public abstract Vector3 getMovementVelocity();

	/**
	 * Sets the movement velocity for this {@link org.spout.api.entity.Entity}.
	 * @param velocity The {@link Vector3} velocity to apply to movement.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent setMovementVelocity(Vector3 velocity);

	/**
	 * Gets rotation velocity of this {@link org.spout.api.entity.Entity}.
	 * @return the current velocity as a {@link Vector3}.
	 */
	public abstract Vector3 getRotationVelocity();

	/**
	 * Sets the rotation velocity for this {@link org.spout.api.entity.Entity}.
	 * @param velocity The {@link Vector3} velocity to apply to rotation.
	 * @return This component, so you can chain.
	 */
	public abstract SceneComponent setRotationVelocity(Vector3 velocity);

	/**
	 * Activates the physics for this scene by giving it a shape and a mass.
	 * May be called more than once to change the shape or mass of the scene.
	 * @param area
	 * @param mass
	 * @return this component
	 */
	public abstract SceneComponent activate(BoundingBox area, float mass);

	/**
	 * Gets the bounding box volume for this scene, or null if not activated.
	 * @return bounding box volume, or null.
	 */
	public abstract BoundingBox getVolume();

	// Other

	/**
	 * Returns whether the physics for this scene is active.
	 * <p>
	 * Physics is considered activated when given a nonzero mass.
	 * </p>
	 * @return activated
	 */
	public abstract boolean isActivated();

	@Override
	public boolean isDetachable() {
		return false;
	}
}