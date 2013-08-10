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
package org.spout.engine.component.entity;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.component.entity.PhysicsComponent;
import org.spout.api.entity.Player;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.ReactConverter;
import org.spout.api.math.Vector3;
import org.spout.engine.world.SpoutRegion;

import org.spout.physics.body.MobileRigidBody;
import org.spout.physics.body.RigidBody;
import org.spout.physics.body.RigidBodyMaterial;
import org.spout.physics.collision.shape.CollisionShape;

/**
 * The Spout implementation of {@link org.spout.api.component.entity.PhysicsComponent}. <p/> //TODO: Physics rotation setters
 */
public class SpoutPhysicsComponent extends PhysicsComponent {
	//Spout
	private final Transform snapshot = new Transform();
	private final Transform live = new Transform();
	private final Transform render = new Transform();
	//React
	private RigidBody body;
	private final RigidBodyMaterial material = new RigidBodyMaterial();
	//Used in handling crossovers
	private CollisionShape shape;
	private float mass = 0;
	private boolean activated = false;
	private boolean isMobile = true;
	private boolean isGhost = false;

	@Override
	public PhysicsComponent activate(final float mass, final CollisionShape shape, final boolean isGhost, final boolean isMobile) {
		if (mass < 1f) {
			throw new IllegalArgumentException("Cannot activate physics with mass less than 1f");
		}
		if (shape == null) {
			throw new IllegalArgumentException("Cannot activate physics with a null shape");
		}
		if (body != null) {
			((SpoutRegion) getOwner().getRegion()).removeBody(body);
		}
		this.isGhost = isGhost;
		this.isMobile = isMobile;
		this.mass = mass;
		this.shape = shape;
		activated = true;
		activate((SpoutRegion) getOwner().getRegion());

		return this;
	}

	public void activate(final SpoutRegion region) {
		body = region.addBody(live, mass, shape, isGhost, isMobile);
		body.setMaterial(material);
		body.setUserPointer(getOwner());
	}

	@Override
	public void deactivate() {
		if (getOwner() != null && getOwner().getRegion() != null && body != null) {
			((SpoutRegion) getOwner().getRegion()).removeBody(body);
		}
		activated = false;
	}

	@Override
	public boolean isActivated() {
		return activated;
	}

	@Override
	public Transform getTransform() {
		return snapshot.copy();
	}

	@Override
	public Transform getTransformRender() {
		return render;
	}

	public Transform getTransformLive() {
		return live;
	}

	@Override
	public SpoutPhysicsComponent setTransform(Transform transform) {
		return setTransform(transform, true);
	}

	public SpoutPhysicsComponent setTransform(Transform transform, boolean sync) {
		live.set(transform);
		if (sync) {
			sync();
		}
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
	public SpoutPhysicsComponent setPosition(Point point) {
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
	public SpoutPhysicsComponent setRotation(Quaternion rotation) {
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
	public SpoutPhysicsComponent setScale(Vector3 scale) {
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
	public SpoutPhysicsComponent translate(Vector3 point) {
		live.translate(point);
		return this;
	}

	@Override
	public SpoutPhysicsComponent rotate(Quaternion rotate) {
		live.rotate(rotate);
		return this;
	}

	@Override
	public SpoutPhysicsComponent scale(Vector3 scale) {
		live.scale(scale);
		return this;
	}

	@Override
	public PhysicsComponent impulse(Vector3 impulse, Vector3 offset) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public PhysicsComponent impulse(Vector3 impulse) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public PhysicsComponent force(Vector3 force, boolean ignoreGravity) {
		if (body == null) {
			throw new IllegalStateException("Cannot force a null body. If the entity is activated, make sure it is spawned as well");
		}
		if (ignoreGravity) {
			body.setExternalForce(ReactConverter.toReactVector3(force));
		} else {
			body.getExternalForce().add(ReactConverter.toReactVector3(force));
		}
		return this;
	}

	@Override
	public PhysicsComponent force(Vector3 force) {
		return force(force, false);
	}

	@Override
	public PhysicsComponent torque(Vector3 torque) {
		if (body == null) {
			throw new IllegalStateException("Cannot torque a null body. If the entity is activated, make sure it is spawned as well");
		}
		body.setExternalTorque(ReactConverter.toReactVector3(torque));
		return this;
	}

	@Override
	public PhysicsComponent impulseTorque(Vector3 torque) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public PhysicsComponent dampenMovement(float damp) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public PhysicsComponent dampenRotation(float damp) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public float getMass() {
		return mass;
	}

	@Override
	public PhysicsComponent setMass(float mass) {
		if (!isActivated()) {
			throw new IllegalStateException("Entities cannot have mass until they are activated");
		}
		if (!(body instanceof MobileRigidBody)) {
			throw new IllegalStateException("Only mobile entities can change mass");
		}
		if (mass < 0f) {
			throw new IllegalArgumentException("Cannot set a mass less than 0f");
		}
		this.mass = mass;
		((MobileRigidBody) body).setMass(mass);
		return this;
	}

	@Override
	public float getFriction() {
		return material.getFriction();
	}

	@Override
	public PhysicsComponent setFriction(float friction) {
		if (friction < 0f || friction > 1f) {
			throw new IllegalArgumentException("Friction must be between 0f and 1f (inclusive)");
		}
		material.setFriction(friction);
		return this;
	}

	@Override
	public float getRestitution() {
		return material.getRestitution();
	}

	@Override
	public PhysicsComponent setRestitution(float restitution) {
		if (restitution < 0f || restitution > 1f) {
			throw new IllegalArgumentException("Restitution must be between 0f and 1f (inclusive)");
		}
		material.setRestitution(restitution);
		return this;
	}

	@Override
	public Vector3 getMovementVelocity() {
		if (body == null) {
			throw new IllegalStateException("Cannot get velocity of a null body. If the entity is activated, make sure it is spawned as well");
		}
		return ReactConverter.toSpoutVector3(body.getLinearVelocity());
	}

	@Override
	public PhysicsComponent setMovementVelocity(Vector3 velocity) {
		if (body == null) {
			throw new IllegalStateException("Cannot set velocity of a null body. If the entity is activated, make sure it is spawned as well");
		}
		if (!(body instanceof MobileRigidBody)) {
			throw new UnsupportedOperationException("Bodies which are not instances of MobileRigidBody cannot set their movement velocity");
		}
		((MobileRigidBody) body).setLinearVelocity(ReactConverter.toReactVector3(velocity));
		return this;
	}

	@Override
	public Vector3 getRotationVelocity() {
		if (body == null) {
			throw new IllegalStateException("Cannot get rotation velocity of a null body. If the entity is activated, make sure it is spawned as well");
		}
		return ReactConverter.toSpoutVector3(body.getAngularVelocity());
	}

	@Override
	public PhysicsComponent setRotationVelocity(Vector3 velocity) {
		if (body == null) {
			throw new IllegalStateException("Cannot set rotation velocity of a null body. If the entity is activated, make sure it is spawned as well");
		}
		if (!(body instanceof MobileRigidBody)) {
			throw new UnsupportedOperationException("Bodies which are not instances of MobileRigidBody cannot set their rotation velocity");
		}
		((MobileRigidBody) body).setAngularVelocity(ReactConverter.toReactVector3(velocity));
		return this;
	}

	@Override
	public CollisionShape getShape() {
		return shape;
	}

	@Override
	public boolean isMobile() {
		return isMobile;
	}

	@Override
	public boolean isGhost() {
		return isGhost;
	}

	@Override
	public String toString() {
		return "snapshot= {" + snapshot + "}, live= {" + live + "}, render= " + render + "}, body= {" + body + "}";
	}

	/**
	 * Called before the simulation is polled for an update. <p> This aligns the body's transform with Spout's if someone moves without physics. </p>
	 */
	public void onPrePhysicsTick() {
		if (body == null) {
			return;
		}
		final org.spout.physics.math.Vector3 positionLiveToReact = ReactConverter.toReactVector3(live.getPosition());
		body.getTransform().setPosition(positionLiveToReact);
	}

	/**
	 * Called after the simulation was polled for an update. <p> This updates Spout's live with the transform of the body. The render transform is updated with interpolation from the body </p>
	 */
	public void onPostPhysicsTick(float dt) {
		interpolateAndSetRender(dt);
	}

	/**
	 * Interpolates the live transform and sets the output to the render transform. <p/> This is necessary for smooth rendering.
	 */
	public void interpolateAndSetRender(float dt) {
		if (render.isEmpty()) {
			render.set(snapshot);
		}
		//Only interpolate if same world
		if (render.getPosition().getWorld() != getOwner().getWorld()) {
			return;
		}

		//TODO: Untangle Camera position/rotation from render transform
		//Spout Interpolation
		if (body == null) {
			if (snapshot.equals(live)) {
				return;
			}
			final float step = dt * (60f / 20f);

			final Point position = live.getPosition();
			final Quaternion rotation = live.getRotation();
			final Vector3 scale = live.getScale();

			render.setPosition(render.getPosition().multiply(1 - step).add(position.multiply(dt)));

			final Quaternion renderRot = render.getRotation();
			render.setRotation(new Quaternion(renderRot.getX() * (1 - step) + rotation.getX() * step,
					renderRot.getY() * (1 - step) + rotation.getY() * step,
					renderRot.getZ() * (1 - step) + rotation.getZ() * step,
					renderRot.getW() * (1 - step) + rotation.getW() * step, false)
			);

			render.setScale(render.getScale().multiply(1 - step).add(scale.multiply(step)));
		} else {
			final Transform physicsLive = ReactConverter.toSpoutTransform(body.getTransform(), live.getPosition().getWorld(), live.getScale());
			if (!live.equals(physicsLive)) {
				live.set(physicsLive);
				sync();
			}
			final Transform physicsRender = ReactConverter.toSpoutTransform(body.getInterpolatedTransform(), live.getPosition().getWorld(), live.getScale());
			if (!render.equals(physicsRender)) {
				render.set(physicsRender);
			}
		}
	}

	public void copySnapshot() {
		snapshot.set(live);
	}

	private void sync() {
		if (getOwner() instanceof Player && Spout.getPlatform() == Platform.SERVER) {
			((Player) getOwner()).getNetwork().forceSync();
		}
	}
}
