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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;

public interface FileSystem {
	/**
	 * The standard permission for installing plugins by command line.
	 */
	public static final String INSTALLATION_PERMISSION = "spout.install";

	/**
	 * Called before engine is started.
	 */
	public void init();

	/**
	 * Called after engine is started.
	 */
	public void postStartup();

	/**
	 * Returns a set of all registered loaders.
	 *
	 * @return all loaders
	 */
	public Set<ResourceLoader> getLoaders();

	/**
	 * Returns the loader with the specified scheme. There can only ever be one loader per scheme in the FileSystem.
	 *
	 * @param scheme to get
	 * @return loader with specified scheme
	 */
	public ResourceLoader getLoader(String scheme);

	/**
	 * Registers the specified loader.
	 *
	 * @param loader to register
	 */
	public void registerLoader(ResourceLoader loader);

	/**
	 * Returns an {@link InputStream} of a resource at the specified {@link URI}.
	 *
	 * @param uri to get stream from
	 * @return input stream
	 * @throws ResourceNotFoundException if there is no resource at specified path
	 */
	public InputStream getResourceStream(URI uri) throws ResourceNotFoundException;

	/**
	 * Returns an {@link InputStream} of a resource at the specified {@link URI}.
	 *
	 * @param uri to get stream from
	 * @return input stream
	 */
	public InputStream getResourceStream(String uri);

	/**
	 * Adds the resource at the specified location to the system's resource cache.
	 *
	 * @param uri to load resource at
	 * @throws LoaderNotFoundException if there is no loader for the specified scheme
	 * @throws ResourceNotFoundException if there is no resource at the specified path
	 * @throws IOException if there was a problem obtaining/disposing the input stream
	 */
	public void loadResource(URI uri) throws LoaderNotFoundException, ResourceNotFoundException, IOException;

	/**
	 * Adds the resource at the specified location to the system's resource cache.
	 *
	 * @param uri to load resource at
	 * @throws LoaderNotFoundException if there is no loader for the specified scheme
	 * @throws ResourceNotFoundException if there is no resource at the specified path
	 * @throws IOException if there was a problem obtaining/disposing the input stream
	 */
	public void loadResource(String uri) throws LoaderNotFoundException, ResourceNotFoundException, IOException;

	/**
	 * Returns the resource at the specified path with an inferred type. If this resource is not loaded when this is called, it will be automatically loaded and cached before returning the resource. This
	 * call assumes that the inferred return type is actually the correct type of the resource loader's return type. If the resource is not found in the specified path, this call will then attempt to
	 * load the fallback resource specified in the {@link ResourceLoader} of this scheme; if that is null, the returned resource will be null.
	 *
	 * @param uri to get resource from
	 * @param <R> inferred type of resource
	 * @return resource at path
	 */
	public <R> R getResource(URI uri);

	/**
	 * Returns the resource at the specified path with an inferred type. If this resource is not loaded when this is called, it will be automatically loaded and cached before returning the resource. This
	 * call assumes that the inferred return type is actually the correct type of the resource loader's return type. If the resource is not found in the specified path, this call will then attempt to
	 * load the fallback resource specified in the {@link ResourceLoader} of this scheme; if that is null, the returned resource will be null.
	 *
	 * @param uri to get resource from
	 * @param <R> inferred type of resource
	 * @return resource at path
	 */
	public <R> R getResource(String uri);

	/**
	 * Returns a list of all the resources in the specified directory.
	 *
	 * @param uri to get resources from
	 * @param <R> type of resources
	 * @return resources
	 * @see {@link #getResource(java.net.URI)}
	 */
	public <R> List<R> getResources(URI uri);

	/**
	 * Returns a list of all the resources in the specified directory.
	 *
	 * @param uri to get resources from
	 * @param <R> type of resources
	 * @return resources
	 * @see #getResource(java.net.URI)
	 */
	public <R> List<R> getResources(String uri);

	/**
	 * Returns a list of all {@link ResourcePathResolver}s that are currently on the system. These resolvers handle the {@link URI}s passed to {@link #getResource(java.net.URI)} to find a suitable input
	 * stream for the resource.
	 *
	 * @return list of path resolvers
	 */
	public List<ResourcePathResolver> getPathResolvers();

	/**
	 * Adds a new path resolver to be queried when attempting to find a suitable input stream for a specified {@link URI} in {@link #getResource(java.net.URI)}
	 *
	 * @param pathResolver to add
	 */
	public void addPathResolver(ResourcePathResolver pathResolver);

	/**
	 * Removes the path resolver to be queried when attempting to find a suitable input stream for a specified {@link URI} in {@link #getResource(java.net.URI)}
	 *
	 * @param pathResolver to remove
	 */
	public void removePathResolver(ResourcePathResolver pathResolver);

	/**
	 * Requests an install on the system.
	 *
	 * @param name of plugin to install
	 * @param uri location of plugin
	 */
	public void requestPluginInstall(String name, URI uri);
}