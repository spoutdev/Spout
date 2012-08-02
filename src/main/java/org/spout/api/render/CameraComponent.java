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
package org.spout.api.render;

import org.spout.api.entity.component.BasicEntityComponent;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;

public class CameraComponent extends BasicEntityComponent implements Camera {

	Matrix projection;
	Matrix view;
	private ViewFrustum frustum = new ViewFrustum();

	@Override
	public Matrix getProjection() {
		return projection;
	}

	@Override
	public Matrix getView() {
		return view;
	}

	@Override
	public void updateView() {
		view = MathHelper.rotate(getParent().getRotation()).multiply(MathHelper.translate(getParent().getPosition()));

	}

	@Override
	public void onTick(float dt) {
		updateView();

	}

	@Override
	public void onAttached() {
		// TODO Get FOV
		projection = MathHelper.createPerspective(90f, 4.0f / 3.0f, .001f, 1000f);
		updateView();
		frustum.update(projection, view);
	}

	@Override
	public ViewFrustum getFrustum() {
		return frustum;
	}

}
