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
package org.spout.api.geo.discrete;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.spout.api.geo.World;
import org.spout.math.vector.Vector3;
import org.spout.api.util.StringUtil;
import org.spout.api.util.concurrent.SpinLock;
import org.spout.api.util.thread.annotation.Threadsafe;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix3;

@Threadsafe
public final class Transform implements Serializable {
	private static final long serialVersionUID = 2L;
	private final transient SpinLock lock = new SpinLock();
	private Point position;
	private Quaternion rotation;
	private Vector3 scale;

	public Transform() {
		this(Point.invalid, Quaternion.IDENTITY, Vector3.ZERO);
	}

	public Transform(Transform transform) {
		set(transform);
	}

	public Transform(Point position, Quaternion rotation, Vector3 scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Point getPosition() {
		try {
			lock.lock();
			return position;
		} finally {
			lock.unlock();
		}
	}

	public Transform setPosition(Point position) {
		try {
			lock.lock();
			this.position = position;
		} finally {
			lock.unlock();
		}
		return this;
	}

	public Transform translate(float x, float y, float z) {
		return translate(new Vector3(x, y, z));
	}

	public Transform translate(Vector3 offset) {
		try {
			lock.lock();
			this.position = this.position.add(offset);
		} finally {
			lock.unlock();
		}
		return this;
	}

	public Transform rotate(Quaternion offset) {
		try {
			lock.lock();
			this.rotation = Quaternion.fromAngleRadAxis(offset.getW(), offset.getX(), offset.getY(), offset.getZ());
		} finally {
			lock.unlock();
		}
		return this;
	}

	public Transform scale(Vector3 offset) {
		try {
			lock.lock();
			this.scale = this.scale.add(offset);
		} finally {
			lock.unlock();
		}
		return this;
	}

	public Transform translateAndSetRotation(Vector3 offset, Quaternion rotation) {
		try {
			lock.lock();
			this.position = this.position.add(offset);
			this.rotation = rotation;
		} finally {
			lock.unlock();
		}
		return this;
	}

	public Quaternion getRotation() {
		try {
			lock.lock();
			return rotation;
		} finally {
			lock.unlock();
		}
	}

	public Transform setRotation(Quaternion rotation) {
		try {
			lock.lock();
			this.rotation = rotation;
		} finally {
			lock.unlock();
		}
		return this;
	}

	public Vector3 getScale() {
		try {
			lock.lock();
			return scale;
		} finally {
			lock.unlock();
		}
	}

	public Transform setScale(Vector3 scale) {
		try {
			lock.lock();
			this.scale = scale;
		} finally {
			lock.unlock();
		}
		return this;
	}

	/**
	 * Atomically sets the value of this transform to the value of another transform
	 *
	 * @param transform the other transform
	 * @return this transform
	 */
	@Threadsafe
	public Transform set(Transform transform) {
		if (transform == null) {
			throw new NullPointerException("Transform can not be a null argument!");
		}

		try {
			SpinLock.dualLock(lock, transform.lock);
			setUnsafe(transform.position, transform.rotation, transform.scale);
		} finally {
			SpinLock.dualUnlock(lock, transform.lock);
		}
		return this;
	}

	/**
	 * Atomically sets the value of this transform.
	 *
	 * @param world the world
	 * @param px the x coordinate of the position
	 * @param py the y coordinate of the position
	 * @param pz the z coordinate of the position
	 * @param rx the x coordinate of the quaternion
	 * @param ry the y coordinate of the quaternion
	 * @param rz the z coordinate of the quaternion
	 * @param rw the w coordinate of the quaternion
	 * @param sx the x coordinate of the scale
	 * @param sy the y coordinate of the scale
	 * @param sz the z coordinate of the scale
	 * @return this transform
	 */
	@Threadsafe
	public Transform set(World world, float px, float py, float pz, float rx, float ry, float rz, float rw, float sx, float sy, float sz) {
		return this.set(new Point(world, px, py, pz), new Quaternion(rx, ry, rz, rw), new Vector3(sx, sy, sz));
	}

	/**
	 * Atomically sets this point to the given components
	 *
	 * @return this transform
	 */
	@Threadsafe
	public Transform set(Point p, Quaternion r, Vector3 s) {
		try {
			lock.lock();
			setUnsafe(p, r, s);
		} finally {
			lock.unlock();
		}
		return this;
	}

	private void setUnsafe(Point p, Quaternion r, Vector3 s) {
		this.position = p;
		this.rotation = r;
		this.scale = s;
	}

	/**
	 * Creates a Transform that is a copy of this transform
	 *
	 * @return the snapshot
	 */
	@Threadsafe
	public Transform copy() {
		try {
			lock.lock();
			return new Transform(position, rotation, scale);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets a String representation of this transform
	 *
	 * Note: unsafe, could return torn values
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + StringUtil.toString(position, rotation, scale);
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder(41, 63);
		try {
			lock.lock();
			builder.append(position).append(rotation).append(scale);
		} finally {
			lock.unlock();
		}
		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Transform)) {
			return false;
		}
		Transform t = (Transform) other;
		try {
			SpinLock.dualLock(lock, t.lock);
			return position.equals(t.position) && rotation.equals(t.rotation) && scale.equals(t.scale);
		} finally {
			SpinLock.dualUnlock(lock, t.lock);
		}
	}

	/**
	 * Returns the 4x4 matrix that represents this transform object
	 */
	public Matrix3 toMatrix() {
		Matrix3 translate = Matrix3.createTranslation(getPosition().toVector2());
		Matrix3 rotate = Matrix3.createRotation(getRotation());
		Matrix3 scale = Matrix3.createScaling(getScale());
		return scale.mul(rotate).mul(translate);
	}

	/**
	 * Returns a unit vector that points in the forward direction of this transform
	 */
	public Vector3 forwardVector() {
		return Matrix3.createRotation(getRotation()).transform(Vector3.FORWARD);
	}

	/**
	 * Returns a unit vector that points right in relation to this transform
	 */
	public Vector3 rightVector() {
		return Matrix3.createRotation(getRotation()).transform(Vector3.RIGHT);
	}

	/**
	 * Returns a unit vector that points up in relation to this transform
	 */
	public Vector3 upVector() {
		return Matrix3.createRotation(getRotation()).transform(Vector3.UP);
	}

	/**
	 * Returns if this Transform is "empty" <p> Empty is defined by Position, {@link Point}, of the transform equaling {@link Point#invalid}, Rotation, {@link Quaternion}, of the transform equaling
	 * {@link Quaternion#IDENTITY}, and Scale, {@link Vector3}, equaling {@link Vector3#ONE}.
	 *
	 * @return True if empty, false if not
	 */
	public boolean isEmpty() {
		try {
			lock.lock();
			return position.equals(Point.invalid) && rotation.equals(Quaternion.IDENTITY) && scale.equals(Vector3.ONE);
		} finally {
			lock.unlock();
		}
	}
}
