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
package org.spout.engine.entity.component;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import org.spout.api.component.impl.PhysicsComponent;
import org.spout.api.component.impl.TransformComponent;
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
	private DefaultMotionState state;
	private Vector3 angularVelocity = Vector3.ZERO;
	private Vector3 linearVelocity = Vector3.ZERO;
	private boolean dirty = false;
	private float mass = 0f;

	@Override
	public void onDetached() {
		SpoutRegion region = (SpoutRegion) getOwner().getRegion();
		if (region != null) {
			region.removePhysics(this);
		}
	}

	@Override
	public float getRestitution() {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			return body.getRestitution();
		}
	}

	@Override
	public void setRestitution(float restitution) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			body.setRestitution(restitution);
		}
	}

	@Override
	public float getAngularDamping() {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			return body.getAngularDamping();
		}
	}

	@Override
	public float getLinearDamping() {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			return body.getLinearDamping();
		}
	}

	@Override
	public void setDamping(float linearDamping, float angularDamping) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			body.setDamping(linearDamping, angularDamping);
		}
	}

	@Override
	public float getFriction() {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			return body.getFriction();
		}
	}

	@Override
	public void setFriction(float friction) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			body.setFriction(friction);
		}
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
		Vector3f inertia = new Vector3f(0f, 0f, 0f);
		shape.calculateLocalInertia(mass, new Vector3f(0f, 0f, 0f));
		final RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass, state, shape, inertia);
		info.restitution = 0f;
		body = new RigidBody(info);
		body.setUserPointer(getOwner());
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
	public void setAngularVelocity(Vector3 velocity) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			body.setAngularVelocity(MathHelper.toVector3f(velocity));
		}
	}

	@Override
	public void setLinearVelocity(Vector3 velocity) {
		if (body == null) {
			throw new IllegalStateException("A collision shape must be set first");
		}
		synchronized (((SpoutRegion) getOwner().getRegion()).getSimulation()) {
			body.setLinearVelocity(MathHelper.toVector3f(velocity));
		}
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

	/**
	 * Gets the collision object which holds the collision shape and is used to calculate physics such as velocity, intertia,
	 * etc. All PhysicsComponents are guaranteed to have a valid object.
	 * @return the CollisionObject
	 */
	public CollisionObject getCollisionObject() {
		return body;
	}

	/**
	 * Gets the MotionState. A MotionState is the "bridge" between Bullet and Spout in-which Bullet tells Spout that the
	 * object has moved and to update your transforms accordingly.
	 * @return the MotionState
	 */
	public DefaultMotionState getMotionState() {
		return state;
	}

	public void copySnapshot() {
		if (body != null) {
			SpoutRegion r = (SpoutRegion) getOwner().getRegion();
			if (r == null) {
				throw new IllegalStateException("Entity region is null!");
			}
			synchronized (r.getSimulation()) {
				Vector3 angularVelocityLive = MathHelper.toVector3(body.getInterpolationAngularVelocity(new Vector3f()));
				Vector3 linearVelocityLive = MathHelper.toVector3(body.getInterpolationLinearVelocity(new Vector3f()));
				dirty = !linearVelocityLive.equals(linearVelocity) || angularVelocityLive.equals(angularVelocity);
				angularVelocity = angularVelocityLive;
				linearVelocity = linearVelocityLive;
			}
		}
	}

	private class SpoutDefaultMotionState extends DefaultMotionState {
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
			TransformComponent t = entity.getTransform();
			org.spout.api.geo.discrete.Transform spoutTransform = t.getTransformLive();
			Point point = spoutTransform.getPosition();
			boolean resetPos = false, resetRot = false;
			if (!t.isPositionDirty()) {
				t.setPosition(new Point(MathHelper.toVector3(transform.origin), entity.getWorld()));
			} else {
				resetPos = true;
			}
			if (!t.isRotationDirty()) {
				t.setRotation(MathHelper.toQuaternion(transform.getRotation(new Quat4f())));
			} else {
				resetRot = true;
			}
			
			if (resetPos && resetRot) {
				transform.set(new Matrix4f(MathHelper.toQuaternionf(spoutTransform.getRotation()), MathHelper.toVector3f(point.getX(), point.getY(), point.getZ()), 1));
				body.setWorldTransform(transform);
			} else if (resetPos) {
				transform.set(new Matrix4f(transform.getRotation(new Quat4f()), MathHelper.toVector3f(point.getX(), point.getY(), point.getZ()), 1));
				body.setWorldTransform(transform);
			} else if (resetRot) {
				transform.set(new Matrix4f(MathHelper.toQuaternionf(spoutTransform.getRotation()), transform.origin, 1));
				body.setWorldTransform(transform);
			}
		}
	}
}