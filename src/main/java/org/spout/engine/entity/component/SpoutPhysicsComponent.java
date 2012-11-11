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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.google.common.base.Objects;

import org.spout.api.component.components.PhysicsComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.discrete.Point;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.engine.world.SpoutRegion;

/**
 * A component that represents the physics object that is a motion of the entity within the world.
 */
public class SpoutPhysicsComponent extends PhysicsComponent {
	private static final DefaultedKeyImpl<Vector3> ANGULAR_VELOCITY = new DefaultedKeyImpl<Vector3>("live_angular_velocity", Vector3.ZERO);
	private static final DefaultedKeyImpl<Vector3> LINEAR_VELOCITY = new DefaultedKeyImpl<Vector3>("live_linear_velocity", Vector3.ZERO);
	//TODO persist 
	private RigidBody collisionObject = null;
	private final AtomicReference<CollisionShape> liveShape = new AtomicReference<CollisionShape>(null);
	private MotionState state;

	private final AtomicBoolean dirtyAngularVelocity = new AtomicBoolean(false);
	private final AtomicBoolean dirtyLinearVelocity = new AtomicBoolean(false);
	private Vector3 angularVelocity = Vector3.ZERO;
	private Vector3 linearVelocity = Vector3.ZERO;

	@Override
	public void onAttached() {
		if (collisionObject != null) {
			throw new IllegalStateException("Can not attach physics component twice!");
		}
		state = new SpoutDefaultMotionState(getOwner());
		collisionObject = new RigidBody(0.5F, state, liveShape.get());
		org.spout.api.geo.discrete.Transform spoutTransform = getOwner().getTransform().getTransformLive();
		Point point = spoutTransform.getPosition();
		collisionObject.setWorldTransform(new Transform(new Matrix4f(MathHelper.toQuaternionf(spoutTransform.getRotation()), MathHelper.toVector3f(point.getX(), point.getY(), point.getZ()), 1)));
	}

	@Override
	public void onDetached() {
		((SpoutRegion)this.getOwner().getRegion()).removePhysics(this);
	}

	@Override
	public CollisionObject getCollisionObject() {
		return collisionObject;
	}

	@Override
	public MotionState getMotionState() {
		return state;
	}

	@Override
	public CollisionShape getCollisionShape() {
		return collisionObject.getCollisionShape();
	}

	@Override
	public void setCollisionShape(CollisionShape shape) {
		liveShape.set(shape);
	}

	@Override
	public Vector3 getAngularVelocity() {
		return angularVelocity;
	}

	@Override
	public Vector3 getAngularVelocityLive() {
		return getData().get(ANGULAR_VELOCITY);
	}

	@Override
	public Vector3 getLinearVelocity() {
		return linearVelocity;
	}

	@Override
	public Vector3 getLinearVelocityLive() {
		return getData().get(LINEAR_VELOCITY);
	}

	@Override
	public void setAngularVelocity(Vector3 velocity) {
		getData().put(ANGULAR_VELOCITY, velocity);
		dirtyAngularVelocity.set(true);
	}

	@Override
	public void setLinearVelocity(Vector3 velocity) {
		getData().put(LINEAR_VELOCITY, velocity);
		dirtyLinearVelocity.set(true);
	}

	@Override
	public boolean isVelocityDirty() {
		return !angularVelocity.equals(getAngularVelocityLive()) && !linearVelocity.equals(getLinearVelocityLive());
	}

	@Override
	public boolean isCollisionObjectDirty() {
		return !Objects.equal(liveShape.get(), getCollisionShape());
	}

	public void copySnapshot() {
		//Was dirty, cleaning
		if (dirtyAngularVelocity.compareAndSet(true, false)) {
			angularVelocity  = getAngularVelocityLive();
			collisionObject.setInterpolationAngularVelocity(MathHelper.toVector3f(angularVelocity));
		} else {
			angularVelocity = MathHelper.toVector3(collisionObject.getInterpolationAngularVelocity(new Vector3f()));
			getData().put(ANGULAR_VELOCITY, angularVelocity);
		}
		if (dirtyLinearVelocity.compareAndSet(true, false)) {
			linearVelocity  = getLinearVelocityLive();
			collisionObject.setInterpolationAngularVelocity(MathHelper.toVector3f(linearVelocity));
		} else {
			linearVelocity = MathHelper.toVector3(collisionObject.getInterpolationLinearVelocity(new Vector3f()));
			getData().put(LINEAR_VELOCITY, linearVelocity);
		}

		if (isCollisionObjectDirty()) {
			collisionObject.setCollisionShape(liveShape.get());
		}
	}

	private static class SpoutDefaultMotionState extends DefaultMotionState {
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