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
import org.spout.api.component.impl.ModelComponent;
import org.spout.api.entity.Entity;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.entity.EntityDespawnEvent;
import org.spout.api.model.Model;
import org.spout.api.render.Camera;
import org.spout.engine.SpoutClient;
import org.spout.engine.entity.component.ClientTextModelComponent;
import org.spout.engine.entity.component.EntityRendererComponent;
import org.spout.engine.mesh.BaseMesh;

public class EntityRenderer implements Listener{
	
	//TODO send entity here to render it (enter/spawn chunk in viewdistance)
	
	private Map<Model, List<Entity>> entities = new HashMap<Model, List<Entity>>();
	
	public void addEntity(Entity entity){
		EntityRendererComponent render = entity.get(EntityRendererComponent.class);
		
		if(render == null){
			return;
		}
		
		ModelComponent modelComponent = entity.get(ModelComponent.class);

		if (modelComponent == null || modelComponent.getModel() == null) {
			return;
		}
		
		Model model = modelComponent.getModel();
		
		List<Entity> list = entities.get(model);
		
		if(list == null){
			list = new ArrayList<Entity>();
			entities.put(model, list);
		}
		
		list.add(entity);
		render.init();
		entity.setRendered(true);
	}
	
	public void removeEntity(Entity entity){
		ModelComponent modelComponent = entity.get(ModelComponent.class);

		if (modelComponent == null || modelComponent.getModel() == null) {
			return;
		}

		Model model = modelComponent.getModel();
		
		List<Entity> list = entities.get(model);
		
		list.remove(entity);
		entity.setRendered(false);
		
		if(list.isEmpty()){
			entities.remove(model);
		}
	}
	
	@EventHandler(order = Order.MONITOR)
	public void onEntityDespawnEvent(EntityDespawnEvent event){
		if(event.getEntity().isRendered()){
			removeEntity(event.getEntity());
		}
	}
	
	public void render(float dt){
		for (Entity e : ((SpoutClient)Spout.getEngine()).getActiveWorld().getAll()) {
			if(!e.isRendered())
				addEntity(e);
			
			/*EntityRendererComponent r = e.get(EntityRendererComponent.class);
			if (r != null) {
				r.update(dt);
				r.render();
			}*/
		}
		Camera camera = ((Client)Spout.getEngine()).getActiveCamera();
		
		for(Entry<Model, List<Entity>> entry : entities.entrySet()){
			Model model = entry.getKey();
			List<Entity> list = entry.getValue();
			BaseMesh mesh = (BaseMesh) model.getMesh();
			
			EntityRendererComponent first = list.get(0).get(EntityRendererComponent.class);
			
			if(!mesh.isBatched()){
				//TODO Put mesh in model not in BaseMesh, because BaseMesh can be shared between model
				// and different can use different skeleton, so that can produce a conflict in the mesh
				first.init();
			}			
			
			//Render model
			mesh.preDraw();
			
			first.getModel().getRenderMaterial().getShader().setUniform("View", camera.getView());
			first.getModel().getRenderMaterial().getShader().setUniform("Projection", camera.getProjection());
			
			for(Entity e : list){
				EntityRendererComponent r = e.get(EntityRendererComponent.class);
				r.update(dt);
				r.draw();
			}
			
			mesh.postDraw();
			
			//Render text component
			for(Entity e : list){
				ClientTextModelComponent r = e.get(ClientTextModelComponent.class);
				if(r != null)
					r.render(camera);
			}
		}
		

		//TODO Remove this when we use SpoutClientWorld
		//SpoutSnapshotLock lock = (SpoutSnapshotLock) ((Client)Spout.getEngine()).getScheduler().getSnapshotLock();
		//lock.coreReadLock("Render Thread - Render Entities");
		/*for (Entity e : ((SpoutClient)Spout.getEngine()).getActiveWorld().getAll()) {
			EntityRendererComponent r = e.get(EntityRendererComponent.class);
			if (r != null) {
				r.update(dt);
				r.render();
			}
		}*/
		//lock.coreReadUnlock("Render Thread - Render Entities");
	}
	
	
}
