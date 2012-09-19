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

import org.spout.api.component.components.EntityComponent;
import org.spout.api.component.components.ModelComponent;
import org.spout.api.component.components.TransformComponent;
import org.spout.api.math.Matrix;
import org.spout.api.model.Mesh;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.batcher.PrimitiveBatch;
import org.spout.engine.mesh.BaseMesh;

public class EntityRendererComponent extends EntityComponent {
	
	ModelComponent model;
	TransformComponent transform;
	
	PrimitiveBatch batch;
	
	boolean dirty = true;
	
	@Override
	public void onAttached(){
		model = getHolder().get(ModelComponent.class);
		transform = getHolder().getTransform();
		batch = new PrimitiveBatch();
	}
	
	
	public void render() {
		if(model == null) return;
		BaseMesh m = (BaseMesh)model.getModel().getMesh();
		
		if(dirty) {
			m.batch();
			dirty = false;
		}
		Matrix modelMatrix = transform.getTransformation();
		RenderMaterial mat = model.getModel().getRenderMaterial();
		
		mat.getShader().setUniform("Model", modelMatrix);		
		
		m.render(mat);
		
	}
	
}
