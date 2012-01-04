package org.getspout.server.entity;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.getspout.api.collision.model.CollisionModel;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.entity.PlayerController;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.io.store.MemoryStore;
import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector3;
import org.getspout.api.metadata.EntityMetadataStore;
import org.getspout.api.metadata.MetadataStringValue;
import org.getspout.api.metadata.MetadataValue;
import org.getspout.api.model.Model;
import org.getspout.api.plugin.Plugin;
import org.getspout.api.util.StringMap;
import org.getspout.server.SpoutRegion;
import org.getspout.server.SpoutServer;

public class SpoutEntity extends EntityMetadataStore implements Entity {
	public final static int NOTSPAWNEDID = -1;
	public static final StringMap entityStringMap = new StringMap(null, new MemoryStore<Integer>(), null, 0, Short.MAX_VALUE);
	
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
		this.server = server;
		transformAndManager = new TransformAndManager(new Transform(point, Quaternion.identity , Vector3.ONE), this.server.getEntityManager());
		this.controller = controller;
		transformAndManagerLive.set(transformAndManager.copy());
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
				newRegion = newPosition.getWorld().getRegionLive(newPosition, true);
			}
			EntityManager newEntityManager = ((SpoutRegion)newRegion).getEntityManager();
			
			TransformAndManager newTM = new TransformAndManager(transform, newEntityManager);
			
			transformAndManagerLive.set(newTM);
			
		//}
		
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
			if (live != null && live.entityManager != null) {
				live.entityManager.allocate(this);
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
		return position.getWorld().getChunk(position);
	}

	@Override
	public Region getRegion() {
		Point position = transformAndManager.transform.getPosition();
		return position.getWorld().getRegion(position);
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
