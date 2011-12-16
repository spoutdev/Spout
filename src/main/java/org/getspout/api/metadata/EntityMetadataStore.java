/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.metadata;

import org.getspout.api.entity.Entity;

/**
 * An EntityMetadataStore stores metadata values for all {@link Entity} classes an their descendants.
 */
public class EntityMetadataStore extends MetadataStoreBase<Entity> implements MetadataStore<Entity> {

	/**
	 * Generates a unique metadata key for an {@link Entity} entity ID.
	 * @see MetadataStoreBase#Disambiguate(Object, String)
	 * @param entity
	 * @param metadataKey
	 * @return
	 */
	@Override
	protected String disambiguate(Entity entity, String metadataKey) {
		return Integer.toString(entity.getEntityId()) + ":" + metadataKey;
	}

}
