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

import org.spout.math.imaginary.Complex;
import org.spout.math.matrix.Matrix3;
import org.spout.math.vector.Vector2;

public class Transform2D {
	private Vector2 position;
	private Complex rotation;
	private Vector2 scale;

	public Transform2D() {
		this(Vector2.ZERO, Complex.IDENTITY, Vector2.ONE);
	}

	public Transform2D(Vector2 position, Complex rotation, Vector2 scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public void add(float x, float y) {
		setPosition(getPosition().add(x, y));
	}

	public void setPosition(float x, float y) {
		this.position = new Vector2(x, y);
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setScale(float scale) {
		this.scale = new Vector2(scale, scale);
	}

	public void setScale(float scaleX, float scaleY) {
		this.scale = new Vector2(scaleX, scaleY);
	}

	public void setScale(Vector2 scale) {
		this.scale = scale;
	}

	public Vector2 getScale() {
		return scale;
	}

	public void setRotation(float angle) {
		this.rotation = new Complex(angle);
	}

	public void setRotation(Complex rotation) {
		this.rotation = rotation;
	}

	public Complex getRotation() {
		return rotation;
	}

	public Matrix3 toMatrix() {
		Matrix3 rotation = this.rotation.toMatrix();
		Matrix3 translation = Matrix3.createTranslation(position);
		Matrix3 scale = Matrix3.createScaling(this.scale.toVector3());
		return scale.mul(rotation).mul(translation);
	}
}
