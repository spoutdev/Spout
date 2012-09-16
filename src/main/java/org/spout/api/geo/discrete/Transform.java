/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.geo.discrete;

import java.io.Serializable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.geo.World;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringUtil;
import org.spout.api.util.concurrent.SpinLock;
import org.spout.api.util.thread.Threadsafe;

@ThreadSafe
public final class Transform implements Serializable {
	private static final long serialVersionUID = 2L;

	private final SpinLock lock = new SpinLock();
	private Point position; 
	private Quaternion rotation;
	private Vector3 scale;
	public Transform() {
		this(Point.invalid, Quaternion.IDENTITY, Vector3.ONE);
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

	public void setPosition(Point position) {
		try {
			lock.lock();
			this.position = position;
		} finally {
			lock.unlock();
		}
	}

	public Quaternion getRotation() {
		try {
			lock.lock();
			return rotation;
		} finally {
			lock.unlock();
		}
	}

	public void setRotation(Quaternion rotation) {
		try {
			lock.lock();
			this.rotation = rotation;
		} finally {
			lock.unlock();
		}
	}

	public Vector3 getScale() {
		try {
			lock.lock();
			return scale;
		} finally {
			lock.unlock();
		}
	}

	public void setScale(Vector3 scale) {
		try {
			lock.lock();
			this.scale = scale;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Atomically sets the value of this transform to the value of another
	 * transform
	 *
	 * @param transform the other transform
	 * @return true if successful
	 */
	@Threadsafe
	public void set(Transform transform) {
		if (transform == null) {
			throw new NullPointerException("Transform can not be a null argument!");
		}

		try {
			transform.lock.lock();
			set(transform.position, transform.rotation, transform.scale);
		} finally {
			transform.lock.unlock();
		}
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
	 */
	@Threadsafe
	public void set(World world, float px, float py, float pz, float rx, float ry, float rz, float rw, float sx, float sy, float sz) {
		this.set(new Point(world, px, py, pz), new Quaternion(rx, ry, rz, rw), new Vector3(sx, sy, sz));
	}

	/**
	 * Atomically sets this point to the given components
	 *
	 * @param p
	 * @param r
	 * @param s
	 */
	@Threadsafe
	public void set(Point p, Quaternion r, Vector3 s) {
		try {
			lock.lock();
			this.position = p;
			this.rotation = r;
			this.scale = s;
		} finally {
			lock.unlock();
		}
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
			lock.lock();
			t.lock.lock();
			return position.equals(t.position) && rotation.equals(t.rotation) && scale.equals(t.scale);
		} finally {
			lock.unlock();
			t.lock.unlock();
		}
	}

	/**
	 * Returns the 4x4 matrix that represents this transform object
	 * @return
	 */
	public Matrix toMatrix(){
		Matrix translate = MathHelper.translate(getPosition());
		Matrix rotate = MathHelper.rotate(getRotation());
		Matrix scale = MathHelper.scale(getScale());
		return scale.multiply(rotate).multiply(translate);
	}

	/**
	 * Returns a unit vector that points in the forward direction of this transform
	 * @return
	 */
	public Vector3 forwardVector() {
		return MathHelper.transform(Vector3.FORWARD, getRotation());
	}

	/**
	 * Returns a unit vector that points right in relation to this transform
	 */
	public Vector3 rightVector() {
		return MathHelper.transform(Vector3.RIGHT, getRotation());
	}

	/**
	 * Returns a unit vector that points up in relation to this transform
	 * @return
	 */
	public Vector3 upVector() {
		return MathHelper.transform(Vector3.UP, getRotation());
	}
}
