package org.getspout.api.entity;

public abstract class Controller {
	protected Entity parent;
	public void attachToEntity(Entity e){
		this.parent = e;
	}
	
	public abstract void onAttached();
	public abstract void onTick(float dt);
}
