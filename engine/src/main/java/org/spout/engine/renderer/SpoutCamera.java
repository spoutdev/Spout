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

package org.spout.engine.renderer;

import org.spout.api.render.Camera;
import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector3;

public class SpoutCamera extends org.spout.renderer.Camera implements Camera {

	public SpoutCamera(Matrix4 projection) {
		super(projection);
	}
	@Override
	public Vector3 getForward() {
		return super.getForward();	}

	@Override
	public Vector3 getPosition() {
		return super.getPosition();
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		return super.getProjectionMatrix();
	}

	@Override
	public Vector3 getRight() {
		return super.getRight();
	}

	@Override
	public Quaternion getRotation() {
		return super.getRotation();
	}

	@Override
	public Vector3 getUp() {
		return super.getUp();
	}

	@Override
	public Matrix4 getViewMatrix() {
		return super.getViewMatrix();
	}

	@Override
	public void setPosition(Vector3 pos) {
		super.setPosition(pos);
	}

	@Override
	public void setRotation(Quaternion rot) {
		super.setRotation(rot);
	}
}
