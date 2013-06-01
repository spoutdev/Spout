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
package org.spout.engine.filesystem.resource;

import java.util.Collections;
import java.util.Map;

import org.spout.api.model.Model;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.model.mesh.Mesh;
import org.spout.api.render.RenderMaterial;

public class ClientModel implements Model {
	private static final long serialVersionUID = 1L;
	public Mesh mesh;
	public Skeleton skeleton;
	public RenderMaterial material;
	public Map<String, Animation> animations;

	@SuppressWarnings("unchecked")
	public ClientModel(Mesh mesh,RenderMaterial material) {
		this(mesh, null, material, Collections.EMPTY_MAP);
	}

	public ClientModel(Mesh mesh, Skeleton skeleton, RenderMaterial material, Map<String, Animation> animations) {
		this.mesh = mesh;
		this.skeleton = skeleton;
		this.material = material;
		this.animations = animations;
	}
	
	@Override
	public Mesh getMesh() {
		return mesh;
	}
	
	@Override
	public Skeleton getSkeleton() {
		return skeleton;
	}
	
	@Override
	public RenderMaterial getRenderMaterial() {
		return material;
	}

	@Override
	public Map<String, Animation> getAnimations() {
		return animations;
	}

}
