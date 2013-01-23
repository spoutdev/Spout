package org.spout.api.component.impl;

import org.spout.api.component.type.EntityComponent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

/**
 * Component that gives the owner the characteristics to be a part of a Scene.
 * <p/>
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
	public abstract Transform getTransform();

	// Position

	/**
	 * Gets the {@link Point} representing the location where the {@link org.spout.api.entity.Entity} is within the scene.
	 * <p>
	 * The Point is guarantee'd to always be valid.
	 * </p>
	 * @return The Point in the scene.
	 */
	public abstract Point getPosition();

	// Rotation

	/**
	 * Gets the {@link Quaternion} representing the rotation of the {@link org.spout.api.entity.Entity} within the scene.
	 * <p>
	 * The Quaternion is gurrantee'd to always be valid.
	 * </p>
	 * @return The Quaternion in the scene.
	 */
	public abstract Quaternion getRotation();

	// Scale

	/**
	 * Gets the {@link Vector3} representing the scale of the {@link org.spout.api.entity.Entity} within the scene.
	 * <p>
	 * The Scale is gurrantee'd to always be valid.
	 * </p>
	 * @return The Scale (Vector3) in the scene.
	 */
	public abstract Vector3 getScale();

	// Physics

	/**
	 * Translates this {@link org.spout.api.entity.Entity} from its current {@link Point} to the Point
	 * that is the addition of the {@link Vector3} provided.
	 * <p>
	 * For example, if I want to move an Entity up one (Up being the y-axis), I would do a translate(new Vector3(0, 1, 0));
	 * <p/>
	 * Bear in mind, doing a translate does so without physics and instead the position of the Entity will be directly set within its physics
	 * transform.
	 * </p>
	 * @param howMuch A Vector3 which will be added to the current Point (position).
	 * @return SceneComponent Returns this component for chaining.
	 */
	public abstract SceneComponent translate(Vector3 howMuch);

	/**
	 * Rotates this {@link org.spout.api.entity.Entity} from its current {@link org.spout.api.math.Quaternion} to the Quaternion
	 * that is the addition of the Quaternion provided.
	 * <p>
	 * For example, if I want to rotate an Entity upwards (which is moving its yaw), I would do a rotate(new Quaternion(0, 1, 0, 0));
	 * <p/>
	 * Bear in mind, doing a rotate does so without physics and instead the rotation of the Entity will be directly set within its physics
	 * transform.
	 * </p>
	 * @param howMuch A Quaternion which will be added to the current Quaternion (rotation).
	 * @return SceneComponent Returns this component for chaining.
	 */
	public abstract SceneComponent rotate(Quaternion howMuch);

	/**
	 * Scales this {@link org.spout.api.entity.Entity} from its current scale to the {@link Vector3} representing the new scale which is
	 * an addition of the Vector3 provided.
	 * <p>
	 * For example, if I want to scale an Entity to be taller (which is scaling its y-factor), I would do a scale(new Vector3(0, 1, 0));
	 * </p>
	 * @param howMuch A Vector3 which will be added to the current Vector3 (scale).
	 * @return SceneComponent Returns this component for chaining.
	 */
	public abstract SceneComponent scale(Vector3 howMuch);

	/**
	 * Impulse performs a translation of the {@link org.spout.api.entity.Entity} by the {@link Vector3} from the Vector3 offset.
	 * <p>
	 * Impulse is a force across delta time. A few rules apply.
	 * - The entity must have a mass > 0 (ie not a static object).
	 * - The offset is in world space. This means the impulse is applied from the offset provided.
	 * - Entities of higher masses need greater impulses to move. Can't get movement to occur? Lower mass or apply greater impulse.
	 * <p/>
	 * For example, if I want to propel an entity forward, like a wave of water pushing an entity downstream, I would do the following.
	 * <p/>
	 * // Vector3.FORWARD = 0, 0, 1
	 * scene.impulse(Vector3.FORWARD, new Vector3(getPosition().subtract(getTransform().getForward()));
	 * // The above adds 1 to the forwardness of the Entity every simulation step (if applied in onTick for example)
	 * </p>
	 * @param howMuch The Vector3 impulse (force) to apply.
	 * @param offset The offset within the world to apply the impulse from.
	 * @return SceneComponent Returns this component for chaining.
	 */
	public abstract SceneComponent impulse(Vector3 howMuch, Vector3 offset);

	/**
	 * Force performs a translation of the {@link org.spout.api.entity.Entity} by the {@link Vector3} from the Vector3 offset.
	 * <p>
	 * Force is, as it sounds, an instant force to the Entity. A few rules apply.
	 * - The entity must have a mass > 0 (ie not a static object).
	 * - The offset is in world space. This means the force is applied from the offset provided.
	 * - Entities of higher masses need greater forces to move. Can't get movement to occur? Lower mass or apply greater force.
	 * <p/>
	 * For example, if I want to propel an entity right in an instant, such as simulating a two entities hitting each other, I would do the following.
	 * <p/>
	 * // Vector3,RIGHT = 1, 0, 0
	 * scene.force(Vector3.RIGHT, new Vector3(getPosition().subtract(getTransform.getRight()));
	 * // The above forces the Entity's momentum by 1 every simulation step (if applied in onTick for example).
	 * </p>
	 * @param howMuch The Vector3 force to apply.
	 * @param offset The offset within the world to apply the force from.
	 * @return SceneComponent Returns this component for chaining.
	 */
	public abstract SceneComponent force(Vector3 howMuch, Vector3 offset);

	// Other

	@Override
	public boolean isDetachable() {
		return false;
	}
}
