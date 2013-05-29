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
import org.spout.api.entity.Entity;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.entity.EntityDespawnEvent;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.geo.World;
import org.spout.api.model.Model;
import org.spout.api.render.Camera;

import org.spout.engine.SpoutClient;
import org.spout.engine.entity.component.ClientTextModelComponent;
import org.spout.engine.entity.component.EntityRendererComponent;
import org.spout.engine.entity.component.SpoutSceneComponent;
import org.spout.engine.mesh.BaseMesh;
import org.spout.engine.resources.ClientRenderMaterial;
import org.spout.engine.resources.ClientTexture;

/**
 * The Renderer of all EntityRendererComponents
 *
 * This class has several objectives...
 * -- Keep a cache of all EntityRendererComponents who share a model. This cuts down on all rendering.
 */
public class EntityRenderer {
	private final List<EntityRendererComponent> RENDERERS = new ArrayList<EntityRendererComponent>();
	private final Map<Model, List<EntityRendererComponent>> RENDERERS_PER_MODEL = new HashMap<Model, List<EntityRendererComponent>>();
	private int count = 0;

	public void add(EntityRendererComponent renderer) {
		RENDERERS.add(renderer);
		count++;
	}

	public void remove(EntityRendererComponent renderer) {
		RENDERERS.remove(renderer);
		//Cleanup models
		//TODO Keep the model (not the renderer) cached always?
		for (final Model model : renderer.getModels()) {
			final List<EntityRendererComponent> modelRenderers = RENDERERS_PER_MODEL.get(model);
			modelRenderers.remove(renderer);
			if (modelRenderers.isEmpty()) {
				RENDERERS_PER_MODEL.remove(model);
			}
		}
		renderer.setRendered(false);
		count--;
	}

	public void render(float dt) {
		final Camera camera = ((Client) Spout.getEngine()).getPlayer().getType(Camera.class);

		//Loop through all renderers and add models as needed.
		for (final EntityRendererComponent renderer : RENDERERS) {
			final List<Model> models = renderer.getModels();
			if (models.isEmpty()) {
				continue;
			}
			for (final Model model : models) {
				List<EntityRendererComponent> modelRenderers = RENDERERS_PER_MODEL.get(model);
				if (modelRenderers == null) {
					modelRenderers = new ArrayList<EntityRendererComponent>();
					RENDERERS_PER_MODEL.put(model, modelRenderers);
				}
				if (((ClientTexture) model.getRenderMaterial().getValue("Diffuse")).isLoaded()) {
					modelRenderers.add(renderer);
					renderer.init();
				}
			}
		}

		//Call renderers based on models
		for (Entry<Model, List<EntityRendererComponent>> entry : RENDERERS_PER_MODEL.entrySet()) {
			final Model model = entry.getKey();
			final List<EntityRendererComponent> renderers = entry.getValue();
			final BaseMesh mesh = (BaseMesh) model.getMesh();
			//Prep mesh for rendering
			mesh.preDraw();

			//Set uniforms
			model.getRenderMaterial().getShader().setUniform("View", camera.getView());
			model.getRenderMaterial().getShader().setUniform("Projection", camera.getProjection());

			//Render
			for (EntityRendererComponent renderer : renderers) {
				renderer.update(model, dt);
				renderer.draw(model);
			}

			//Callback after rendering
			mesh.postDraw();
		}
	}

	public int getRenderedEntities() {
		return count;
	}
}
