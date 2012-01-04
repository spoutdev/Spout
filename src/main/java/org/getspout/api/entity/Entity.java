package org.getspout.api.entity;

import org.getspout.api.collision.model.CollisionModel;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.metadata.Metadatable;
import org.getspout.api.model.Model;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;

/**
 * Represents an entity, which may or may not be spawned into the world.
 */
public interface Entity extends Metadatable {
	
	public int getId();
	
	// TODO - should these be main thread only ?
	public Controller getController() ;
	public void setController(Controller controller);
	
	/**
	 * Gets the transform for entity
	 * 
	 * @return
	 */
	@SnapshotRead
	public Transform getTransform();
	
	/**
	 * Gets the live/unstable position of the entity.  
	 * 
	 * Use of live reads may have a negative performance impact
	 * 
	 * @return
	 */
	@LiveRead
	public Transform getLiveTransform();
	
	/**
	 * Set transform
	 * 
	 * @param transform new Transform
	 */
	@DelayedWrite
	public void setTransform(Transform transform);
	
	// TODO - add thread timing annotations
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
	@SnapshotRead
	public Chunk getChunk();
	
	/**
	 * Gets the region the entity is associated and managed with, or null if unspawned.
	 * 
	 * @return region
	 */
	@SnapshotRead
	public Region getRegion();
	
	/**
	 * Called just before a snapshot update.  
	 * 
	 * This is intended purely as a monitor based step.  
	 * 
	 * No updates should be made to the entity at this stage.
	 * 
	 * It can be used to send packets for network update.
	 */
	public void preSnapshot();
	
	/**
	 * Kills the entity.  This takes effect at the next snapshot.
	 * 
	 * If the entity's position is set before the next snapshot, the entity won't be removed.
	 * 
	 * @return true if the entity was alive
	 */
	@DelayedWrite
	@LiveRead
	public boolean kill();
}
