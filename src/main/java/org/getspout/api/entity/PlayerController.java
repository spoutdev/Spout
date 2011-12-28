package org.getspout.api.entity;

import org.getspout.api.player.Player;

/**
 * Represents a Controller that is controlled by a player
 * An entity is a Player if entity.GetController() instanceof PlayerController == true
 *
 */
public abstract class PlayerController extends Controller {
	Player owner;
	
	public PlayerController(Player owner){
		this.owner = owner;
	}
	
	public Player getPlayer(){
		return owner;
	}
}
