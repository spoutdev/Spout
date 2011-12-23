package org.getspout.api.metadata;

import org.getspout.api.entity.Entity;


/**
 * An EntityMetadataStore stores metadata values for all {@link Entity} classes
 * an their descendants.
 */
public class EntityMetadataStore extends MetadataStoreBase<Entity> implements MetadataStore<Entity> {

	/**
	 * Generates a unique metadata key for an {@link Entity} entity ID.
	 *
	 * @see MetadataStoreBase#Disambiguate(Object, String)
	 * @param entity
	 * @param metadataKey
	 * @return
	 */

	@Override
	protected String disambiguate(Entity entity, String metadataKey) {
		return Integer.toString(entity.getId()) + ":" + metadataKey;
	}

}
