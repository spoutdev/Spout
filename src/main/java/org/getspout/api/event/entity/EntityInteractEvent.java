package org.getspout.api.event.entity;

import org.getspout.api.entity.Entity;
import org.getspout.api.event.HandlerList;

/**
 * Called when an entity interacts with an Entity
 * 
 *
 */
public class EntityInteractEvent extends EntityEvent {
	private static HandlerList handlers = new HandlerList();
	Entity interacted;
	
	public Entity getInteractedWith(){
		return interacted;
	}
	
	public void setInteractedWith(Entity e){
		this.interacted = e;
	}
	
	public HandlerList getHandlers() {
		
		return handlers;
	}

}
