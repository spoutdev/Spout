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
package org.spout.api;

import java.io.InputStream;
import java.net.URI;

import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.resource.ResourceNotFoundException;

/**
 * A FileSystem handles the loading of client and server resources.
 * 
 * On the {@link Client}, loading a resource will load the resource from the hard-drive.  
 * On the {@link Server}, it will notify all clients to load the resource, as well as provide a representation of that resource.
 *
 */
public interface FileSystem {
	/**
	 * Initializes this implementation of the FileSystem.
	 * 
	 * This includes making the proper directory structure for the server or client.
	 */
	public abstract void init();

	public abstract void postStartup();

	/**
	 * Attempts to load the given path as an {@link InputStream}. If the file
	 * can not be found the system will attempt to find the file in the
	 * '/fallbacks/' directory of the jar.
	 * 
	 * If an invalid path or resource is passed in, this method will throw an
	 * {@link InvalidArgumentException}
	 * 
	 * @param path to the resource
	 * @return {@link InputStream} of the given path.
	 * @throws ResourceNotFoundException - if the path to the resource is
	 *             invalid, or does not exist.
	 */
	public abstract InputStream getResourceStream(URI path) throws ResourceNotFoundException;

	/**
	 * Attempts to load the given String as an {@link InputStream} 
	 * This method will attempt to invoke {@link #getResource(URI)} if the path is valid.
	 * If an invalid path is passed in, this method will throw an {@link InvalidArgumentException}
	 * 
	 * @param path to the resource
	 * @return {@link InputStream} of the given path.
	 */
	public abstract InputStream getResourceStream(String path);

	/**
	 * Registers the given resource loader.
	 * 
	 * @param loader The loader to register.
	 */
	public abstract void registerLoader(ResourceLoader<? extends Resource> loader);
	
	/**
	 * Attempts to load the given path as a resource into the FileSystem.
	 * 
	 * @param path to the resource being loaded.
	 * @throws ResourceNotFoundException
	 */
	public abstract void loadResource(URI path) throws ResourceNotFoundException;

	/**
	 * Attempst to load the given path as a resource into the FileSystem.
	 * this method will attempt to invoke {@link #loadResource(URI)} if the path is valid.
	 * 
	 * @param path to the resource being loaded.
	 */
	public abstract void loadResource(String path);

	/**
	 * Gets the loaded resource from the FileSystem.  If the resource has not yet been cached
	 * it will attempt to invoke {{@link #loadResource(URI)}.
	 * 
	 * @param path to the resource.
	 * @return {@link Resource}
	 */
	public abstract Resource getResource(URI path);

	/**
	 * Gets the loaded resource from the FileSystem. If the resource has not yet
	 * been cached it will attempt to invoke {{@link #loadResource(String)}.
	 * 
	 * @param path to the resource.
	 * @return {@link Resource}
	 */
	public abstract Resource getResource(String path);

}