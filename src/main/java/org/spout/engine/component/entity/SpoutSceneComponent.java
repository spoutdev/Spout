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
package org.spout.engine.component.entity;

import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.ClientOnly;
import org.spout.api.collision.BoundingBox;
import org.spout.api.component.entity.SceneComponent;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.concurrent.AtomicFloat;

/**
 * The Spout implementation of {@link SceneComponent}.
 */
public class SpoutSceneComponent extends SceneComponent {
	private final Transform snapshot = new Transform();
	private final Transform live = new Transform();
	private Vector3 velocitySnapshot = Vector3.ZERO;
	private final AtomicReference<Vector3> velocity = new AtomicReference<Vector3>(Vector3.ZERO);
	private final AtomicReference<Vector3> impulses = new AtomicReference<Vector3>(Vector3.ZERO);
	private final AtomicReference<Vector3> forces = new AtomicReference<Vector3>(Vector3.ZERO);
	private final AtomicFloat mass = new AtomicFloat(0);
	private final AtomicReference<BoundingBox> area = new AtomicReference<BoundingBox>(null);
	//Client/Rendering
	private final Transform render = new Transform();
	private Vector3 position = Vector3.ONE;
	private Quaternion rotate = Quaternion.IDENTITY;
	private Vector3 scale = Vector3.ONE;

	@Override
	public void onAttached() {
		//TODO Player Physics
		//		if (getOwner() instanceof Player) {
		//			throw new IllegalStateException("This component is not designed for Players.");
		//		}
		render.set(live);
	}

	@Override
	public Transform getTransform() {
		return snapshot.copy();
	}

	public Transform getLiveTransform() {
		return live.copy();
	}

	@Override
	public SpoutSceneComponent setTransform(Transform transform) {
		live.set(transform);
		return this;
	}

	@Override
	public boolean isTransformDirty() {
		return !snapshot.equals(live);
	}

	@Override
	public Point getPosition() {
		return snapshot.getPosition();
	}

	@Override
	public SpoutSceneComponent setPosition(Point point) {
		live.setPosition(point);
		return this;
	}

	@Override
	public boolean isPositionDirty() {
		return !snapshot.getPosition().equals(live.getPosition());
	}

	@Override
	public Quaternion getRotation() {
		return snapshot.getRotation();
	}

	@Override
	public SpoutSceneComponent setRotation(Quaternion rotation) {
		live.setRotation(rotation);
		return this;
	}

	@Override
	public boolean isRotationDirty() {
		return !snapshot.getRotation().equals(live.getRotation());
	}

	@Override
	public Vector3 getScale() {
		return snapshot.getScale();
	}

	@Override
	public SpoutSceneComponent setScale(Vector3 scale) {
		live.setScale(scale);
		return this;
	}

	@Override
	public boolean isScaleDirty() {
		return !snapshot.getScale().equals(live.getScale());
	}

	@Override
	public World getWorld() {
		return getPosition().getWorld();
	}

	@Override
	public boolean isWorldDirty() {
		return !snapshot.getPosition().getWorld().equals(live.getPosition().getWorld());
	}

	@Override
	public SpoutSceneComponent translate(Vector3 point) {
		live.translate(point);
		return this;
	}

	@Override
	public SpoutSceneComponent rotate(Quaternion rotate) {
		live.rotate(rotate);
		return this;
	}

	@Override
	public SpoutSceneComponent scale(Vector3 scale) {
		live.scale(scale);
		return this;
	}

	@Override
	public SpoutSceneComponent impulse(Vector3 impulse, Vector3 offset) {
		//TODO: implement correctly
		impulse(impulse);
		return this;
	}

	@Override
	public SpoutSceneComponent impulse(Vector3 impulse) {
		Vector3 impulses = this.impulses.get();
		while (!this.impulses.compareAndSet(impulses, impulses.add(impulse))) {
			impulses = this.impulses.get();
		}
		return this;
	}

	public Vector3 getRawImpulses() {
		return this.impulses.get();
	}

	public SpoutSceneComponent setRawImpulses(Vector3 impulse) {
		this.impulses.set(impulse);
		return this;
	}

	@Override
	public SpoutSceneComponent force(Vector3 force, Vector3 offset) {
		//TODO: implement correctly
		force(force);
		return this;
	}

	@Override
	public SpoutSceneComponent force(Vector3 force) {
		Vector3 forces = this.forces.get();
		while (!this.forces.compareAndSet(forces, forces.add(force))) {
			forces = this.forces.get();
		}
		return this;
	}

	@Override
	public SpoutSceneComponent torque(Vector3 torque) {
		//TODO: implement
		return this;
	}

	@Override
	public SpoutSceneComponent impulseTorque(Vector3 torque) {
		//TODO: implement
		return this;
	}

	@Override
	public SpoutSceneComponent dampenMovement(float damp) {
		//TODO: implement
		return this;
	}

	@Override
	public SpoutSceneComponent dampenRotation(float damp) {
		//TODO: implement
		return this;
	}

	@Override
	public float getFriction() {
		//TODO: implement
		return 0F;
	}

	@Override
	public SpoutSceneComponent setFriction(float friction) {
		//TODO: implement
		return this;
	}

	@Override
	public float getMass() {
		return mass.get();
	}

	@Override
	public float getRestitution() {
		//TODO: implement
		return 0;
	}

	@Override
	public SpoutSceneComponent setRestitution(float restitution) {
		//TODO: implement
		return this;
	}

	@Override
	public Vector3 getMovementVelocity() {
		return velocitySnapshot;
	}

	public Vector3 getRawMovementVelocity() {
		return this.velocity.get();
	}

	public Vector3 getRawForces() {
		return this.forces.get();
	}

	public SpoutSceneComponent setRawForces(Vector3 force) {
		this.forces.set(force);
		return this;
	}

	@Override
	public SpoutSceneComponent setMovementVelocity(Vector3 velocity) {
		this.velocity.set(velocity);
		return this;
	}

	@Override
	public Vector3 getRotationVelocity() {
		//TODO: implement
		return Vector3.ZERO;
	}

	@Override
	public SpoutSceneComponent setRotationVelocity(Vector3 velocity) {
		//TODO: implement
		return this;
	}

	@Override
	public SpoutSceneComponent activate(BoundingBox area, float mass) {
		this.area.set(area);
		this.mass.set(mass);
		return this;
	}

	@Override
	public BoundingBox getVolume() {
		return this.area.get();
	}

	@Override
	public boolean isActivated() {
		return mass.get() > 0;
	}

	/**
	 * Gets the live transform state of this {@link org.spout.api.entity.Entity} within the scene.
	 * <p/>
	 * Warning: the live transform is unstable; it may change during the API tickstage.
	 * @return The Transform representing the live state.
	 */
	public Transform getTransformLive() {
		return live;
	}

	/**
	 * Gets the {@link Transform} this {@link org.spout.api.entity.Entity} had within the last game tick
	 * of the scene with interpolation applied (so it appears smooth to users).
	 * <p/>
	 * The render transform is the transform of the entity clients see within the scene.
	 * @return The Transform, interpolated, as of the last game tick.
	 */
	@ClientOnly
	@Override
	public Transform getRenderTransform() {
		return render;
	}

	/**
	 * Snapshots values for the next tick.
	 */
	public void copySnapshot() {
		snapshot.set(live);
		velocitySnapshot = velocity.get();
		position = snapshot.getPosition();

		scale = snapshot.getScale();

		Quaternion qDiff = new Quaternion(snapshot.getRotation().getX() - render.getRotation().getX(),
				snapshot.getRotation().getY() - render.getRotation().getY(),
				snapshot.getRotation().getZ() - render.getRotation().getZ(),
				snapshot.getRotation().getW() - render.getRotation().getW(), false);

		if (qDiff.getX() * qDiff.getX() + qDiff.getY() * qDiff.getY() + qDiff.getZ() * qDiff.getZ() > 2) {
			rotate = new Quaternion(-snapshot.getRotation().getX(),
					-snapshot.getRotation().getY(),
					-snapshot.getRotation().getZ(),
					-snapshot.getRotation().getW(), false);
		} else {
			rotate = snapshot.getRotation();
		}
	}

	/**
	 * Interpolates the render transform for Spout rendering. This only kicks in when the entity has no body.
	 * @param dtp time since last interpolation.
	 */
	public void interpolateRender(float dtp) {

		float dt = dtp * 80f / 20f;

		render.setPosition(render.getPosition().multiply(1 - dt).add(position.multiply(dt)));
		Quaternion q = render.getRotation();
		render.setRotation(new Quaternion(q.getX() * (1 - dt) + rotate.getX() * dt,
				q.getY() * (1 - dt) + rotate.getY() * dt,
				q.getZ() * (1 - dt) + rotate.getZ() * dt,
				q.getW() * (1 - dt) + rotate.getW() * dt, false));
		render.setScale(render.getScale().multiply(1 - dt).add(scale.multiply(dt)));
	}
}