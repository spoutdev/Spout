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

import org.getspout.api.plugin.Plugin;

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
