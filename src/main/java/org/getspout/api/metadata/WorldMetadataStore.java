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

import org.getspout.api.geo.World;

/**
 * An WorldMetadataStore stores metadata values for {@link World} objects.
 */
public class WorldMetadataStore extends MetadataStoreBase<World> implements MetadataStore<World> {

	/**
	 * Generates a unique metadata key for a {@link World} object based on the
	 * world UID.
	 *
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
