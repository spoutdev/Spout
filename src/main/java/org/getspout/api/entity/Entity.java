package org.getspout.api.entity;

import org.getspout.api.collision.model.CollisionModel;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.metadata.Metadatable;
import org.getspout.api.model.Model;

/**
 * Represents an entity, which may or may not be spawned into the world.
 */
public interface Entity extends Metadatable {
	
	public int getId();
	
	public Controller getController() ;
	public void setController(Controller controller);
	public Transform getTransform();
	
	public void setModel(Model model);
	public Model getModel();
	
	public void setCollision(CollisionModel model);
	public CollisionModel getCollision();
	
	
	/**
	 * Returns true if this entity's controller is the provided controller
	 * 
	 * @param clazz 
	 * @return true if this entity's controller is the provided controller
	 */
	public boolean is(Class<? extends Controller> clazz);
	
	/**
	 * Returns true if this entity is spawned and being Simulated in the world
	 * 
	 * @return spawned
	 */
	public boolean isSpawned();

	/**
	 * Gets the chunk the entity resides in, or null if unspawned.
	 * 
	 * @return chunk
	 */
	public Chunk getChunk();
	
	/**
	 * Gets the region the entity is associated and managed with, or null if unspawned.
	 * 
	 * @return region
	 */
	public Region getRegion();
}
