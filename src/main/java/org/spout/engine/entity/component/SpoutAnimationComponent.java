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
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.spout.api.Spout;
import org.spout.api.component.impl.AnimationComponent;
import org.spout.api.component.impl.ModelComponent;
import org.spout.api.event.entity.AnimationEndEvent;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.AnimationPlayed;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.mesh.BaseMesh;

public class SpoutAnimationComponent extends AnimationComponent {

	private final static Matrix identity = MatrixMath.createIdentity();

	//Depend of the shader
	public final static int ALLOWED_BONE_PER_VERTEX = 2;
	public final static int ALLOWED_ANIMATION_PER_MESH = 2;
	public final static int ALLOWED_BONE_PER_MESH = 10;
	
	public final static int LAYOUT_WEIGHT = 4;
	public final static int LAYOUT_ID = 5;

	private List<AnimationPlayed> animations = new ArrayList<AnimationPlayed>();

	//Keep a matrices array at the size of managed skeleton
	private RenderMaterial renderMaterial;
	private int bonesInMesh = 0;
	private Matrix[] matrices = null;

	@Override
	public AnimationPlayed playAnimation(Animation animation){
		return playAnimation(animation,false);
	}

	@Override
	public AnimationPlayed playAnimation(Animation animation, boolean loop){
		//TODO : Maybe check if the animation is compatible with the skeletin of this model
		//TODO : Maybe make real sync to avoid error with render

		AnimationPlayed ac = new AnimationPlayed(animation, loop);

		//Allocate matrices
		ac.setMatrices(new Matrix[ALLOWED_BONE_PER_MESH]);

		animations.add(ac);

		return ac;
	}

	@Override
	public void stopAnimation(AnimationPlayed animation){
		animations.remove(animation);
	}

	@Override
	public void stopAnimations() {
		animations.clear();
	}

	public void batchSkeleton(BaseMesh mesh) {
		ModelComponent model = getOwner().get(ModelComponent.class);

		if( model.getModel().getSkeleton() == null)
			throw new IllegalStateException("AnimationComponent require a entity with a skeleton");

		Skeleton skeleton = model.getModel().getSkeleton();

		if (skeleton != null) {

			renderMaterial = model.getModel().getRenderMaterial();

			//Register matrices identity to fill when no animation
			matrices = new Matrix[ALLOWED_BONE_PER_MESH];
			for (int i = 0; i < matrices.length; i++) {
				matrices[i] = identity;
			}

			bonesInMesh = skeleton.getBoneSize();

			if(mesh.getContainer().getBuffers().containsKey(LAYOUT_ID))
				return;

			System.out.println("Buffering skeleton");
			FloatBuffer boneIdBuffer = BufferUtils.createFloatBuffer(mesh.getContainer().element * ALLOWED_BONE_PER_VERTEX);
			FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(mesh.getContainer().element * ALLOWED_BONE_PER_VERTEX);

			boneIdBuffer.clear();
			weightBuffer.clear();

			//For each vertice
			for (int i = 0; i < mesh.getContainer().element; i++) {

				//Get the vertice id in the .obj/.ske referential
				int vertexId = mesh.getContainer().getVerticeIndex()[i] - 1;

				if (vertexId >= skeleton.getVerticeArray().size()) {
					throw new IllegalStateException("Mesh don't match skeleton");
				}

				int j = 0;
				//For each registred bone associated with this vertice, add it in buffer
				for (; j < skeleton.getVerticeArray().get(vertexId).size(); j++) {
					int bone_id = skeleton.getVerticeArray().get(vertexId).get(j);
					boneIdBuffer.put(bone_id);
					float weight = skeleton.getWeightArray().get(vertexId).get(j);
					weightBuffer.put(weight);
				}
				//Full the buffer for the number of vertice
				for (; j < ALLOWED_BONE_PER_VERTEX; j++) {
					boneIdBuffer.put(0);
					weightBuffer.put(0);
				}
			}

			boneIdBuffer.flip();
			weightBuffer.flip();
			System.out.println(mesh.getContainer().element + " -> " + boneIdBuffer.limit());

			mesh.getContainer().setBuffers(LAYOUT_WEIGHT, weightBuffer);
			mesh.getContainer().setBuffers(LAYOUT_ID, boneIdBuffer);
			render();//Render one time to send the required uniform
			System.out.println("Buffering skeleton SUCCESS");
		}
	}

	public void updateAnimation(float dt){
		if (animations.isEmpty())
			return;

		for(int i = 0; i < animations.size(); i++){
			AnimationPlayed ac = animations.get(i);

			ac.setCurrentTime(ac.getCurrentTime() + dt * ac.getSpeed());

			ac.setCurrentFrame((int) (ac.getCurrentTime() / ac.getAnimation().getDelay()));

			if(ac.getCurrentFrame() >= ac.getAnimation().getFrame()){ //Loop
				if(!ac.isLoop()){

					//TODO : Send a AnimationEndEvent is the loop is enabled ?

					animations.remove(i);
					i--;

					if (AnimationEndEvent.getHandlerList().getRegisteredListeners().length != 0) {
						Spout.getEventManager().callEvent(new AnimationEndEvent(getOwner(),ac.getAnimation()));
					}
				}
				ac.setCurrentTime(0);
				ac.setCurrentFrame(0);
			}
		}
	}

	public void render(){
		int count = 0;
		for(AnimationPlayed ac : animations){
			int i;

			for (i = 0; i < bonesInMesh; i++) {
				ac.getMatrices()[i] = ac.getAnimation().getBoneTransform(i, ac.getCurrentFrame()).getMatrix();
			}

			for (; i < ALLOWED_BONE_PER_MESH; i++) {
				ac.getMatrices()[i] = identity.transpose();
			}

			renderMaterial.getShader().setUniform("bone_matrix" + (count + 1), ac.getMatrices());

			count++;
			if(count >= ALLOWED_ANIMATION_PER_MESH)
				break;
		}

		while(count < ALLOWED_ANIMATION_PER_MESH){
			renderMaterial.getShader().setUniform("bone_matrix" + (count + 1), matrices);
			count++;
		}
	}
}