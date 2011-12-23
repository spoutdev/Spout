package org.getspout.api.indev.entity;

import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.metadata.Metadatable;

/**
 * API Entity.  
 * 
 * 
 */
public abstract class Entity implements Metadatable {
	Transform transform = new Transform();
	Controller controller;
	
	
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	public Transform getTransform() {
		return transform;
	}
	
	public void onTick(float dt){
		if(controller != null) controller.onTick(dt);
	}
}
