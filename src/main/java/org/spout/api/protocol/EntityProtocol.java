package org.spout.api.protocol;

import org.spout.api.entity.Entity;

/**
 * A class that generates messages associated with entities
 */
public interface EntityProtocol {

	/**
	 * Gets a message to spawn the entity. The entity should spawn at the
	 * location at the last snapshot
	 *
	 * @param entity the entity
	 * @return the spawn message
	 */
	public Message[] getSpawnMessage(Entity entity);

	/**
	 * Gets a message to destroy the entity.
	 *
	 * @param entity the entity
	 * @return the destroy message
	 */
	public Message[] getDestroyMessage(Entity entity);

	/**
	 * Gets a message to update the entity. This should move the entity from its
	 * snapshot position to its live position.
	 *
	 * @param entity the entity
	 * @return the update message
	 */
	public Message[] getUpdateMessage(Entity entity);

}
