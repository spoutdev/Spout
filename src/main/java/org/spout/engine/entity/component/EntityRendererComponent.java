/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.entity.component;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;
import org.spout.api.component.components.EntityComponent;
import org.spout.api.component.components.ModelComponent;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.model.animation.Bone;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotEntity;
import org.spout.engine.mesh.BaseMesh;

public class EntityRendererComponent extends EntityComponent {

	@Override
	public void onAttached() {
	}

	public void update(){
		//Generate renderer
		ModelComponent model = getOwner().get(ModelComponent.class);

		if(model == null)
			return;

		BaseMesh mesh = (BaseMesh) model.getModel().getMesh();

		if(mesh.isBatched())
			return;

		Skeleton skeleton = null;//model.getModel().getSkeleton();

		//TODO : In progress !
		if(skeleton != null){
			System.out.println("Buffering skeleton");
			FloatBuffer boneIdBuffer = BufferUtils.createFloatBuffer(mesh.getContainer().element * skeleton.getBonePerVertice());
			FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(mesh.getContainer().element * skeleton.getBonePerVertice());

			if(skeleton.getBonePerVertice() > skeleton.getBonePerVertice())
				System.out.println("Number of bone per vertice limited ! ");
			
			boneIdBuffer.clear();
			weightBuffer.clear();

			//For each vertice
			for(int i = 0; i < mesh.getContainer().element; i++ ){

				//Get the vertice id in the .obj/.ske referential
				int vertexId =  mesh.getContainer().getVerticeIndex()[i];

				if(vertexId >= skeleton.getVerticeArray().size()){
					System.out.println("Depassement");
					continue;
				}
				
				int j = 0;
				//For each registred bone associated with this vertice, add it in buffer
				for(; j < skeleton.getVerticeArray().get(vertexId).size(); j++){
					boneIdBuffer.put(skeleton.getVerticeArray().get(vertexId).get(j));
					weightBuffer.put(skeleton.getWeightArray().get(vertexId).get(j));
				}
				//Full the buffer for the number of vertice
				for(; j < skeleton.getBonePerVertice(); j++){
					boneIdBuffer.put(-1);
					weightBuffer.put(-1);
				}
				System.out.println("Taille : "+j );
			}
			
			boneIdBuffer.flip();
			weightBuffer.flip();

			mesh.getContainer().setBuffers(5, boneIdBuffer);
			mesh.getContainer().setBuffers(6, weightBuffer);
			System.out.println("Buffering skeleton SUCCESS");
		}

		mesh.batch();
	}

	public void render(Camera camera) {
		ModelComponent model = getOwner().get(ModelComponent.class);

		if (model == null)
			return;

		BaseMesh mesh = (BaseMesh) model.getModel().getMesh();

		if (mesh == null) 
			return;

		Matrix modelMatrix = getOwner().getTransform().getTransformation();
		RenderMaterial mat = model.getModel().getRenderMaterial();

		mat.getShader().setUniform("View", camera.getView());
		mat.getShader().setUniform("Projection", camera.getProjection());
		mat.getShader().setUniform("Model", modelMatrix);
		
		mat.getShader().setUniform("bone_matrix", MathHelper.createIdentity());
		
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
