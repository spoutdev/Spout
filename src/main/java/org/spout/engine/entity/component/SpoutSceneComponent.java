package org.spout.engine.entity.component;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;

import org.spout.api.ClientOnly;
import org.spout.api.component.impl.SceneComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

import org.spout.engine.world.SpoutRegion;

/**
 * The Spout implementation of {@link SceneComponent}.
 *
 * TODO Afforess, make this thread-safe (properly).
 */
public class SpoutSceneComponent extends SceneComponent {
	private final Transform snapshot = new Transform();
	private final Transform live = new Transform();
	private final Transform render = new Transform();
	private final Vector3f inertia = new Vector3f();
	private RigidBody body;

	@Override
	public void onAttached() {
		if (getOwner() instanceof Player) {
			throw new IllegalStateException("This component is not designed for Players.");
		}
	}

	@Override
	public Transform getTransform() {
		return snapshot;
	}

	@Override
	public SceneComponent setTransform(Transform transform) {
		snapshot.set(transform);
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
		validateBody();
		body.applyImpulse(MathHelper.toVector3f(impulse), MathHelper.toVector3f(offset));
		return this;
	}

	@Override
	public SceneComponent force(Vector3 force, Vector3 offset) {
		validateBody();
		body.applyForce(MathHelper.toVector3f(force), MathHelper.toVector3f(offset));
		return this;
	}

	@Override
	public SceneComponent torque(Vector3 torque) {
		validateBody();
		body.applyTorque(MathHelper.toVector3f(torque));
		return this;
	}

	@Override
	public SceneComponent impulseTorque(Vector3 torque) {
		validateBody();
		body.applyTorqueImpulse(MathHelper.toVector3f(torque));
		return this;
	}

	@Override
	public SceneComponent dampenMovement(float damp) {
		validateBody();
		body.setDamping(damp, body.getAngularDamping());
		return this;
	}

	@Override
	public SceneComponent dampenRotation(float damp) {
		validateBody();
		body.setDamping(body.getLinearDamping(), damp);
		return this;
	}

	@Override
	public CollisionShape getShape() {
		validateBody();
		return body.getCollisionShape();
	}

	@Override
	public SceneComponent setShape(float mass, CollisionShape shape) {
		//Calculate inertia
		shape.calculateLocalInertia(getMass(), inertia);
		//Construct body blueprint
		final RigidBodyConstructionInfo blueprint = new RigidBodyConstructionInfo(mass, new SpoutMotionState(getOwner()), shape, inertia);
		body = new RigidBody(blueprint);
		body.activate();
		updatePhysicsSpace();
		return this;
	}

	@Override
	public float getFriction() {
		validateBody();
		return body.getFriction();
	}

	@Override
	public SceneComponent setFriction(float friction) {
		validateBody();
		body.setFriction(friction);
		updatePhysicsSpace();
		return this;
	}

	@Override
	public float getMass() {
		validateBody();
		return body.getInvMass();
	}

	@Override
	public float getRestitution() {
		validateBody();
		return body.getRestitution();
	}

	@Override
	public SceneComponent setRestitution(float restitution) {
		validateBody();
		body.setRestitution(restitution);
		updatePhysicsSpace();
		return this;
	}

	@Override
	public Vector3 getMovementVelocity() {
		validateBody();
		//TODO Snapshot/live values needed?
		return MathHelper.toVector3(body.getLinearVelocity(new Vector3f()));
	}

	@Override
	public SceneComponent setMovementVelocity(Vector3 velocity) {
		validateBody();
		body.setLinearVelocity(MathHelper.toVector3f(velocity));
		//TODO May need to perform a Physics space update...testing needed.
		return this;
	}

	@Override
	public Vector3 getRotationVelocity() {
		validateBody();
		//TODO Snapshot/live values needed?
		return MathHelper.toVector3(body.getAngularVelocity(new Vector3f()));
	}

	@Override
	public SceneComponent setRotationVelocity(Vector3 velocity) {
		validateBody();
		body.setAngularVelocity(MathHelper.toVector3f(velocity));
		//TODO May need to perform a Physics space update...testing needed.
		return this;
	}

	@Override
	public SceneComponent setActivated(boolean activate) {
		body.setActivationState(activate == true ? CollisionObject.ACTIVE_TAG : CollisionObject.DISABLE_SIMULATION);
		return this;
	}

	/**
	 * Gets the live transform state of this {@link org.spout.api.entity.Entity} within the scene.
	 *
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
	}

	/**
	 * Updates a body within the simulation.
	 *
	 * Due to how TeraBullet caches bodies in the simulation, updating attributes of a body tend to have no
	 * effect as it uses cached values. This method does a workaround by hotswapping bodies.
	 *
	 * This method should be entirely safe to use as physics isn't ticked until Stage 2, this method is only
	 * available in Stage 1.
	 *
	 * TODO See if clearing cache pairs solves this without hotswapping?
	 */
	public void updatePhysicsSpace() {
		final SpoutRegion simulation = (SpoutRegion) getOwner().getRegion();
		if (simulation == null) {
			throw new IllegalStateException("Attempting to update a body within a simulation but the region is null!");
		}
		//TODO Afforess confirm that I'm crazy and an Entity can't have a region without being spawned...
		if (!getOwner().isSpawned()) {
			return;
		}
		//swap
		//simulation.removePhysics(getOwner());
		//simulation.addPhysics(getOwner());
	}

	/**
	 * Checks to see if the body isn't null and if so, throws an exception.
	 */
	private void validateBody() {
		if (body == null) {
			throw new IllegalStateException("You need to give the Entity a shape (with setShape(mass, shape) before manipulating it");
		}
	}

	/**
	 * Forces a physics body translation without forces or any physics corrections.
	 */
	private void forcePhysicsUpdate() {
		body.setWorldTransform(MathHelper.toPhysicsTransform(live));
		body.clearForces(); //TODO May not be correct here, needs testing.
	}

	private final class SpoutMotionState extends DefaultMotionState {
		private final SpoutSceneComponent scene;

		public SpoutMotionState(Entity entity) {
			super(MathHelper.toPhysicsTransform(((SpoutSceneComponent) entity.getScene()).getTransformLive()));
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
			final com.bulletphysics.linearmath.Transform physicsTransform = MathHelper.toPhysicsTransform(scene.getTransformLive());
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
			scene.getRenderTransform().set(MathHelper.toSceneTransform(liveContainer, in));
			/*
				Now we will set the Scene's live transform to that of the Physics' transform.

				As said above, the transform passed in is interpolated and as such is not suitable for live. When setWorldTransform is called,
				the physics has already ticked (for this game tick) and then interpolation occurs based on the timestep of the simulation.
				Thankfully Bullet provides a non-interpolated "live" (after physics ticked) transform available via getWorldTransform. We
				will simply set live to that.
			 */
			final com.bulletphysics.linearmath.Transform physicsContainer = new com.bulletphysics.linearmath.Transform();
			scene.getBody().getWorldTransform(physicsContainer);
			scene.getTransformLive().set(MathHelper.toSceneTransform(liveContainer, physicsContainer));
		}
	}
}