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

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.component.impl.ModelComponent;
import org.spout.api.entity.Entity;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.SpoutClient;
import org.spout.engine.entity.component.EntityRendererComponent;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;

public class EntityRenderer {
	
	//TODO send entity here to render it (enter/spawn chunk in viewdistance)
	
	private Map<RenderMaterial, List<Entity>> entities = new HashMap<RenderMaterial, List<Entity>>();
	
	public void addEntity(Entity entity){
		EntityRendererComponent render = entity.get(EntityRendererComponent.class);
		
		if(render == null){
			return;
		}
		
		ModelComponent model = entity.get(ModelComponent.class);

		if (model == null || model.getModel() == null) {
			return;
		}
		
		RenderMaterial mat = model.getModel().getRenderMaterial();
		
		List<Entity> list = entities.get(mat);
		
		if(list == null){
			list = new ArrayList<Entity>();
			entities.put(mat, list);
		}
		
		if(!list.contains(entity))
			list.add(entity);
	}
	
	public void removeEntity(Entity entity){
		ModelComponent model = entity.get(ModelComponent.class);

		if (model == null || model.getModel() == null) {
			return;
		}
		
		RenderMaterial mat = model.getModel().getRenderMaterial();
		
		List<Entity> list = entities.get(mat);
		
		list.remove(entity);
		
		if(list.isEmpty()){
			entities.remove(mat);
		}
	}
	
	public void render(float dt){
		/*if(((Client)Spout.getEngine()).getActivePlayer().getWorld() == null)
			return;
		
		for (List<Entity> list : entities.values()) {
			for (Entity e : list) {
				EntityRendererComponent r = e.get(EntityRendererComponent.class);
				if (r != null) {
					r.update(dt);
					r.render();
				}
			}
		}*/

		//TODO Remove this when we use SpoutClientWorld
		//SpoutSnapshotLock lock = (SpoutSnapshotLock) ((Client)Spout.getEngine()).getScheduler().getSnapshotLock();
		//lock.coreReadLock("Render Thread - Render Entities");
		for (Entity e : ((SpoutClient)Spout.getEngine()).getActiveWorld().getAll()) {
			EntityRendererComponent r = e.get(EntityRendererComponent.class);
			if (r != null) {
				r.update(dt);
				r.render();
			}
		}
		//lock.coreReadUnlock("Render Thread - Render Entities");
	}
	
}
