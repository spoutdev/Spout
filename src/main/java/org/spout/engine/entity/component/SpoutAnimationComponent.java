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
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.mesh.BaseMesh;

public class SpoutAnimationComponent extends AnimationComponent {

	private final static Matrix identity = MathHelper.createIdentity();

	//Keep a matrices array at the size of managed skeleton
	private Matrix[] matrices = null;
	
	class AnimationChannel{
		public Animation animation = null;
		public float speed = 1f;
		public boolean loop = false;
		private Matrix[] matrices = null;
		
		public int currentFrame = 0;
		public float currentTime = 0;
	}
	
	public List<AnimationChannel> animations = new ArrayList<AnimationChannel>();
	
	public void playAnimation(Animation animation){
		playAnimation(animation,false);
	}

	public void playAnimation(Animation animation, boolean loop){
		//TODO : Maybe check if the animation is compatible with the skeletin of this model
		//TODO : Maybe make real sync to avoid error with render

		AnimationChannel ac = new AnimationChannel();
		
		ac.currentFrame = 0;
		ac.currentTime = 0;
		ac.currentFrame = 0;
		ac.loop = loop;
		ac.animation = animation; // Finish by set of the animation to avoid NPE

		//Allocate matrices
		ac.matrices = new Matrix[getOwner().get(ModelComponent.class).getModel().getSkeleton().getBoneSize()];
		
		animations.add(ac);
	}
	
	public void stopAnimation(int i){
		animations.remove(i);
	}
	
	public void stopAnimation(){
		animations.clear();
	}

	public void batchSkeleton(BaseMesh mesh) {
		ModelComponent model = getOwner().get(ModelComponent.class);
		Skeleton skeleton = model.getModel().getSkeleton();
		if (skeleton != null) {

			//Register matrices identity to fill when no animation
			matrices = new Matrix[skeleton.getBoneSize()];
			for (int i = 0; i < matrices.length; i++) {
				matrices[i] = identity;
			}

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
	}

	public void updateAnimation(float dt){
		if (animations.isEmpty())
			return;

		for(int i = 0; i < animations.size(); i++){
			AnimationChannel ac = animations.get(i);

			ac.currentTime += dt * ac.speed;

			ac.currentFrame = (int) (ac.currentTime / ac.animation.getDelay());

			if(ac.currentFrame >= ac.animation.getFrame()){ //Loop
				if(!ac.loop){

					//TODO : Send a AnimationEndEvent is the loop is enabled ?

					animations.remove(i);
					i--;
					
					if (AnimationEndEvent.getHandlerList().getRegisteredListeners().length != 0) {
						Spout.getEventManager().callEvent(new AnimationEndEvent(getOwner(),ac.animation));
					}
				}
				ac.currentTime = 0;
				ac.currentFrame = 0;
			}
		}
	}
	
	public final static int ALLOWED_ANIMATION = 2; //Depend of the shader
	
	public void render(){
		ModelComponent model = getOwner().get(ModelComponent.class);
		
		if( model.getModel().getSkeleton() == null)
			throw new IllegalStateException("AnimationComponent require a entity with a skeleton");

		RenderMaterial mat = model.getModel().getRenderMaterial();

		int count = 0;
		for(AnimationChannel ac : animations){
			if(count >= ALLOWED_ANIMATION)
				break;

			for (int i = 0; i < ac.matrices.length; i++) {
				ac.matrices[i] = new Matrix(4, ac.animation.getBoneTransform(i, ac.currentFrame).getMatrix());
			}
			
			//System.out.println(count + " : play animation");

			mat.getShader().setUniform("bone_matrix" + (count + 1), ac.matrices);

			count++;
		}

		while(count < ALLOWED_ANIMATION){
			//System.out.println(count + " : use identity");
			mat.getShader().setUniform("bone_matrix" + (count + 1), matrices);
			count++;
		}

		//TODO : Replace "bone_matrix" by something configurable ?
	}
}