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
package org.spout.api.component.entity;

import org.spout.api.geo.discrete.Transform;
import org.spout.api.render.Camera;
import org.spout.api.render.ViewFrustum;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;

public class CameraComponent extends EntityComponent implements Camera {
	private Matrix4 projection;
	private Matrix4 view;
	private ViewFrustum frustum = new ViewFrustum();
	private float fieldOfView = 75f;

	public CameraComponent() {

	}

	public CameraComponent(Matrix4 createPerspective, Matrix4 createLookAt) {
		projection = createPerspective;
		view = createLookAt;
	}

	public void setScale(float scale) { //1/2
		projection = Matrix4.createPerspective(fieldOfView * scale, 4.0f / 3.0f, .001f * scale, 1000f * scale);
		updateView();
	}

	@Override
	public void onAttached() {
		// TODO Get FOV
		projection = Matrix4.createPerspective(fieldOfView, 4.0f / 3.0f, .001f, 1000f);
		updateView();
	}

	@Override
	public Matrix4 getProjection() {
		return projection;
	}

	@Override
	public Matrix4 getView() {
		return view;
	}

	@Override
	public void updateView() {
		Transform transform = getOwner().getPhysics().getTransformRender();
		Matrix4 pos = Matrix4.createTranslation(transform.getPosition().mul(-1));
		Matrix4 rot = Matrix4.createRotation(transform.getRotation());
		view = pos.mul(rot);
		frustum.update(projection, view, transform.getPosition());
	}

	@Override
	public void updateReflectedView() {
		Transform transform = getOwner().getPhysics().getTransformRender();
		Matrix4 pos = Matrix4.createTranslation(transform.getPosition().mul(-1, 1, -1));
		Matrix4 rot = Matrix4.createRotation(transform.getRotation());
		view = Matrix4.createScaling(1, -1, 1, 1).mul(pos).mul(rot);
		frustum.update(projection, view, transform.getPosition());
	}

	@Override
	public boolean canTick() {
		return false;
	}

	@Override
	public ViewFrustum getFrustum() {
		return frustum;
	}

	@Override
	public Matrix3 getRotation() {
		Transform transform = getOwner().getPhysics().getTransformRender();
		return Matrix3.createRotation(transform.getRotation());
	}
}
