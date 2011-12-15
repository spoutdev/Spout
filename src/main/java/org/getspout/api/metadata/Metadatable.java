/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.metadata;

import java.util.List;

import org.getspout.api.plugin.Plugin;

/**
 * This interface is implemented by all objects that can provide metadata about themselves.
 */
public interface Metadatable {

	/**
	 * Sets a metadata value in the implementing object's metadata store.
	 * @param metadataKey
	 * @param newMetadataValue
	 */
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue);

	/**
	 * Returns a list of previously set metadata values from the implementing object's metadata store.
	 * @param metadataKey
	 * @return A list of values, one for each plugin that has set the requested value.
	 */
	public List<MetadataValue> getMetadata(String metadataKey);

	/**
	 * Tests to see whether the implementing object contains the given metadata value in its metadata store.
	 * @param metadataKey
	 * @return
	 */
	public boolean hasMetadata(String metadataKey);

	/**
	 * Removes the given metadata value from the implementing object's metadata store.
	 * @param metadataKey
	 * @param owningPlugin This plugin's metadata value will be removed. All other values will be left untouched.
	 */
	public void removeMetadata(String metadataKey, Plugin owningPlugin);

}
