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
package org.spout.engine.filesystem.path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;
import org.spout.engine.filesystem.FileSystem;

public class JarfileResolver extends FilepathResolver {
	public JarfileResolver() {
		super(FileSystem.PLUGIN_DIRECTORY.getPath());
		// TODO Auto-generated constructor stub
	}

	File pluginsFolder = FileSystem.PLUGIN_DIRECTORY;

	
	private JarFile getJar(String path) throws IOException{
		String pluginName = path.substring(path.lastIndexOf(File.separatorChar) + 1);
		Plugin p = Spout.getEngine().getPluginManager().getPlugin(pluginName);
		if(p == null) return null;
		return new JarFile(p.getFile());
	}
	
	@Override
	public boolean existsInPath(String file, String path) {
		boolean has = false;
		JarFile f = null;
		try {
			
			f = getJar(path);
			if(f == null) {
				Spout.log("Tried to get file " + file + " from plugin " + path + " but it isn't loaded!");
				return false; //If the plugin doesn't exist, we don't have the file
			}
			
			JarEntry entry = f.getJarEntry(file);
			has = entry != null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return has;
	}

	@Override
	public InputStream getStream(String file, String path) {
		JarFile f = null;
		try {
			f = getJar(path);
			JarEntry entry = f.getJarEntry(file);
			InputStream s = f.getInputStream(entry);
			return s; //TODO close the jar.
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
