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
package org.spout.engine.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.model.Model;
import org.spout.api.render.Camera;

import org.spout.engine.component.entity.SpoutModelComponent;
import org.spout.engine.filesystem.resource.ClientTexture;
import org.spout.engine.mesh.BaseMesh;

/**
 * The Renderer of all EntityRendererComponents
 *
 * This class has several objectives...
 * -- Keep a cache of all EntityRendererComponents who share a model. This cuts down on all rendering.
 */
public class EntityRenderer {
	private final List<SpoutModelComponent> RENDERERS = new ArrayList<SpoutModelComponent>();
	private int count = 0;

	public void add(SpoutModelComponent renderer) {
		RENDERERS.add(renderer);
		count++;
	}

	public void remove(SpoutModelComponent renderer) {
		RENDERERS.remove(renderer);
		renderer.setRendered(false);
		count--;
	}

	public void render(float dt) {
		final Camera camera = ((Client) Spout.getEngine()).getPlayer().getType(Camera.class);

		//Call renderers based on models
		for (SpoutModelComponent renderer : RENDERERS) {
			for(Model model : renderer.getModels()) {
				if (((ClientTexture) model.getRenderMaterial().getValue("Diffuse")).isLoaded()) {
					renderer.init();
}
				final BaseMesh mesh = (BaseMesh) model.getMesh();
				//Prep mesh for rendering
				mesh.preDraw();

				//Set uniforms
				model.getRenderMaterial().getShader().setUniform("View", camera.getView());
				model.getRenderMaterial().getShader().setUniform("Projection", camera.getProjection());

				renderer.update(model, dt);
				renderer.draw(model);
				
				//Callback after rendering
				mesh.postDraw();
			}
		}
	}

	public int getRenderedEntities() {
		return count;
	}
}
