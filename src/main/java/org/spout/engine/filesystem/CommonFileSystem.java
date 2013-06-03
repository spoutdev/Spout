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
package org.spout.engine.filesystem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.resource.ResourceNotFoundException;
import org.spout.api.resource.ResourcePathResolver;
import org.spout.api.resource.FileSystem;
import org.spout.api.resource.LoaderNotFoundException;
import org.spout.api.resource.ResourceLoader;

import org.spout.engine.filesystem.path.FilePathResolver;
import org.spout.engine.filesystem.path.JarFilePathResolver;
import org.spout.engine.filesystem.path.ZipFilePathResolver;
import org.spout.engine.filesystem.resource.loader.CommandBatchLoader;

public class CommonFileSystem implements FileSystem {
	public static final File PLUGINS_DIRECTORY = new File("plugins");
	public static final File RESOURCES_DIRECTORY = new File("resources");
	public static final File CACHE_DIRECTORY = new File("cache");
	public static final File CONFIG_DIRECTORY = new File("config");
	public static final File UPDATES_DIRECTORY = new File("updates");
	public static final File DATA_DIRECTORY = new File("data");
	public static final File WORLDS_DIRECTORY = new File("worlds");

	private final Set<ResourceLoader> loaders = new HashSet<ResourceLoader>();
	private final Map<URI, Object> loadedResources = new HashMap<URI, Object>();
	private final List<ResourcePathResolver> pathResolvers = new ArrayList<ResourcePathResolver>();
	private boolean initialized;

	private void createDirs() {
		if (!PLUGINS_DIRECTORY.exists()) PLUGINS_DIRECTORY.mkdirs();
		if (!RESOURCES_DIRECTORY.exists()) RESOURCES_DIRECTORY.mkdirs();
		if (!CACHE_DIRECTORY.exists()) CACHE_DIRECTORY.mkdirs();
		if (!CONFIG_DIRECTORY.exists()) CONFIG_DIRECTORY.mkdirs();
		if (!UPDATES_DIRECTORY.exists()) UPDATES_DIRECTORY.mkdirs();
		if (!DATA_DIRECTORY.exists()) DATA_DIRECTORY.mkdirs();
		if (!WORLDS_DIRECTORY.exists()) WORLDS_DIRECTORY.mkdirs();
	}

	@Override
	public void init() {
		registerLoader(new CommandBatchLoader());

		createDirs();
		pathResolvers.add(new FilePathResolver(CACHE_DIRECTORY.getPath()));
		pathResolvers.add(new ZipFilePathResolver(RESOURCES_DIRECTORY.getPath()));
		pathResolvers.add(new JarFilePathResolver());
		initialized = true;
	}

	private void loadFallback(ResourceLoader loader) {
		String fallback = loader.getFallback();
		if (fallback != null) {
			try {
				loadResource(fallback);
			} catch (LoaderNotFoundException e) {
				throw new IllegalArgumentException("Specified fallback has no associated loader", e);
			} catch (ResourceNotFoundException e) {
				throw new IllegalArgumentException("Specified fallback does not exist.", e);
			} catch (IOException e) {
				throw new IllegalStateException("Error while loading fallback resource", e);
			}
		}
	}

	@Override
	public void postStartup() {
		// load fallbacks
		for (ResourceLoader loader : loaders) {
			loadFallback(loader);
		}
	}

	@Override
	public Set<ResourceLoader> getLoaders() {
		return Collections.unmodifiableSet(loaders);
	}

	@Override
	public ResourceLoader getLoader(String scheme) {
		for (ResourceLoader loader : loaders) {
			if (loader.getScheme().equalsIgnoreCase(scheme)) {
				return loader;
			}
		}
		return null;
	}

	@Override
	public void registerLoader(ResourceLoader loader) {
		// load the fallback
		loaders.add(loader);
		if (initialized) {
			loadFallback(loader);
		}
	}

	@Override
	public InputStream getResourceStream(URI path) throws ResourceNotFoundException {
		// Find the correct search path
		ResourcePathResolver searchPath = getPathResolver(path);
		if (searchPath != null) {
			return searchPath.getStream(path);
		}

		// No path found? Open our jar and grab the fallback 'file' scheme
		Spout.getEngine().getLogger().log(Level.WARNING, "Tried to load {0} it isn''t found!  Using system fallback", path);
		String scheme = path.getScheme();
		if (!scheme.equals("file")) {
			throw new ResourceNotFoundException(path.toString());
		}
		return CommonFileSystem.class.getResourceAsStream("/fallbacks/" + path.getPath());
	}

	@Override
	public InputStream getResourceStream(String path) {
		try {
			return getResourceStream(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to get a Resource Stream URI, but" + path + " Isn't a URI", e);
		} catch (ResourceNotFoundException e) {
			throw new IllegalArgumentException("Resource not found at path '" + path + "':", e);
		}
	}

	private ResourcePathResolver getPathResolver(URI uri) {
		for (ResourcePathResolver resolver : pathResolvers) {
			if (resolver.existsInPath(uri)) {
				return resolver;
			}
		}
		return null;
	}

	@Override
	public void loadResource(URI uri) throws LoaderNotFoundException, ResourceNotFoundException, IOException {
		// find the loader
		// this needs to be thrown first, so we can use a fallback loader and know it exists
		String scheme = uri.getScheme();
		ResourceLoader loader = getLoader(scheme);
		if (loader == null) {
			throw new LoaderNotFoundException(scheme);
		}

		// grab the input stream
		ResourcePathResolver resolver = getPathResolver(uri);
		if (resolver == null) {
			throw new ResourceNotFoundException(uri.toString());
		}
		InputStream in = new BufferedInputStream(resolver.getStream(uri));

		// finally load
		Object resource = loader.load(in);
		if (resource == null) {
			throw new IllegalStateException("Loader for scheme '" + scheme + "' returned a null resource.");
		}
		loadedResources.put(uri, resource);

		// close the stream
		in.close();
	}

	@Override
	public void loadResource(String uri) throws LoaderNotFoundException, ResourceNotFoundException, IOException {
		try {
			loadResource(new URI(uri));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Specified URI is not valid.");
		}
	}

	@SuppressWarnings("unchecked")
	private <R> R tryCast(Object obj, String scheme) {
		try {
			return (R) obj;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Specified scheme '" + scheme + "' does not point to the inferred resource type.");
		}
	}

	@Override
	public <R> R getResource(URI uri) {
		if (loadedResources.containsKey(uri)) {
			// already loaded
			return tryCast(loadedResources.get(uri), uri.getScheme());
		}

		try {
			// not loaded yet
			loadResource(uri);
		} catch (LoaderNotFoundException e) {
			// scheme has not loader
			throw new IllegalArgumentException("No loader found for scheme " + uri.getScheme(), e);
		} catch (IOException e) {
			// error closing the stream
			throw new IllegalArgumentException("An exception occurred when loading the resource at " + uri.toString(), e);
		} catch (ResourceNotFoundException e) {
			// not found in path, try to load fallback resource
			Spout.getLogger().log(Level.WARNING, "No resource found at {0}, loading fallback...", uri.toString());
			String fallback = getLoader(uri.getScheme()).getFallback(); // assumption: loader is never null here
			if (fallback == null) {
				Spout.getLogger().log(Level.WARNING, "No resource found at {0} and has no fallback resource.", uri.toString());
				return null;
			}

			try {
				return tryCast(loadedResources.get(new URI(fallback)), uri.getScheme());
			} catch (URISyntaxException se) {
				throw new IllegalStateException("Fallback name for scheme " + uri.getScheme() + " is invalid.", e);
			}
		}

		return tryCast(loadedResources.get(uri), uri.getScheme());
	}

	@Override
	public <R> R getResource(String uri) {
		try {
			return getResource(new URI(uri));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Specified URI '" + uri + "' is invalid.", e);
		}
	}

	@Override
	public <R> List<R> getResources(URI uri) {
		ResourcePathResolver resolver = getPathResolver(uri);
		if (resolver == null) {
			throw new IllegalArgumentException("Could not resolve path '" + uri.toString() + "'");
		}

		String[] files = resolver.list(uri);
		List<R> resources = new ArrayList<R>();
		for (String file : files) {
			resources.add((R) getResource(uri.getScheme() + "://" + uri.getHost() + uri.getPath() + file));
		}
		return resources;
	}

	@Override
	public <R> List<R> getResources(String uri) {
		try {
			return getResources(new URI(uri));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Specified uri is invalid", e);
		}
	}

	@Override
	public List<ResourcePathResolver> getPathResolvers() {
		return Collections.unmodifiableList(pathResolvers);
	}

	@Override
	public void addPathResolver(ResourcePathResolver pathResolver) {
		pathResolvers.add(pathResolver);
	}

	@Override
	public void removePathResolver(ResourcePathResolver pathResolver) {
		pathResolvers.remove(pathResolver);
	}
}
