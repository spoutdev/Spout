package org.getspout.api.player;

import org.getspout.api.command.CommandSource;
import org.getspout.api.entity.Entity;
import org.getspout.api.protocol.Session;

import java.net.InetAddress;

public interface Player extends CommandSource {
	
	/**
	 * Gets the player's name
	 * 
	 * @return the player's name
	 */
	public String getName();

	/**
	 * Sends a message as if the player had typed it into their chat gui.
	 *
	 * @param message The message to send
	 */
	public void chat(String message);
	
	/**
	 * Gets the entity corresponding to the player
	 * 
	 * @return the entity, or null if the player is offline
	 */
	public Entity getEntity();
	
	/**
	 * Gets the session associated with the Player.
	 * 
	 * @return the session, or null if the player is offline
	 */
	public Session getSession();
	
	/**
	 * Gets if the player is online
	 * 
	 * @return true if online
	 */
	public boolean isOnline();

	/**
	 * Gets the sessions address
	 * This is equivalent to getSession().getAddress().getAddress();
	 * @return The session's address
	 */
	public InetAddress getAddress();
	
}
