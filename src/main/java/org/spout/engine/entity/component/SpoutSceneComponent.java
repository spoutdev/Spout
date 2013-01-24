package org.spout.engine.entity.component;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.DefaultMotionState;

import org.spout.api.component.impl.SceneComponent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

/**
 * The Spout implementation of {@link SceneComponent}.
 */
public class SpoutSceneComponent extends SceneComponent {
	private final Transform snapshot = new Transform();
	private Transform live = new Transform();

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

	@Override
	public Transform getTransform() {
		return null;
	}

	@Override
	public SceneComponent setTransform(Transform transform) {
		return null;
	}

	@Override
	public boolean isTransformDirty() {
		return false;
	}

	@Override
	public Transform getRenderTransform() {
		return null;
	}

	@Override
	public Point getPosition() {
		return null;
	}

	@Override
	public Quaternion getRotation() {
		return null;
	}

	@Override
	public Vector3 getScale() {
		return null;
	}

	@Override
	public SceneComponent translate(Vector3 point) {
		return null;
	}

	@Override
	public SceneComponent rotate(Quaternion rotate) {
		return null;
	}

	@Override
	public SceneComponent scale(Vector3 scale) {
		return null;
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
		return null;
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

	private final class SpoutMotionState extends DefaultMotionState {

	}
}