/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.resource;

import java.io.InputStream;
import java.net.URI;

public interface ResourcePathResolver {
	/**
	 * Returns true if the specified path exists in the host.
	 *
	 * @param host of path
	 * @param path within the host
	 * @return true if specified path exists
	 */
	public boolean existsInPath(String host, String path);

	/**
	 * Returns true if the specified path exists in the host.
	 *
	 * @param uri including the host and path of the resource
	 * @return true if specified path exists
	 */
	public boolean existsInPath(URI uri);

	/**
	 * Returns an {@link java.io.InputStream} at the given host and path to be resolved by the implementing class.
	 *
	 * @param host of stream
	 * @param path within the host
	 * @return input stream
	 */
	public InputStream getStream(String host, String path);

	/**
	 * Returns an {@link java.io.InputStream} at the given host and path to be resolved by the implementing class.
	 *
	 * @param uri including the host and path of the resource
	 * @return input stream
	 */
	public InputStream getStream(URI uri);

	/**
	 * Lists all files in the specified directory within the host. The specified path must end in a '/' to identify the path as a directory.
	 *
	 * @param host of the directory
	 * @param path within the host
	 * @return array of the names of the files within the specified path
	 */
	public String[] list(String host, String path);

	/**
	 * Lists all files in the specified directory within the host. The specified path must end in a '/' to identify the path as a directory.
	 *
	 * @param uri including the host and path of the resource
	 * @return array of the names of the files within the specified path
	 */
	public String[] list(URI uri);
}
