/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.resource;

import java.io.InputStream;
import java.net.URI;

/**
 * An interface to load resources.
 * 
 * @param <E>
 */
public interface ResourceLoader<E extends Resource> {
	/**
	 * Loads a resource from the given input stream.
	 * 
	 * @param stream
	 * @return
	 */
	public E getResource(InputStream stream);

	/**
	 * Gets the resource that corresponds with the given URI.
	 * 
	 * @param resource
	 * @return
	 */
	public E getResource(URI resource);

	/**
	 * Returns the fallback name for this resource.
	 * 
	 * @return
	 */
	public String getFallbackResourceName();

	/**
	 * Returns the protocol that this loader should load. For example,
	 * <code>sound://</code> would have the protocol <code>sound</code>.
	 * 
	 * @return
	 */
	public String getProtocol();

	/**
	 * Returns the file extensions that resources loaded with this loader should
	 * have. This excludes the preceding period; for example, a file named
	 * "sound.wav" has the extension "wav".
	 * 
	 * @return
	 */
	public String[] getExtensions();
}
