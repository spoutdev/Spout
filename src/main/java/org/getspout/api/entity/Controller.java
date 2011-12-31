package org.getspout.api.entity;

public abstract class Controller {
	protected Entity parent;
	public void attachToEntity(Entity e){
		this.parent = e;
	}
	
	public abstract void onAttached();
	/**
	 * 
	 * @param dt the number of seconds since last update
	 */
	public abstract void onTick(float dt);
}
