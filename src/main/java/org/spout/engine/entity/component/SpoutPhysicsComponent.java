/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.entity.component;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import org.spout.api.component.components.PhysicsComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;

import org.spout.engine.world.SpoutRegion;

/**
 * A component that represents the physics object that is a motion of the entity within the world.
 */
public class SpoutPhysicsComponent extends PhysicsComponent {
	//TODO persist
	private RigidBody body = null;
	private MotionState state;
	private Vector3 angularVelocity = Vector3.ZERO;
	private Vector3 linearVelocity = Vector3.ZERO;
	private boolean dirty = false;
	//Mass is stored but it can only be set on the object before the entity is spawned into the world.
	private float mass = 0f;

	@Override
	public void onDetached() {
		SpoutRegion region = (SpoutRegion) getOwner().getRegion();
		if (region != null) {
			region.removePhysics(this);
		}
	}

	@Override
	public float getFriction() {
		return body.getFriction();
	}

	@Override
	public void setFriction(float friction) {
		body.setFriction(friction);
	}

	@Override
	public float getMass() {
		return mass;
	}

	@Override
	public void setMass(float mass) {
		this.mass = mass;
	}

	@Override
	public CollisionObject getCollisionObject() {
		return body;
	}

	@Override
	public MotionState getMotionState() {
		return state;
	}

	@Override
	public CollisionShape getCollisionShape() {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		return body.getCollisionShape();
	}

	@Override
	public void setCollisionShape(CollisionShape shape) {
		if (body != null) {
			throw new IllegalStateException("Can not setup physics component twice!");
		}
		org.spout.api.geo.discrete.Transform spoutTransform = getOwner().getTransform().getTransformLive();
		Point point = spoutTransform.getPosition();
		state = new SpoutDefaultMotionState(getOwner());
		body = new RigidBody(getMass(), state, shape);
		body.setWorldTransform(new Transform(new Matrix4f(MathHelper.toQuaternionf(spoutTransform.getRotation()), MathHelper.toVector3f(point.getX(), point.getY(), point.getZ()), 1)));
		body.activate();
	}

	@Override
	public Vector3 getAngularVelocity() {
		return angularVelocity;
	}

	@Override
	public Vector3 getLinearVelocity() {
		return linearVelocity;
	}

	@Override
	public boolean isVelocityDirty() {
		return dirty;
	}

	@Override
	public void applyImpulse(Vector3 impulse) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		SpoutRegion r = (SpoutRegion) getOwner().getRegion();
		if (r == null) {
			throw new IllegalStateException("Entity region is null!");
		}
		synchronized (r.getSimulation()) {
			body.applyCentralImpulse(MathHelper.toVector3f(impulse));
		}
	}

	@Override
	public void applyImpulse(Vector3 impulse, Vector3 relativePos) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		SpoutRegion r = (SpoutRegion) getOwner().getRegion();
		if (r == null) {
			throw new IllegalStateException("Entity region is null!");
		}
		synchronized (r.getSimulation()) {
			body.applyImpulse(MathHelper.toVector3f(impulse), MathHelper.toVector3f(relativePos));
		}
	}

	@Override
	public void applyForce(Vector3 force) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		SpoutRegion r = (SpoutRegion) getOwner().getRegion();
		if (r == null) {
			throw new IllegalStateException("Entity region is null!");
		}
		synchronized (r.getSimulation()) {
			body.applyCentralForce(MathHelper.toVector3f(force));
		}
	}

	@Override
	public void applyForce(Vector3 force, Vector3 relativePos) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		SpoutRegion r = (SpoutRegion) getOwner().getRegion();
		if (r == null) {
			throw new IllegalStateException("Entity region is null!");
		}
		synchronized (r.getSimulation()) {
			body.applyForce(MathHelper.toVector3f(force), MathHelper.toVector3f(relativePos));
		}
	}

	public void copySnapshot() {
		if (body != null) {
			SpoutRegion r = (SpoutRegion) getOwner().getRegion();
			if (r == null) {
				throw new IllegalStateException("Entity region is null!");
			}
			synchronized (r.getSimulation()) {
				angularVelocity = MathHelper.toVector3(body.getInterpolationAngularVelocity(new Vector3f()));
				Vector3 velocity = MathHelper.toVector3(body.getInterpolationLinearVelocity(new Vector3f()));
				if (!velocity.equals(linearVelocity)) {
					dirty = true;
				} else {
					dirty = false;
				}
				linearVelocity = velocity;
			}
		}
	}

	private class SpoutDefaultMotionState extends MotionState {
		private final Entity entity;

		public SpoutDefaultMotionState(Entity entity) {
			this.entity = entity;
		}

		@Override
		public Transform getWorldTransform(Transform transform) {
			org.spout.api.geo.discrete.Transform spoutTransform = entity.getTransform().getTransformLive();
			Point point = spoutTransform.getPosition();
			transform.set(new Matrix4f(MathHelper.toQuaternionf(spoutTransform.getRotation()), MathHelper.toVector3f(point.getX(), point.getY(), point.getZ()), 1));
			return transform;
		}

		@Override
		public void setWorldTransform(Transform transform) {
			Point pos = new Point(MathHelper.toVector3(transform.origin), entity.getWorld());
			entity.getTransform().setPosition(pos);
			entity.getTransform().setRotation(MathHelper.toQuaternion(transform.getRotation(new Quat4f())));
		}
	}
}