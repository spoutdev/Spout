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
package org.getspout.unchecked.api.metadata;

import org.getspout.unchecked.api.OfflinePlayer;

/**
 * A PlayerMetadataStore stores metadata for {@link org.bukkit.entity.Player}
 * and {@link OfflinePlayer} objects.
 */
public class PlayerMetadataStore extends MetadataStoreBase<OfflinePlayer> implements MetadataStore<OfflinePlayer> {
	/**
	 * Generates a unique metadata key for {@link org.bukkit.entity.Player} and
	 * {@link OfflinePlayer} using the player name.
	 *
	 * @see MetadataStoreBase#Disambiguate(Object, String)
	 * @param player
	 * @param metadataKey The name identifying the metadata value
	 * @return
	 */

	@Override
	protected String disambiguate(OfflinePlayer player, String metadataKey) {
		return player.getName().toLowerCase() + ":" + metadataKey;
	}
}
