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

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spout.api.FileSystem;
import org.spout.api.Spout;
import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.resource.ResourceNotFoundException;
import org.spout.api.resource.ResourcePathResolver;

import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.path.FilePathResolver;
import org.spout.engine.filesystem.path.JarFilePathResolver;
import org.spout.engine.filesystem.path.ZipFilePathResolver;
import org.spout.engine.resources.loader.CommandBatchLoader;
import org.spout.engine.resources.loader.TextureLoader;

/**
 * The basic filesystem of Spout.
 */
public class SharedFileSystem implements FileSystem {
	private static File parentDir = new File(".");
	private ResourcePathResolver[] searchPaths;
	private final Map<String, Map<String, ResourceLoader<?>>> loaders = new HashMap<String, Map<String, ResourceLoader<?>>>();
	private final HashMap<URI, Resource> loadedResources = new HashMap<URI, Resource>();

	public synchronized static File getParentDirectory() {
		return parentDir;
	}

	public synchronized static void setParentDirectory(File parentDir) {
		SharedFileSystem.parentDir = parentDir;
	}

	public synchronized static File getPluginDirectory() {
		return new File(parentDir, "plugins");
	}
	
	public synchronized static File getResourceDirectory() {
		return new File(parentDir, "resources");
	}

	public synchronized static File getCacheDirectory() {
		return new File(parentDir, "cache");
	}

	public synchronized static File getConfigDirectory() {
		return new File(parentDir, "config");
	}

	public synchronized static File getUpdateDirectory() {
		return new File(parentDir, "update");
	}

	public synchronized static File getDataDirectory() {
		return new File(parentDir, "data");
	}

	public synchronized static File getWorldsDirectory() {
		return new File(parentDir, "worlds");
	}

	@Override
	public void init() {
		if (!getPluginDirectory().exists()) {
			getPluginDirectory().mkdirs();
		}
		if (!getResourceDirectory().exists()) {
			getResourceDirectory().mkdirs();
		}
		if (!getCacheDirectory().exists()) {
			getCacheDirectory().mkdirs();
		}
		if (!getConfigDirectory().exists()) {
			getConfigDirectory().mkdirs();
		}
		if (!getUpdateDirectory().exists()) {
			getUpdateDirectory().mkdirs();
		}
		if (!getDataDirectory().exists()) {
			getDataDirectory().mkdirs();
		}
		if (!getWorldsDirectory().exists()) {
			getWorldsDirectory().mkdirs();
		}

		registerLoader(new TextureLoader());
		registerLoader(new CommandBatchLoader());

		searchPaths = new ResourcePathResolver[]{new FilePathResolver("cache"), new ZipFilePathResolver(), new JarFilePathResolver()};
	}

	@Override
	public void postStartup() {
		loadFallbacks();
	}

	private void loadFallbacks() {
		for (Map<String, ResourceLoader<?>> protocolLoaders : loaders.values()) {
			for (ResourceLoader<?> loader : protocolLoaders.values()) {
				String fallback = loader.getFallbackResourceName();
				if (fallback != null) {
					loadResource(fallback);
				}
			}
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
		Spout.getEngine().getLogger().warning("Tried to load " + path + " it isn't found!  Using system fallback");
		String scheme = path.getScheme();
		if (!scheme.equals("file")) {
			throw new ResourceNotFoundException(path.toString());
		}
		return SharedFileSystem.class.getResourceAsStream("/fallbacks/" + path.getPath());
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

	@Override
	public void registerLoader(ResourceLoader<? extends Resource> loader) {
		String protocol = loader.getProtocol();
		if (loaders.containsKey(protocol)) {
			return;
		}
		for (String extension : loader.getExtensions()) {
			getLoaders(protocol).put(extension, loader);
		}
	}

	@Override
	public void loadResource(URI path) throws ResourceNotFoundException {
		String protocol = path.getScheme();
		if (!loaders.containsKey(protocol)) {
			throw new IllegalArgumentException("Unknown resource type: " + protocol);
		}

		String fileName = new File(path.getPath()).getName();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

		ResourceLoader<?> loader = getLoader(protocol, ext);
		if (loader == null) {
			throw new IllegalArgumentException("Unsupported file extension for protocol '" + protocol + "': " + ext);
		}
		Resource r = loader.getResource(path);
		loadedResources.put(path, r);
	}

	@Override
	public void loadResource(String path) {
		try {
			loadResource(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to load resource at '" + path + "', but path is not a URI.", e);
		} catch (ResourceNotFoundException e) {
			throw new IllegalArgumentException("Resource not found at '" + path + "': ", e);
		}
	}

	@Override
	public Resource getResource(URI path) {
		if (!loadedResources.containsKey(path)) {
			if (Spout.debugMode() && ((SpoutEngine)Spout.getEngine()).isSetupComplete()) {
				Spout.getLogger().warning("Late Precache of resource: " + path.toString());
			}
			try {
				loadResource(path);
			} catch (ResourceNotFoundException e) {
				String scheme = path.getScheme();
				String extension = getExtension(path);
				ResourceLoader<?> loader = getLoader(scheme, extension);
				if (loader == null) {
					throw new IllegalArgumentException("No loader found for " + scheme + " protocol with extension " + extension + "!");
				}

				String name = loader.getFallbackResourceName();
				if (name == null) {
					Spout.getLogger().info("Resource not found for path '" + path + "' and does not have fallback.");
					return null;
				}

				try {
					URI fallbackName = new URI(name);
					return loadedResources.get(fallbackName);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
					return null;
				}
			}
		}
		return loadedResources.get(path);
	}

	@Override
	public Resource getResource(String path) {
		try {
			return getResource(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to get resource at '" + path + "', but path is not a URI.");
		}
	}

	@Override
	public ResourcePathResolver getPathResolver(URI path) {
		for (ResourcePathResolver searchPath : searchPaths) {
			if (searchPath.existsInPath(path)) {
				return searchPath;
			}
		}
		return null;
	}

	@Override
	public ResourcePathResolver getPathResolver(String path) {
		try {
			return getPathResolver(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to get path resolver at '" + path + "', but path is not a URI.");
		}
	}

	@Override
	public void loadResources(URI uri) {
		ResourcePathResolver resolver = getPathResolver(uri);
		if (resolver == null) {
			throw new IllegalArgumentException("Could not resolve path '" + uri.toString() + "'");
		}

		String[] files = resolver.list(uri);
		for (String file : files) {
			loadResource(uri.getScheme() + "://" + uri.getHost() + uri.getPath() + file);
		}
	}

	@Override
	public void loadResources(String path) {
		try {
			loadResources(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to load resources from '" + path + "': ", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Resource> List<T> getResources(URI uri) {
		ResourcePathResolver resolver = getPathResolver(uri);
		if (resolver == null) {
			throw new IllegalArgumentException("Could not resolve path '" + uri.toString() + "'");
		}

		String[] files = resolver.list(uri);
		List<T> resources = new ArrayList<T>();
		for (String file : files) {
			resources.add((T) getResource(uri.getScheme() + "://" + uri.getHost() + uri.getPath() + file));
		}
		return resources;
	}

	@Override
	public <T extends Resource> List<T> getResources(String path) {
		try {
			return getResources(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to get resources from '" + path + "': ", e);
		}
	}

	private ResourceLoader<?> getLoader(String protocol, String extension) {
		return getLoaders(protocol).get(extension);
	}

	private Map<String, ResourceLoader<?>> getLoaders(String protocol) {
		Map<String, ResourceLoader<?>> protocolLoaders = loaders.get(protocol.toLowerCase());
		if (protocolLoaders == null) {
			protocolLoaders = new HashMap<String, ResourceLoader<?>>();
			loaders.put(protocol.toLowerCase(), protocolLoaders);
		}
		return protocolLoaders;
	}

	private String getExtension(URI path) {
		String fileName = new File(path.getPath()).getName();
		return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
	}
}
