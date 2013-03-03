/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.resource.ResourceNotFoundException;
import org.spout.api.resource.ResourcePathResolver;

/**
 * A FileSystem handles the loading of client and server resources.
 * 
 * On the {@link Client}, loading a resource will load the resource from the hard-drive.  
 * On the {@link Server}, it will notify all clients to load the resource, as well as provide a representation of that resource.
 */
public interface FileSystem {
	/**
	 * Initializes this implementation of the FileSystem.
	 * 
	 * This includes making the proper directory structure for the server or client.
	 */
	public abstract void init(Engine engine);

	/**
	 * Called after startup
	 */
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
	 * @throws ResourceNotFoundException - if the resource is not found
	 */
	public abstract void loadResource(URI path) throws ResourceNotFoundException;

	/**
	 * Attempt to load the given path as a resource into the FileSystem.
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

	/**
	 * Returns the {@link ResourcePathResolver} that passes the
	 * {@link ResourcePathResolver#existsInPath(java.net.URI)} call for the
	 * specified path signifying where to get a {@link Resource} from at the
	 * specified path.
	 *
	 * @param path to the resource
	 * @return resolution for path
	 */
	public abstract ResourcePathResolver getPathResolver(URI path);

	/**
	 * Returns the {@link ResourcePathResolver} that passes the
	 * {@link ResourcePathResolver#existsInPath(java.net.URI)} call for the
	 * specified path signifying where to get a {@link Resource} from at the
	 * specified path.
	 *
	 * @param path to the resource
	 * @return resolution for path
	 */
	public abstract ResourcePathResolver getPathResolver(String path);

	/**
	 * Attempts to load all resources into the FileSystem at the given
	 * directory. Specified path must end with a '/' to identify the
	 * path as a directory.
	 *
	 * @param uri to load resources from
	 */
	public abstract void loadResources(URI uri);

	/**
	 * Attempts to load all resources into the FileSystem at the given
	 * directory. Specified path must end with a '/' to identify the
	 * path as a directory.
	 *
	 * @param path to load resources from
	 */
	public abstract void loadResources(String path);

	/**
	 * Returns all the loaded resources in the FileSystem at the given
	 * directory. If any of the resources in the specified directory
	 * are not loaded this method will attempt to load the unloaded
	 * resource. The specified type for the List must reflect the
	 * given scheme in the URI or a {@link ClassCastException} will be
	 * thrown. The given URI's path must end with a '/' to identify the
	 * path as a directory.
	 *
	 * @param uri of the directory to get the resources from
	 * @param <T> type of the {@link Resource} to load; this type parameter
	 * must reflect the scheme of the URI or else a {@link ClassCastException}
	 * will be thrown
	 * @throws ClassCastException if type parameter does not reflect URI scheme
	 * @return a list of <T> loaded from the directory at the specified URI
	 */
	public abstract <T extends Resource> List<T> getResources(URI uri);

	/**
	 * Returns all the loaded resources in the FileSystem at the given
	 * directory. If any of the resources in the specified directory
	 * are not loaded this method will attempt to load the unloaded
	 * resource. The specified type for the List must reflect the
	 * given scheme in the URI or a {@link ClassCastException} will be
	 * thrown. The given URI's path must end with a '/' to identify the
	 * path as a directory.
	 *
	 * @param path of the directory to get the resources from
	 * @param <T> type of the {@link Resource} to load; this type parameter
	 * must reflect the scheme of the URI or else a {@link ClassCastException}
	 * will be thrown
	 * @throws ClassCastException if type parameter does not reflect URI scheme
	 * @return a list of <T> loaded from the directory at the specified URI
	 */
	public abstract <T extends Resource> List<T> getResources(String path);
}