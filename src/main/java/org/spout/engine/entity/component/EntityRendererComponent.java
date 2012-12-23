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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import org.spout.api.component.impl.ModelComponent;
import org.spout.api.component.type.EntityComponent;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotEntity;

import org.spout.engine.mesh.BaseMesh;

public class EntityRendererComponent extends EntityComponent {
	
	public Animation animation = null;
	public int currentFrame = 0;
	public float currentTime = 0;
	
	public float rot = 0f;

	@Override
	public void onAttached() {
	}

	private void batch(){
		ModelComponent model = getOwner().get(ModelComponent.class);

		if (model == null) {
			return;
		}
		
		BaseMesh mesh = (BaseMesh) model.getModel().getMesh();

		if (mesh.isBatched()) {
			return;
		}

		Skeleton skeleton = model.getModel().getSkeleton();

		//TODO : In progress !
		if (skeleton != null) {
			System.out.println("Buffering skeleton");
			FloatBuffer boneIdBuffer = BufferUtils.createFloatBuffer(mesh.getContainer().element * skeleton.getBonePerVertice());
			FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(mesh.getContainer().element * skeleton.getBonePerVertice());

			if (skeleton.getBonePerVertice() > skeleton.getBonePerVertice()) {
				System.out.println("Number of bone per vertice limited ! ");
			}

			boneIdBuffer.clear();
			weightBuffer.clear();

			//For each vertice
			for (int i = 0; i < mesh.getContainer().element; i++) {

				//Get the vertice id in the .obj/.ske referential
				int vertexId = mesh.getContainer().getVerticeIndex()[i] - 1;

				if (vertexId >= skeleton.getVerticeArray().size()) {
					System.out.println("Depassement");
					continue;
				}

				int j = 0;
				//For each registred bone associated with this vertice, add it in buffer
				for (; j < skeleton.getVerticeArray().get(vertexId).size(); j++) {
					boneIdBuffer.put(skeleton.getVerticeArray().get(vertexId).get(j));
					weightBuffer.put(skeleton.getWeightArray().get(vertexId).get(j));
				}
				//Full the buffer for the number of vertice
				for (; j < skeleton.getBonePerVertice(); j++) {
					boneIdBuffer.put(-1);
					weightBuffer.put(-1);
				}
				//System.out.println("Taille : "+j );
			}

			boneIdBuffer.flip();
			weightBuffer.flip();

			mesh.getContainer().setBuffers(4, weightBuffer);
			mesh.getContainer().setBuffers(5, boneIdBuffer);
			System.out.println("Buffering skeleton SUCCESS");
		}

		mesh.batch();
	}
	
	private void updateAnimation(float dt){
		if(animation == null){
			ModelComponent model = getOwner().get(ModelComponent.class);

			if (model == null) {
				return;
			}
			
			animation = model.getModel().getAnimations().get("animatest");
			currentTime = 0;
			currentFrame = 0;
			return;
		}
		
		currentTime += dt;
		
		currentFrame = (int) (currentTime / animation.getDelay());
		
		if(currentFrame >= animation.getFrame()){ //Loop
			currentTime = 0;
			currentFrame = 0;
		}
		
		//TODO : Send a animation finish event.
		//TODO : Loop on animation if wanted
	}
	
	public void update(float dt) {
		
		batch(); //TODO : Call the batch method one time when the render start
		
		updateAnimation(dt);
	}

	public void render(Camera camera) {
		ModelComponent model = getOwner().get(ModelComponent.class);

		if (model == null) {
			return;
		}
		
		BaseMesh mesh = (BaseMesh) model.getModel().getMesh();

		if (mesh == null) {
			return;
		}

		Matrix modelMatrix = getOwner().getTransform().getTransformation();
		RenderMaterial mat = model.getModel().getRenderMaterial();

		mat.getShader().setUniform("View", camera.getView());
		mat.getShader().setUniform("Projection", camera.getProjection());
		mat.getShader().setUniform("Model", modelMatrix);
		Skeleton skeleton = model.getModel().getSkeleton();
		if (skeleton != null) {
			Matrix[] matrices = new Matrix[model.getModel().getSkeleton().getBoneSize()];
		
			if(animation != null){
				for (int i = 0; i < matrices.length; i++) {
					matrices[i] = new Matrix(4, animation.getBoneTransform(i, currentFrame).getMatrix());
				}
			}else{
				for (int i = 0; i < matrices.length; i++) {
					matrices[i] = MathHelper.rotateX(rot);
				}
				rot += 0.1f;
			}

			mat.getShader().setUniform("bone_matrix", matrices);
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
