/*
 * This file is part of SpoutAPI (http://www.spout.org/).
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
package org.spout.api.metadata;

import java.util.List;

import org.spout.api.plugin.Plugin;

/**
 * Stores metadata.
 */
public interface MetadataStore<TSubject> {

	/**
	 * Adds a metadata value to an object.
	 *
	 * @param subject The object receiving the metadata.
	 * @param metadataKey A unique key to identify this metadata.
	 * @param newMetadataValue
	 */
	public void setMetadata(TSubject subject, String metadataKey, MetadataValue newMetadataValue);

	/**
	 * Returns all metadata values attached to an object. If multiple plugins
	 * have attached metadata, each will value will be included.
	 *
	 * @param subject
	 * @param metadataKey
	 * @return
	 */
	public List<MetadataValue> getMetadata(TSubject subject, String metadataKey);

	/**
	 * Tests to see if a metadata attribute has been set on an object.
	 *
	 * @param subject
	 * @param metadataKey
	 * @return
	 */
	public boolean hasMetadata(TSubject subject, String metadataKey);

	/**
	 * Removes a metadata item owned by a plugin from a subject.
	 *
	 * @param subject
	 * @param metadataKey
	 * @param owningPlugin
	 */
	public void removeMetadata(TSubject subject, String metadataKey, Plugin owningPlugin);

	/**
	 * Invalidates all metadata in the metadata store that originates from the
	 * given plugin. Doing this will force each invalidated metadata item to be
	 * recalculated the next time it is accessed.
	 *
	 * @param owningPlugin
	 */
	public void invalidateAll(Plugin owningPlugin);

}
