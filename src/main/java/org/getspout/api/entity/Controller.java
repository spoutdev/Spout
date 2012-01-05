package org.getspout.api.entity;

public abstract class Controller {
	protected Entity parent;
	public void attachToEntity(Entity e){
		this.parent = e;
	}
	
	public abstract void onAttached();
	
	/**
	 * Called when the entity dies.
	 * 
	 * Called just before the snapshotStart method.
	 */
	public void onDeath() {
	}
	/**
	 * 
	 * @param dt the number of seconds since last update
	 */
	public abstract void onTick(float dt);
	
	/**
	 * Called just before a snapshot update.  
	 * 
	 * This is intended purely as a monitor based step.  
	 * 
	 * NO updates should be made to the entity at this stage.
	 * 
	 * It can be used to send packets for network update.
	 */
	public void snapshotStart() {
	}
}
