package org.getspout.commons.metadata;

import org.getspout.commons.World;

/**
 * An WorldMetadataStore stores metadata values for {@link World} objects.
 */
public class WorldMetadataStore extends MetadataStoreBase<World> implements MetadataStore<World> {

	/**
	 * Generates a unique metadata key for a {@link World} object based on the world UID.
	 * @see WorldMetadataStore#Disambiguate(Object, String)
	 * @param world
	 * @param metadataKey
	 * @return
	 */
	@Override
	protected String disambiguate(World world, String metadataKey) {
		return world.getUID().toString() + ":" + metadataKey;
	}

}
