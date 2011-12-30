package org.getspout.api.entity;

import org.getspout.api.collision.model.CollisionModel;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.metadata.Metadatable;
import org.getspout.api.model.Model;

/**
 * API Entity.  
 * 
 * 
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
	 * @param clazz 
	 * @return
	 */
	public boolean is(Class<? extends Controller> clazz);
	
	/**
	 * Returns true if this entity is spawned and being Simulated in the world
	 * @return
	 */
	public boolean isSpawned();
}
