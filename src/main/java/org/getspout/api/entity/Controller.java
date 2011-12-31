package org.getspout.api.entity;

public abstract class Controller {
	protected Entity parent;
	public void attachToEntity(Entity e){
		this.parent = e;
	}
	
	public abstract void onAttached();
	
	/**
	 * Called once per tick
	 * 
	 * @param dt milliseconds since the last tick
	 */
	public abstract void onTick(long dt);
}
