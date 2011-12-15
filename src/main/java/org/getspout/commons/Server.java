package org.getspout.commons;

/**
 * Represents the server-specific implementation of Minecraft.
 */
public interface Server extends Game{
	
	/**
	 * Returns the name this server, if set.
	 * 
	 * @return server name
	 */
	public String getName();
	
	/**
	 * Returns true if this server is using a whitelist.
	 * 
	 * @return whitelist enabled
	 */
	public boolean isWhitelist();
	
	/**
	 * Sets the whitelist value of this server.
	 * 
	 * @param whitelist value to set
	 */
	public void setWhitelist(boolean whitelist);
	
	/**
	 * Reads the whitelist file from the disk, updating the players that are allowed to join where nessecary.
	 */
	public void updateWhitelist();
	
	/**
	 * Gets an array of all of the player names that are on the whitelist.
	 * 
	 * @return whitelisted player names
	 */
	public String[] getWhitelistedPlayers();
}
