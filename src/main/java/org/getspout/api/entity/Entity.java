package org.getspout.api.entity;

import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.metadata.Metadatable;

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
	
	public void onTick(float dt);
	
	public boolean is(Class<?> clazz);
}
