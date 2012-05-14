/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import org.spout.api.Spout;
import org.spout.api.plugin.Platform;
import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.resource.ResourcePathResolver;

import org.spout.engine.filesystem.path.FilepathResolver;
import org.spout.engine.filesystem.path.JarfileResolver;
import org.spout.engine.filesystem.path.ZipfileResolver;
import org.spout.engine.resources.ShaderLoader;
import org.spout.engine.resources.TextureLoader;

public class FileSystem {
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
	static ResourcePathResolver[] searchpaths;
	static final HashMap<String, ResourceLoader<? extends Resource>> LOADERS = new HashMap<String, ResourceLoader<? extends Resource>>();
	static final HashMap<URI, Resource> LOADED_RESOURCES = new HashMap<URI, Resource>();

	public static void init() {
		if (Spout.getPlatform() == Platform.CLIENT) {
			if (!RESOURCE_FOLDER.exists()) {
				RESOURCE_FOLDER.mkdirs();
			}
			if (!CACHE_FOLDER.exists()) {
				CACHE_FOLDER.mkdirs();
			}
		} else if (Spout.getPlatform() == Platform.SERVER) {
			if (!CONFIG_DIRECTORY.exists()) {
				CONFIG_DIRECTORY.mkdirs();
			}
			if (!UPDATE_DIRECTORY.exists()) {
				UPDATE_DIRECTORY.mkdirs();
			}
		}

		if (!PLUGIN_DIRECTORY.exists()) {
			PLUGIN_DIRECTORY.mkdirs();
		}
		if (!DATA_DIRECTORY.exists()) {
			DATA_DIRECTORY.mkdirs();
		}
		if (!WORLDS_DIRECTORY.exists()) {
			WORLDS_DIRECTORY.mkdirs();
		}

		searchpaths = new ResourcePathResolver[]{new FilepathResolver("cache"),
				//new ZipfileResolver(),
				new JarfileResolver()};

		registerLoader("texture", new TextureLoader());
		registerLoader("shader", new ShaderLoader());
	}

	public static InputStream getResourceStream(URI path) {
		for (int i = 0; i < searchpaths.length; i++) {
			if (searchpaths[i].existsInPath(path)) {
				return searchpaths[i].getStream(path);
			}
		}
		Spout.getEngine().getLogger().warning("Tried to load " + path + " it isn't found!  Using system fallback");
		//Open our jar and grab the fallback file
		String name = LOADERS.get(path.getScheme()).getFallbackResourceName();	
		InputStream stream = FileSystem.class.getResourceAsStream("/fallbacks/" + name);
		return stream;
	}
	
	public static InputStream getResourceStream(String path){
		try {
			return getResourceStream(new URI(path));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Tried to get a Resource Stream URI, but" + path +" Isn't a URI");
		}
	}

	public static void registerLoader(String protocol, ResourceLoader<? extends Resource> loader) {
		if (LOADERS.containsKey(protocol)) {
			return;
		}
		LOADERS.put(protocol, loader);
	}

	public static void loadResource(URI path) {
		String protocol = path.getScheme();
		if (!LOADERS.containsKey(protocol)) {
			throw new IllegalArgumentException("Unknown resource type: " + protocol);
		}
		Resource r = LOADERS.get(protocol).getResource(path);
		LOADED_RESOURCES.put(path, r);
	}

	public static void loadResource(String path) {
		try {
			URI upath = new URI(path);
			loadResource(upath);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Resource getResource(URI path) {
		if (!LOADED_RESOURCES.containsKey(path)) {
			Spout.getLogger().warning("Late Precache of resource: " + path.toString());
			loadResource(path);
		}
		return LOADED_RESOURCES.get(path);
	}

	public static Resource getResource(String path) {
		try {
			URI upath = new URI(path);
			return getResource(upath);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
