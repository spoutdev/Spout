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

import javax.vecmath.Vector3f;
import java.util.concurrent.atomic.AtomicReference;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;

import org.spout.api.ClientOnly;
import org.spout.api.component.impl.SceneComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.GenericMath;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.math.VectorMath;

import org.spout.engine.world.SpoutRegion;

/**
 * The Spout implementation of {@link SceneComponent}.
 */
public class SpoutSceneComponent extends SceneComponent {
	private final Transform snapshot = new Transform();
	private final Transform live = new Transform();
	private final AtomicReference<SpoutRegion> simulationRegion = new AtomicReference<SpoutRegion>(null);
	private RigidBody body;

	//Client/Rendering
	private final Transform render = new Transform();
	private Vector3 speed = Vector3.ONE;
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

	@Override
	public SceneComponent setTransform(Transform transform) {
		live.set(transform);
		if (body != null) {
			forcePhysicsUpdate();
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
	public SceneComponent setPosition(Point point) {
		live.setPosition(point);
		if (body != null) {
			forcePhysicsUpdate();
		}
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
	public SceneComponent setRotation(Quaternion rotation) {
		live.setRotation(rotation);
		if (body != null) {
			forcePhysicsUpdate();
		}
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
	public SceneComponent setScale(Vector3 scale) {
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
	public SceneComponent translate(Vector3 point) {
		live.translate(point);
		return this;
	}

	@Override
	public SceneComponent rotate(Quaternion rotate) {
		live.rotate(rotate);
		return this;
	}

	@Override
	public SceneComponent scale(Vector3 scale) {
		live.scale(scale);
		return this;
	}

	@Override
	public SceneComponent impulse(Vector3 impulse, Vector3 offset) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.applyImpulse(VectorMath.toVector3f(impulse), VectorMath.toVector3f(offset));
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent impulse(Vector3 impulse) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.applyCentralImpulse(VectorMath.toVector3f(impulse));
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent force(Vector3 force, Vector3 offset) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.applyForce(VectorMath.toVector3f(force), VectorMath.toVector3f(offset));
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent force(Vector3 force) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.applyCentralForce(VectorMath.toVector3f(force));
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent torque(Vector3 torque) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.applyTorque(VectorMath.toVector3f(torque));
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent impulseTorque(Vector3 torque) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.applyTorqueImpulse(VectorMath.toVector3f(torque));
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent dampenMovement(float damp) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setDamping(damp, body.getAngularDamping());
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent dampenRotation(float damp) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setDamping(body.getLinearDamping(), damp);
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public CollisionShape getShape() {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().readLock().lock();
			return body.getCollisionShape();
		} finally {
			region.getPhysicsLock().readLock().unlock();
		}
	}

	@Override
	public SceneComponent setShape(final float mass, final CollisionShape shape) {
		//TODO: allowing api to setShape more than once could cause tearing/threading issues
		final RigidBody previous = body;
		//Calculate inertia
		final Vector3f inertia = new Vector3f();
		shape.calculateLocalInertia(mass, inertia);
		//Construct body blueprint
		final RigidBodyConstructionInfo blueprint = new RigidBodyConstructionInfo(mass, new SpoutMotionState(getOwner()), shape, inertia);
		body = new RigidBody(blueprint);
		body.activate();
		final SpoutRegion region = simulationRegion.get();
		if (region != null) {
			try {
				region.getPhysicsLock().writeLock().lock();
				if (previous != null) {
					region.getSimulation().removeRigidBody(previous);
				}
				region.getSimulation().addRigidBody(body);
			} finally {
				region.getPhysicsLock().writeLock().unlock();
			}
		}
		return this;
	}

	@Override
	public float getFriction() {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().readLock().lock();
			return body.getFriction();
		} finally {
			region.getPhysicsLock().readLock().unlock();
		}
	}

	@Override
	public SceneComponent setFriction(float friction) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setFriction(friction);
			updatePhysicsSpace();
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public float getMass() {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().readLock().lock();
			return body.getInvMass();
		} finally {
			region.getPhysicsLock().readLock().unlock();
		}
	}

	@Override
	public float getRestitution() {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().readLock().lock();
			return body.getRestitution();
		} finally {
			region.getPhysicsLock().readLock().unlock();
		}
	}

	@Override
	public SceneComponent setRestitution(float restitution) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setRestitution(restitution);
			updatePhysicsSpace();
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public Vector3 getMovementVelocity() {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().readLock().lock();
			//TODO Snapshot/live values needed?
			return VectorMath.toVector3(body.getLinearVelocity(new Vector3f()));
		} finally {
			region.getPhysicsLock().readLock().unlock();
		}
	}

	@Override
	public SceneComponent setMovementVelocity(Vector3 velocity) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setLinearVelocity(VectorMath.toVector3f(velocity));
			//TODO May need to perform a Physics space update...testing needed.
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public Vector3 getRotationVelocity() {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().readLock().lock();
			//TODO Snapshot/live values needed?
			return VectorMath.toVector3(body.getAngularVelocity(new Vector3f()));
		} finally {
			region.getPhysicsLock().readLock().unlock();
		}
	}

	@Override
	public SceneComponent setRotationVelocity(Vector3 velocity) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setAngularVelocity(VectorMath.toVector3f(velocity));
			//TODO May need to perform a Physics space update...testing needed.
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public SceneComponent setActivated(boolean activate) {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setActivationState(activate == true ? CollisionObject.ACTIVE_TAG : CollisionObject.DISABLE_SIMULATION);
			return this;
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	@Override
	public boolean isActivated() {
		return body != null && body.getActivationState() == CollisionObject.ACTIVE_TAG;
	}

	/**
	 * Gets the live transform state of this {@link org.spout.api.entity.Entity} within the scene.
	 * <p/>
	 * Keep in mind it is completely unstable; the API can change it at anytime during Stage 1 of the
	 * tick.
	 * @return The Transform representing the live state.
	 */
	public Transform getTransformLive() {
		return live;
	}

	/**
	 * Gets the {@link Transform} this {@link org.spout.api.entity.Entity} had within the last game tick
	 * of the scene with interpolation applied (so it appears smooth to users).
	 * <p/>
	 * The render transform is simply the transform of the entity clients see within the scene.
	 * @return The Transform, interpolated, as of the last game tick.
	 */
	@ClientOnly
	public Transform getRenderTransform() {
		return render;
	}

	/**
	 * Gets the {@link RigidBody} that this {@link Entity} has within Physics space.
	 * @return The collision object.
	 */
	public RigidBody getBody() {
		return body;
	}

	/**
	 * Snapshots values for the next tick.
	 */
	public void copySnapshot() {
		snapshot.set(live);

		//Have Spout interpolate if this Entity has no valid body.
		if (body == null) {
			float ratio = 80f / 20f;
			speed = snapshot.getPosition().subtract(render.getPosition()).multiply(ratio);
			rotate = snapshot.getRotation();
			scale = snapshot.getScale().subtract(render.getScale()).multiply(ratio);
		}
	}

	public void updateRender(float dt) {
		render.translate(speed.multiply(dt));
		Quaternion q = render.getRotation();
		render.setRotation(new Quaternion(	q.getX()*(1-dt) + rotate.getX()*dt,
													q.getY()*(1-dt) + rotate.getY()*dt,
													q.getZ()*(1-dt) + rotate.getZ()*dt,
													q.getW()*(1-dt) + rotate.getW()*dt,false));
		render.setScale(render.getScale().add(scale.multiply(dt)));
	}

	/**
	 * Updates a body within the simulation.
	 * <p/>
	 * Due to how TeraBullet caches bodies in the simulation, updating attributes of a body tend to have no
	 * effect as it uses cached values. This method does a workaround by hotswapping bodies.
	 * <p/>
	 * This method should be entirely safe to use as physics isn't ticked until Stage 2, this method is only
	 * available in Stage 1.
	 * <p/>
	 * TODO See if clearing cache pairs solves this without hotswapping?
	 */
	private void updatePhysicsSpace() {
		final SpoutRegion region = simulationRegion.get();
		//swap
		region.getSimulation().removeRigidBody(body);
		region.getSimulation().addRigidBody(body);
	}

	public void simulate(SpoutRegion region) {
		SpoutRegion previous = simulationRegion.getAndSet(region);
		if (previous != region && body != null) {
			try {
				region.getPhysicsLock().writeLock().lock();
				region.getSimulation().addRigidBody(body);
			} finally {
				region.getPhysicsLock().writeLock().unlock();
			}
		}
	}

	/**
	 * Checks to see if the body isn't null and if so, throws an exception.
	 */
	private void validateBody(final SpoutRegion region) {
		if (body == null) {
			throw new IllegalStateException("You need to give the Entity a shape (with setShape(mass, shape) before manipulating it");
		}
		if (region == null) {
			throw new IllegalStateException("Attempting to update a body within a simulation but the region is null!");
		}
	}

	/**
	 * Forces a physics body translation without forces or any physics corrections.
	 */
	private void forcePhysicsUpdate() {
		final SpoutRegion region = simulationRegion.get();
		validateBody(region);
		try {
			region.getPhysicsLock().writeLock().lock();
			body.setWorldTransform(GenericMath.toPhysicsTransform(live));
			body.clearForces(); //TODO May not be correct here, needs testing.
		} finally {
			region.getPhysicsLock().writeLock().unlock();
		}
	}

	private final class SpoutMotionState extends DefaultMotionState {
		private final SpoutSceneComponent scene;

		public SpoutMotionState(Entity entity) {
			super(GenericMath.toPhysicsTransform(((SpoutSceneComponent) entity.getScene()).getTransformLive()));
			this.scene = (SpoutSceneComponent) entity.getScene();
		}

		/**
		 * Called pre-physics tick by TeraBullet. This is used to figure out where in Scene space our object is then perform
		 * physics and figure out where the object would be in Physics space.
		 * @param out
		 * @return
		 */
		@Override
		public com.bulletphysics.linearmath.Transform getWorldTransform(com.bulletphysics.linearmath.Transform out) {
			final com.bulletphysics.linearmath.Transform physicsTransform = GenericMath.toPhysicsTransform(scene.getTransformLive());
			out.set(physicsTransform);
			return out;
		}

		/**
		 * Called post-physics tick by TeraBullet. This is used to update Scene space with transforms calculated from Physics space.
		 * @param in An interpolated Physics Transform, to be used by SpoutRenderer.
		 */
		@Override
		public void setWorldTransform(com.bulletphysics.linearmath.Transform in) {
			/*
				This is only to send the helper function the world and current scale of the entity in the scene.
				Physics completely ignores scale and has no concept of a SpoutWorld so we must "help the helper".
			 */
			final Transform liveContainer = new Transform(); //TODO Possibly pass the helper World and Scale to bypass the need for a transform.
			liveContainer.setPosition(scene.getTransformLive().getPosition());
			liveContainer.setScale(scene.getTransformLive().getScale());
			/*
				Now we can set render and live

				The Transform passed into this method has been interpolated, ready for graphics. We don't set it to the live
				as live needs to be the transform from the last physics tick. We will handle this momentarily.
			 */
			scene.getRenderTransform().set(GenericMath.toSceneTransform(liveContainer, in));
			/*
				Now we will set the Scene's live transform to that of the Physics' transform.

				As said above, the transform passed in is interpolated and as such is not suitable for live. When setWorldTransform is called,
				the physics has already ticked (for this game tick) and then interpolation occurs based on the timestep of the simulation.
				Thankfully Bullet provides a non-interpolated "live" (after physics ticked) transform available via getWorldTransform. We
				will simply set live to that.
			 */
			final com.bulletphysics.linearmath.Transform physicsContainer = new com.bulletphysics.linearmath.Transform();
			scene.getBody().getWorldTransform(physicsContainer);
			scene.getTransformLive().set(GenericMath.toSceneTransform(liveContainer, physicsContainer));
		}
	}
}