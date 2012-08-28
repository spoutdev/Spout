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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.geo.World;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.StringUtil;
import org.spout.api.util.thread.Threadsafe;

public class Transform implements Serializable{
	private static final long serialVersionUID = 1L;

	private Point position = Point.invalid;
	private Quaternion rotation = Quaternion.IDENTITY;
	private Vector3 scale = Vector3.ONE;

	private Transform parent;

	public Transform() {
	}

	public Transform(Point position, Quaternion rotation, Vector3 scale) {
		setPosition(position);
		setRotation(rotation);
		setScale(scale);
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

	/**
	 * Atomically gets the parent of this Transform
	 *
	 * @return the parent
	 */
	@Threadsafe
	public Transform getParent() {
		return parent;
	}

	/**
	 * Atomically Sets the parent of this transform
	 *
	 * @param parent
	 */
	@Threadsafe
	public void setParent(Transform parent) {
		this.parent = parent;
	}

	/**
	 * Atomically sets the value of this transform to the value of another
	 * transform
	 *
	 * @param transform the other transform
	 * @return true if successful
	 */
	@Threadsafe
	public boolean set(Transform transform) {
		if (transform == null) {
			return false;
		}

		setPosition(transform.position);
		setRotation(transform.rotation);
		setScale(transform.scale);
		return true;
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
		this.position = new Point(world, px, py, pz);
		this.rotation = new Quaternion(rx, ry, rz, rw);
		this.scale = new Vector3(sx, sy, sz);
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
		setPosition(p);
		setRotation(r);
		setScale(s);
	}

	/**
	 * Creates a Transform that is the sum of this transform and the given
	 * transform
	 *
	 * @param t the transform
	 * @return the new transform
	 */
	@Threadsafe
	public Transform createSum(Transform t) {
		Transform r = new Transform();

		r.setPosition(position.add(t.getPosition()));
		r.setRotation(rotation.multiply(t.getRotation()));
		r.setScale(scale.add(t.getScale()));
		r.setParent(parent);
		return r;

	}

	/**
	 * Creates a Transform that is a snapshot of the absolute position of this
	 * transform
	 *
	 * @return the snapshot
	 */
	@Threadsafe
	public Transform getAbsolutePosition() {
		if (parent == null) {
			return copy();
		}

		return createSum(parent.getAbsolutePosition());
	}

	/**
	 * Creates a Transform that is a copy of this transform
	 *
	 * @return the snapshot
	 */
	@Threadsafe
	public Transform copy() {
		Transform t = new Transform();

		t.setPosition(position);
		t.setRotation(rotation);
		t.setScale(scale);
		t.setParent(parent);

		return t;
	}

	/**
	 * Gets a String representation of this transform
	 *
	 * @return the string
	 */
	@Override
	@Threadsafe
	public String toString() {
		return getClass().getSimpleName() + StringUtil.toString(position, rotation, scale);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(41, 63).append(position).append(rotation).append(scale).append(parent).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Transform)) {
			return false;
		}
		Transform t = (Transform) other;
		return position.equals(t.position) && rotation.equals(t.rotation) && scale.equals(t.scale) && ObjectUtils.equals(parent, t.parent);
	}

	/**
	 * Returns the 4x4 matrix that represents this transform object
	 * @return
	 */
	public Matrix toMatrix(){
		Matrix translate = MathHelper.translate(position);
		Matrix rotate = MathHelper.rotate(rotation);
		Matrix scale = MathHelper.scale(this.scale);
		
		return scale.multiply(rotate).multiply(translate);
	}
	/**
	 * Returns a unit vector that points in the forward direction of this transform
	 * @return
	 */
	public Vector3 forwardVector() {
		return MathHelper.transform(Vector3.FORWARD, rotation);
	}
	/**
	 * Returns a unit vector that points right in relation to this transform
	 */
	public Vector3 rightVector() {
		return MathHelper.transform(Vector3.RIGHT, rotation);
	}
	/**
	 * Returns a unit vector that points up in relation to this transform
	 * @return
	 */
	public Vector3 upVector() {
		return MathHelper.transform(Vector3.UP, rotation);
	}
}
