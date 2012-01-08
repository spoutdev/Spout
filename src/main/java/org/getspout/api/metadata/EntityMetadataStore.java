/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
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
