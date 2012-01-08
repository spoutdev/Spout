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

import org.spout.api.plugin.Plugin;

public interface MetadataValue {

	/**
	 * Attempts to convert this metadata value to an int and return it.
	 *
	 * @return
	 * @throws MetadataConversionException
	 */
	public int asInt() throws MetadataConversionException;

	/**
	 * Attempts to convert this metadata value to a double and return it.
	 *
	 * @return
	 * @throws MetadataConversionException
	 */
	public double asDouble() throws MetadataConversionException;

	/**
	 * Attempts to convert this metadata value to a boolean and return it.
	 *
	 * @return
	 * @throws MetadataConversionException
	 */
	public boolean asBoolean() throws MetadataConversionException;

	/**
	 * Returns the String representation of this metadata item.
	 *
	 * @return
	 */
	public String asString();

	/**
	 * Returns the {@link Plugin} that created this metadata item.
	 *
	 * @return
	 */
	public Plugin getOwningPlugin();

	/**
	 * Invalidates this metadata item, forcing it to recompute when next
	 * accessed.
	 */
	public void invalidate();

}
