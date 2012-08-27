/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.filesystem;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.FileSystem;
import org.spout.api.Spout;
import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.resource.ResourceNotFoundException;
import org.spout.api.resource.ResourcePathResolver;
import org.spout.engine.filesystem.path.FilepathResolver;
import org.spout.engine.filesystem.path.JarfileResolver;

/**
 * The basic filesystem of Spout.
 */
public class SharedFileSystem implements FileSystem {
	/**
	 * Plugins live in this folder (SERVER and CLIENT)
	 */
	public static final File PLUGIN_DIRECTORY = new File("plugins");
	public static final File RESOURCE_FOLDER = new File("resources");
	public static final File CACHE_FOLDER = new File("cache");
	public static final File CONFIG_DIRECTORY = new File("config");
	public static final File UPDATE_DIRECTORY = new File("update");
	public static final File DATA_DIRECTORY = new File("data");
	public static final File WORLDS_DIRECTORY = new File("worlds");

	ResourcePathResolver[] searchpaths;

	final Map<String, Map<String, ResourceLoader<?>>> loaders = new HashMap<String, Map<String, ResourceLoader<?>>>();
	final HashMap<URI, Resource> loadedResouces = new HashMap<URI, Resource>();

	public void init() {
		if (!PLUGIN_DIRECTORY.exists()) {
			PLUGIN_DIRECTORY.mkdirs();
		}
		if (!DATA_DIRECTORY.exists()) {
			DATA_DIRECTORY.mkdirs();
		}
		if (!WORLDS_DIRECTORY.exists()) {
			WORLDS_DIRECTORY.mkdirs();
		}

		searchpaths = new ResourcePathResolver[] { new FilepathResolver("cache"),
				// new ZipfileResolver(),
				new JarfileResolver() };
	}

	public void postStartup() {
		loadFallbacks();
	}

	private void loadFallbacks() {
		for (Map<String, ResourceLoader<?>> protocolLoaders : loaders.values()) {
			for (ResourceLoader<?> loader : protocolLoaders.values()) {
				loadResource(loader.getFallbackResourceName());
			}
		}
	}

	public InputStream getResourceStream(URI path) throws ResourceNotFoundException {

		for (int i = 0; i < searchpaths.length; i++) {
			if (searchpaths[i].existsInPath(path)) {
				return searchpaths[i].getStream(path);
			}
		}
		Spout.getEngine().getLogger().warning("Tried to load " + path + " it isn't found!  Using system fallback");

		// Open our jar and grab the fallback 'file' scheme
		String scheme = path.getScheme();
		if (scheme.equals("file")) {
			return SharedFileSystem.class.getResourceAsStream("/fallbacks/" + path.getPath());
		}

		// Still can't find it? Throw a ResourceNotFound exception and give out
		// fallbacks
		throw new ResourceNotFoundException(path.toString());

		/*
		 * String name = LOADERS.get(scheme).getFallbackResourceName();
		 * InputStream stream =
		 * FileSystem.class.getResourceAsStream("/fallbacks/" + name); return
		 * stream;
		 */
	}

	public InputStream getResourceStream(String path) {
		try {
			return getResourceStream(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to get a Resource Stream URI, but" + path + " Isn't a URI");
		}
	}

	public void registerLoader(ResourceLoader<? extends Resource> loader) {
		String protocol = loader.getProtocol();
		if (loaders.containsKey(protocol)) {
			return;
		}
		for (String extension : loader.getExtensions()) {
			getLoaders(protocol).put(extension, loader);
		}
	}

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
		loadedResouces.put(path, r);
	}

	public void loadResource(String path) {
		try {
			URI upath = new URI(path);
			loadResource(upath);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public Resource getResource(URI path) {
		if (!loadedResouces.containsKey(path)) {
			if (Spout.debugMode())
				Spout.getLogger().warning("Late Precache of resource: " + path.toString());
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
				try {
					URI fallbackName = new URI(name);
					return loadedResouces.get(fallbackName);
				} catch (URISyntaxException e1) {

					e1.printStackTrace();
					return null;
				}

			}
		}
		return loadedResouces.get(path);
	}

	public Resource getResource(String path) {
		try {
			URI upath = new URI(path);
			return getResource(upath);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
