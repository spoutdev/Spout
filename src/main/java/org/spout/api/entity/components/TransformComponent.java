package org.spout.api.entity.components;

import org.spout.api.entity.BaseComponent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

public class TransformComponent extends BaseComponent {
	private Transform wrapped;
	
	public TransformComponent() {
		wrapped = new Transform();
	}
	
	public Point getPosition() {
		return wrapped.getPosition();
	}
	
	public void setPosition(Point position) {
		wrapped.setPosition(position);
	}
	
	public Quaternion getRotation() {
		return wrapped.getRotation();
	}
	
	public void setRotation(Quaternion rotation) {
		wrapped.setRotation(rotation);
	}
	
	public Vector3 getScale() {
		return wrapped.getScale();
	}
	
	public void setScale(Vector3 scale) {
		wrapped.setScale(scale);
	}
	
	/**
	 * Moves the entity by the provided vector<br/>
	 * @param amount to move the entity
	 */
	public void translate(Vector3 amount) {
		wrapped.getPosition().add(amount);
	}

	/**
	 * Moves the entity by the provided vector
	 * @param x offset
	 * @param y offset
	 * @param z offset
	 */
	public void translate(float x, float y, float z) {
		wrapped.setPosition(wrapped.getPosition().add(x, y, z));
	}

	/**
	 * Rotates the entity about the provided axis by the provided angle
	 * @param ang
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotate(float w, float x, float y, float z) {
		wrapped.setRotation(wrapped.getRotation().rotate(w, x, y, z));
	}

	/**
	 * Rotates the entity by the provided rotation
	 * @param rot
	 */
	public void rotate(Quaternion rot) {
		wrapped.setRotation(wrapped.getRotation().multiply(rot));
	}

	/**
	 * Scales the entity by the provided amount
	 * @param amount
	 */
	public void scale(Vector3 amount) {
		wrapped.setScale(wrapped.getScale().multiply(amount));
	}

	/**
	 * Scales the entity by the provided amount
	 * @param x
	 * @param y
	 * @param z
	 */
	public void scale(float x, float y, float z) {
		wrapped.setScale(wrapped.getScale().multiply(x, y, z));
	}

	/**
	 * Pitches the entity by the provided amount
	 * @param angle
	 */
	public void pitch(float angle) {
		setPitch(angle);
	}
	
	/**
	 * Yaws the entity by the provided amount
	 * @param angle
	 */
	public void yaw(float angle) {
		setYaw(angle);
	}
	
	/**
	 * Rolls the entity by the provided amount
	 * @param angle
	 */
	public void roll(float angle) {
		setRoll(angle);
	}

	/**
	 * Gets the entities current pitch, or vertical angle.
	 * @return pitch of the entity
	 */
	public float getPitch() {
		return getRotation().getPitch();
	}

	/**
	 * Gets the entities current yaw, or horizontal angle.
	 * @return yaw of the entity.
	 */
	public float getYaw() {
		return getRotation().getYaw();
	}

	/**
	 * Gets the entities current roll as a float.
	 * @return roll of the entity
	 */
	public float getRoll() {
		return getRotation().getRoll();
	}

	/**
	 * Sets the pitch of the entity.
	 * @param ang
	 */
	public void setPitch(float angle) {
		setAxisAngles(getPitch(), getYaw(), angle);		
	}

	/**
	 * Sets the roll of the entity.
	 * @param ang
	 */
	public void setRoll(float angle) {
		setAxisAngles(getPitch(), getYaw(), angle);		
	}

	/**
	 * sets the yaw of the entity.
	 * @param ang
	 */
	public void setYaw(float angle) {
		setAxisAngles(getPitch(), angle, getRoll());		
	}
	
	private void setAxisAngles(float pitch, float yaw, float roll) {
		setRotation(MathHelper.rotation(pitch, yaw, roll));
	}	
}
