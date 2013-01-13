/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.entity.component;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.component.impl.ModelComponent;
import org.spout.api.component.impl.PredictableTransformComponent;
import org.spout.api.component.type.EntityComponent;
import org.spout.api.math.Matrix;
import org.spout.api.model.mesh.Mesh;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotEntity;
import org.spout.engine.mesh.BaseMesh;

public class EntityRendererComponent extends EntityComponent {

	private SpoutAnimationComponent animation;
	
	private void batch(){
		ModelComponent model = getOwner().get(ModelComponent.class);

		if (model == null || model.getModel() == null) {
			return;
		}
		Mesh meshRaw = model.getModel().getMesh();
		if (meshRaw == null) {
			return;
		}
		BaseMesh mesh = (BaseMesh) meshRaw;

		if (mesh.isBatched()) {
			return;
		}

		animation = getOwner().get(SpoutAnimationComponent.class);

		if(animation != null){
			animation.batchSkeleton(mesh);
		}

		mesh.batch();
	}

	public void update(float dt) {
		batch(); //TODO : Call the batch method one time when the render start

		if(animation != null)
			animation.updateAnimation(dt);
	}

	public void render() {
		Camera camera = ((Client)Spout.getEngine()).getActiveCamera();
		ModelComponent model = getOwner().get(ModelComponent.class);

		if (model == null || model.getModel() == null) {
			return;
		}

		BaseMesh mesh = (BaseMesh) model.getModel().getMesh();

		if (mesh == null) {
			return;
		}

		Matrix modelMatrix = ((PredictableTransformComponent) getOwner().getTransform()).getRenderTransform().toMatrix();
		RenderMaterial mat = model.getModel().getRenderMaterial();

		mat.getShader().setUniform("View", camera.getView());
		mat.getShader().setUniform("Projection", camera.getProjection());
		mat.getShader().setUniform("Model", modelMatrix);

		if(animation != null){
			animation.render();
		}

		SnapshotEntity snap = new SnapshotEntity(mat, getOwner());

		mat.preRenderEntity(snap);
		mesh.render(mat);
		mat.postRenderEntity(snap);

		ClientTextModelComponent tmc = getOwner().get(ClientTextModelComponent.class);
		if (tmc != null) {
			tmc.render(camera);
		}
	}
}
