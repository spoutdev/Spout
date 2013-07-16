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
package org.spout.engine.component.entity;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

import org.spout.api.Spout;
import org.spout.api.component.entity.AnimationComponent;
import org.spout.api.event.entity.AnimationEndEvent;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.model.Model;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.AnimationPlayed;
import org.spout.api.model.animation.Skeleton;

import org.spout.engine.mesh.BaseMesh;

public class SpoutAnimationComponent extends AnimationComponent {

	/**
	 * Identity matrix used on bones when no animation to play
	 */
	private final static Matrix identity = MatrixMath.createIdentity();

	//Depend of the shader
	public final static int ALLOWED_BONE_PER_VERTEX = 2;
	public final static int ALLOWED_ANIMATION_PER_MESH = 2;
	public final static int ALLOWED_BONE_PER_MESH = 10;
	public final static int LAYOUT_WEIGHT = 4;
	public final static int LAYOUT_ID = 5;

	/**
	 * Entity can have more than one model, this Map allow to handle a list of animation for each model
	 */
	private Map<Model, List<AnimationPlayed>> animations = new HashMap<Model, List<AnimationPlayed>>();

	/**
	 * Matrices array at the size of managed skeleton used to fill shader when no animation to play
	 */
	private Matrix[] defaultMatrices = null;

	/**
	 * Ask to play a animation on a model of the entity
	 */
	@Override
	public AnimationPlayed playAnimation(Model model, Animation animation) {
		return playAnimation(model, animation, false);
	}

	/**
	 * Ask to play a animation on a model of the entity, allow to play a animation in loop
	 */
	@Override
	public AnimationPlayed playAnimation(Model model, Animation animation, boolean loop) {
		//TODO : Maybe check if the animation is compatible with the skeletin of this model
		//TODO : Maybe make real sync to avoid error with render

		AnimationPlayed ac = new AnimationPlayed(animation, loop);

		//Allocate matrices
		ac.setMatrices(new Matrix[ALLOWED_BONE_PER_MESH]);

		List<AnimationPlayed> list = animations.get(model);

		if (list == null) {
			list = new ArrayList<AnimationPlayed>();
			animations.put(model, list);
		}

		list.add(ac);

		return ac;
	}

	@Override
	public void stopAnimation(Model model, AnimationPlayed animation) {
		List<AnimationPlayed> list = animations.get(model);

		if(list == null)
			return;

		list.remove(animation);

		if(list.isEmpty())
			animations.remove(model);
	}

	@Override
	public void stopAnimations() {
		animations.clear();
	}

	//TODO move this in model
	public void batchSkeleton() {
		final SpoutModelComponent models = getOwner().get(SpoutModelComponent.class);

		for (Model model : models.getModels()) {

			if (model.getSkeleton() == null) {
				continue;
			}

			Skeleton skeleton = model.getSkeleton();

			if (skeleton != null) {

				BaseMesh mesh = (BaseMesh) model.getMesh();

				//Register matrices identity to fill when no animation
				defaultMatrices = new Matrix[ALLOWED_BONE_PER_MESH];
				for (int i = 0; i < defaultMatrices.length; i++) {
					defaultMatrices[i] = identity;
				}

				if (mesh.getContainer().getBuffers().containsKey(LAYOUT_ID)) {
					return;
				}

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
						throw new IllegalStateException("Mesh doesn't match skeleton");
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
				render(model);//Render one time to send the required uniform
				System.out.println("Buffering skeleton SUCCESS");
			}
		}
	}

	/**
	 * Update played animation whith the elapsed time
	 * @param model
	 * @param dt
	 */
	public void updateAnimation(Model model, float dt) {
		if (animations.isEmpty()) {
			return;
		}

		List<AnimationPlayed> finished = new ArrayList<>();

		for (AnimationPlayed ac : animations.get(model)) {

			// Increment time of the animation
			ac.setCurrentTime(ac.getCurrentTime() + dt * ac.getSpeed());

			// Recompute the current frame to play
			ac.setCurrentFrame((int) (ac.getCurrentTime() / ac.getAnimation().getDelay()));

			if (ac.getCurrentFrame() >= ac.getAnimation().getFrame()) { 
				if (ac.isLoop()) {	// Loop animation
					//Reset the animation

					//TODO Use a modulo to keep continuous animation nice

					ac.setCurrentTime(0);
					ac.setCurrentFrame(0);
				}else{
					finished.add(ac);

					//TODO : Send a AnimationEndEvent is the loop is enabled too ?
					if (AnimationEndEvent.getHandlerList().getRegisteredListeners().length != 0) {
						Spout.getEventManager().callEvent(new AnimationEndEvent(getOwner(), ac.getAnimation()));
					}
				}
			}
		}

		// Remove finished animations
		animations.get(model).removeAll(finished);
	}

	/**
	 * Set bone_matrix for a model for currents animations
	 * @param model
	 */
	public void render(Model model) {
		int animationCount = 0;

		List<AnimationPlayed> list = animations.get(model);

		if (list != null) {
			for (AnimationPlayed ac : list) {
				int boneCount;

				// Send matrices for existings bones
				for (boneCount = 0; boneCount < ac.getAnimation().getSize(); boneCount++) {
					ac.getMatrices()[boneCount] = ac.getAnimation().getBoneTransform(boneCount, ac.getCurrentFrame()).getMatrix();
				}

				// Send matrices to fill the shader
				for (; boneCount < ALLOWED_BONE_PER_MESH; boneCount++) {
					ac.getMatrices()[boneCount] = identity.transpose();
				}

				//Define bone_matrix? (start at 1 in shader)
				model.getRenderMaterial().getShader().setUniform("bone_matrix" + (animationCount + 1), ac.getMatrices());

				animationCount++;
				if (animationCount >= ALLOWED_ANIMATION_PER_MESH) {
					break;
				}
			}
		}

		// Fill animations slots with defaults matrices
		while (animationCount < ALLOWED_ANIMATION_PER_MESH) {
			//Define bone_matrix? (start at 1 in shader)
			model.getRenderMaterial().getShader().setUniform("bone_matrix" + (animationCount + 1), defaultMatrices);
			animationCount++;
		}
	}
}