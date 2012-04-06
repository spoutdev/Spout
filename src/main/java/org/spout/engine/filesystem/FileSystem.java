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

import org.spout.api.plugin.Platform;
import org.spout.api.resource.ResourcePathResolver;

import org.spout.api.Spout;
import org.spout.engine.filesystem.path.FilepathResolver;
import org.spout.engine.filesystem.path.JarfileResolver;
import org.spout.engine.filesystem.path.ZipfileResolver;

public class FileSystem {

	/**
	 * Plugins live in this folder (SERVER and CLIENT)
	 */
	public static final File pluginDirectory = new File("plugins"); 
	
	public static final File resourceFolder = new File("resources");
	public static final File cacheFolder = new File("cache");	
	public static final File configDirectory = new File("config");
	public static final File updateDirectory = new File("update");
	public static final File dataDirectory = new File("data");	
	public static final File worldsDirectory = new File("worlds");
	
	
	
	static ResourcePathResolver[] searchpaths;
	
	public static void init()
	{
		if(Spout.getPlatform() == Platform.CLIENT && !resourceFolder.exists()) resourceFolder.mkdirs();
		if(Spout.getPlatform() == Platform.CLIENT && !cacheFolder.exists()) cacheFolder.mkdirs();
		if(!pluginDirectory.exists()) pluginDirectory.mkdirs();
		if(Spout.getPlatform() == Platform.SERVER && !configDirectory.exists()) configDirectory.mkdirs();
		if(Spout.getPlatform() == Platform.SERVER && !updateDirectory.exists()) updateDirectory.mkdirs();
		if(!dataDirectory.exists()) dataDirectory.mkdirs();
		if(!worldsDirectory.exists()) worldsDirectory.mkdirs();
		
		
		searchpaths = new ResourcePathResolver[] { new FilepathResolver("cache"), 
													new ZipfileResolver(),
													new JarfileResolver() };
		
	}
	

	
	
}
