package org.getspout.api.player;

import org.getspout.api.entity.Entity;

public interface Player {
	
	/**
	 * Gets the player's name
	 * 
	 * @return the player's name
	 */
	public String getName();
	
	/**
	 * Gets the entity corresponding to the player
	 * 
	 * @return the entity, or null if the player is offline
	 */
	public Entity getEntity();
	
	/**
	 * Gets the player's position.  For offline players, this is where they will appear when they login
	 * 
	 * @return
	 */
	// TODO need to add position checks etc.

}
