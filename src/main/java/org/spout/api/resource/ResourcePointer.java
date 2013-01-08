/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
import java.net.URISyntaxException;

import org.spout.api.Spout;

/**
 * A pointer to a resource, which loads the resource when needed<br>
 * To allow dynamic generation of the path, override the getPath method
 * 
 * @param <T> type of resource, must match the resource URI
 */
public class ResourcePointer<T extends Resource> {
	private T value;
	private final URI path;

	/**
	 * Constructs a new empty Resource Pointer pointing to no resource<br>
	 * Use this constructor if no resource is available yet and null should be returned when getting
	 */
	public ResourcePointer() {
		this((URI) null);
	}

	/**
	 * Constructs a new Resource Pointer pointing to the path specified
	 * 
	 * @param path to the resource in valid URI format
	 */
	public ResourcePointer(String path) {
		try {
			this.path = new URI(path);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("The resource path '" + path + "' is not a valid URI.", e);
		}
	}

	/**
	 * Constructs a new Resource Pointer pointing to the path specified
	 * 
	 * @param path to the resource
	 */
	public ResourcePointer(URI path) {
		this.path = path;
	}

	/**
	 * Gets the path to the Resource, can be null if no resource is specified
	 * 
	 * @return Resource path
	 */
	public URI getPath() {
		return this.path;
	}

	/**
	 * Gets an Input Stream pointing to the Resource as specified in the path
	 * 
	 * @return Resource input stream
	 */
	public InputStream getStream() throws ResourceNotFoundException {
		final URI path = this.getPath();
		if (path == null) {
			throw new ResourceNotFoundException("The Resource Pointer points to no Resource");
		}
		return Spout.getFilesystem().getResourceStream(this.getPath());
	}

	/**
	 * Gets the resource, or null if this Resource Pointer points to a null path<br>
	 * If the resource is not yet loaded, it is loaded
	 * 
	 * @return The Resource
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		if (this.value == null) {
			final URI path = this.getPath();
			if (path != null) {
				this.value = (T) Spout.getFilesystem().getResource(path);
			}
		}
		return this.value;
	}
}
