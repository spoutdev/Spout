/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.entity;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.collision.model.CollisionModel;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.store.MemoryStore;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.metadata.EntityMetadataStore;
import org.spout.api.metadata.MetadataStringValue;
import org.spout.api.metadata.MetadataValue;
import org.spout.api.model.Model;
import org.spout.api.plugin.Plugin;
import org.spout.api.util.StringMap;
import org.spout.server.SpoutRegion;
import org.spout.server.SpoutServer;

public class SpoutEntity extends EntityMetadataStore implements Entity {
	public final static int NOTSPAWNEDID = -1;
	// TODO - needs to have a world based version too?
	public static final StringMap entityStringMap = new StringMap(null, new MemoryStore<Integer>(), 0, Short.MAX_VALUE);
	
	private TransformAndManager transformAndManager;
	private final AtomicReference<TransformAndManager> transformAndManagerLive = new AtomicReference<TransformAndManager>();
	private Controller controller;
	private final SpoutServer server;
	
	public int id = NOTSPAWNEDID;
	
	Model model;
	CollisionModel collision;
	
	public SpoutEntity(SpoutServer server, Transform transform, Controller controller) {
		this.server = server;
		transformAndManager = new TransformAndManager(transform, this.server.getEntityManager());
		this.controller = controller;
		transformAndManagerLive.set(transformAndManager.copy());
	}

	public SpoutEntity(SpoutServer server, Point point, Controller controller) {
		this(server, new Transform(point, Quaternion.identity , Vector3.ONE), controller);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public Controller getControllerLive() {
		return controller;
	}
	
	public void setController(Controller controller) {
		controller.attachToEntity(this);
		this.controller = controller;
		controller.onAttached();
	}

	@Override
	public Transform getTransform() {
		return transformAndManager.transform;
	}
	
	@Override
	public Transform getLiveTransform() {
		return transformAndManagerLive.get().transform;
	}

	@Override
	public void setTransform(Transform transform) {
		//boolean success = false;

		//while (!success) {
			
			// TODO - code to handle world level entity managers
			
			Point newPosition = transform.getPosition();
			Region newRegion = newPosition.getWorld().getRegion(newPosition);
			
			// TODO - entity moved into unloaded chunk - what happens for normal entities
			if (newRegion == null && this.getController() instanceof PlayerController) {
				newRegion = newPosition.getWorld().getRegion(newPosition, true);
			}
			EntityManager newEntityManager = ((SpoutRegion)newRegion).getEntityManager();
			
			TransformAndManager newTM = new TransformAndManager(transform, newEntityManager);
			
			transformAndManagerLive.set(newTM);
			
		//}
		
	}
	
	public boolean kill() {
		TransformAndManager oldTM = transformAndManagerLive.getAndSet(new TransformAndManager(null, null));
		return oldTM.transform == null && oldTM.entityManager == null;
	}
	
	public boolean isDead() {
		return transformAndManager.transform == null && transformAndManager.entityManager == null;
	}
	
	@Override
	public void setModel(Model model) {
		this.model = model;
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public void setCollision(CollisionModel model) {
		this.collision = model;

	}

	@Override
	public CollisionModel getCollision() {
		return collision;
	}

	/**
	 * @param dt milliseonds since the last tick
	 */
	public void onTick(float dt) {
		if (controller != null) controller.onTick(dt);
	}
	
	@Override
	public boolean isSpawned() {
		return (id != NOTSPAWNEDID);
	}
	
	/**
	 * Called when the tick is finished and collisions need to be resolved
	 * and move events fired
	 */
	public void resolve() {
		//Resolve Collisions Here
		
		//Check to see if we should fire off a Move event
	}
	
	public void preSnapshot() {
		TransformAndManager live = transformAndManagerLive.get();
		if (live == null || transformAndManager == null || live.entityManager != transformAndManager.entityManager) {
			if (transformAndManager != null && transformAndManager.entityManager != null) {
				transformAndManager.entityManager.deallocate(this);
			}
			if (live != null) {
				if (live.entityManager != null) {
					live.entityManager.allocate(this);
				} else {
					if (live.transform == null && controller != null) {
						controller.onDeath();
					}
				}
			}
		}
	}
	
	public void copyToSnapshot() {
		transformAndManager = transformAndManagerLive.get();
	}
	
	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		super.setMetadata(this, metadataKey, newMetadataValue);
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return super.getMetadata(this, metadataKey);
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return super.hasMetadata(this, metadataKey);
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		super.removeMetadata(this, metadataKey, owningPlugin);		
	}

	@Override
	public boolean is(Class<? extends Controller> clazz) {
		return clazz.isAssignableFrom(this.getController().getClass());
	}

	@Override
	public void setMetadata(String key, int value) {
		setMetadata(key, new MetadataStringValue(value));		
	}

	@Override
	public void setMetadata(String key, float value) {
		setMetadata(key, new MetadataStringValue(value));

	}

	@Override
	public void setMetadata(String key, String value) {
		setMetadata(key, new MetadataStringValue(value));

	}

	@Override
	public Chunk getChunk() {
		Point position = transformAndManager.transform.getPosition();
		return position.getWorld().getChunk(position, true);
	}

	@Override
	public Region getRegion() {
		Point position = transformAndManager.transform.getPosition();
		return position.getWorld().getRegion(position, true);
	}
	
	private static class TransformAndManager {
		public final Transform transform;
		public final EntityManager entityManager;
		
		public TransformAndManager() {
			this.transform = null;
			this.entityManager = null;
		}
		
		public TransformAndManager(Transform transform, EntityManager entityManager) {
			if (transform != null) {
				this.transform = transform.copy();
			} else {
				this.transform = null;
			}
			this.entityManager = entityManager;
		}
		
		public TransformAndManager copy() {
			return new TransformAndManager(transform != null ? transform.copy() : null, entityManager);
		}
	}
}
