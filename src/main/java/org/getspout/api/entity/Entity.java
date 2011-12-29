package org.getspout.api.entity;

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
	
	//Commented out because CollisionModel doesn't exist yet
	//public void setCollision(CollisionModel model);
	//public CollisionModel getCollision();
	
	public void onTick(float dt);
	
	public boolean is(Class<?> clazz);
}
