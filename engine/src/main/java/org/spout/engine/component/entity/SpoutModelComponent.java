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
package org.spout.engine.component.entity;

import org.spout.api.component.entity.ModelComponent;
import org.spout.api.model.Model;
import org.spout.api.render.effect.SnapshotEntity;

import org.spout.engine.mesh.BaseMesh;
import org.spout.math.matrix.Matrix4;

public class SpoutModelComponent extends ModelComponent {
	private SpoutAnimationComponent animationComponent;
	private boolean rendered = false;

	public void init() {
		animationComponent = getOwner().get(SpoutAnimationComponent.class);

		for (Model model : getModels()) {
			BaseMesh mesh = (BaseMesh) model.getMesh();

			if (animationComponent != null) {
				animationComponent.batchSkeleton();
			}

			if (!mesh.isBatched()) {
				mesh.batch();
			}
		}
	}

	public void update(Model model, float dt) {
		if (animationComponent != null) {
			animationComponent.updateAnimation(model, dt);
		}
	}

	public void draw(Model model) {
		Matrix4 modelMatrix = getOwner().getPhysics().getTransformRender().toMatrix();

		model.getRenderMaterial().getShader().setUniform("Model", modelMatrix);

		if (animationComponent != null) {
			animationComponent.render(model);
		}

		SnapshotEntity snapshot = new SnapshotEntity(model.getRenderMaterial(), getOwner());

		model.getRenderMaterial().preRenderEntity(snapshot);
		((BaseMesh) model.getMesh()).render(model.getRenderMaterial());
		model.getRenderMaterial().postRenderEntity(snapshot);
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
}
