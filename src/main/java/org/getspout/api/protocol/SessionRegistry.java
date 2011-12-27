package org.getspout.api.protocol;


public interface SessionRegistry {

	/**
	 * Adds a new session.
	 *
	 * @param session The session to add.
	 */
	public void add(Session session);
	
	/**
	 * Removes a session.
	 *
	 * @param session The session to remove.
	 */
	public void remove(Session session);
	
}
