package org.spout.engine.entity.component;

import javax.vecmath.Vector3f;

import com.bulletphysics.$Stack;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;

import org.spout.api.ClientOnly;
import org.spout.api.component.impl.SceneComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

/**
 * The Spout implementation of {@link SceneComponent}.
 *
 * TODO Afforess, make this thread-safe (properly).
 */
public class SpoutSceneComponent extends SceneComponent {
	private final Transform snapshot = new Transform();
	private final Transform live = new Transform();
	private final Transform render = new Transform();

	@Override
	public Transform getTransform() {
		return snapshot;
	}

	@Override
	public SceneComponent setTransform(Transform transform) {
		snapshot.set(transform);
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
		return null;
	}

	@Override
	public boolean isPositionDirty() {
		return false;
	}

	@Override
	public Quaternion getRotation() {
		return snapshot.getRotation();
	}

	@Override
	public SceneComponent setRotation(Quaternion rotation) {
		return null;
	}

	@Override
	public boolean isRotationDirty() {
		return false;
	}

	@Override
	public Vector3 getScale() {
		return snapshot.getScale();
	}

	@Override
	public SceneComponent setScale(Vector3 scale) {
		return null;
	}

	@Override
	public boolean isScaleDirty() {
		return false;
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
		return null;
	}

	@Override
	public SceneComponent force(Vector3 force, Vector3 offset) {
		return null;
	}

	@Override
	public SceneComponent torque(Vector3 torque) {
		return null;
	}

	@Override
	public SceneComponent impulseTorque(Vector3 torque) {
		return null;
	}

	@Override
	public void dampenMovement(float damp) {
	}

	@Override
	public SceneComponent dampenRotation(float damp) {
		return null;
	}

	@Override
	public CollisionShape getShape() {
		return null;
	}

	@Override
	public SceneComponent setShape(CollisionShape shape) {
		return null;
	}

	@Override
	public float getFriction() {
		return 0;
	}

	@Override
	public SceneComponent setFriction(float friction) {
		return null;
	}

	@Override
	public float getMass() {
		return 0;
	}

	@Override
	public SceneComponent setMass(float mass) {
		return this;
	}

	@Override
	public float getRestitution() {
		return 0;
	}

	@Override
	public SceneComponent setRestitution(float restitution) {
		return null;
	}

	@Override
	public Vector3 getMovementVelocity() {
		return null;
	}

	@Override
	public SceneComponent setMovementVelocity(Vector3 velocity) {
		return null;
	}

	@Override
	public Vector3 getRotationVelocity() {
		return null;
	}

	@Override
	public SceneComponent setRotationVelocity(Vector3 velocity) {
		return null;
	}

	@Override
	public SceneComponent setActivated(boolean activate) {
		return null;
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
		return null;
	}

	public Vector3 getScaleLive() {
		return null;
	}

	public CollisionObject getObject() {
		return null;
	}

	public void copySnapshot() {

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
		 * @param in An Interpolated Physics Transform, to be used by SpoutRenderer.
		 */
		@Override
		public void setWorldTransform(com.bulletphysics.linearmath.Transform in) {
			/*
				This is only to send the helper function the world and current scale of the entity in the scene.
				Physics completely ignores scale and has no concept of a SpoutWorld so we must "help the helper".
			 */
			final Transform liveContainer = new Transform(); //TODO Possibly pass the helper World and Scale to bypass the need for a transform.
			liveContainer.setPosition(new Point(Vector3.ZERO, scene.getOwner().getWorld()));
			liveContainer.setScale(scene.getScaleLive());
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
			scene.getObject().getWorldTransform(physicsContainer);
			scene.getTransformLive().set(MathHelper.toSceneTransform(liveContainer, physicsContainer));
		}
	}
}