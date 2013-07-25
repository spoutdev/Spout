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
package org.spout.engine.filesystem.path;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;
import org.spout.api.resource.ResourcePathResolver;

public class JarFilePathResolver implements ResourcePathResolver {
	public JarFile getJar(String host) throws IOException {
		Plugin p = Spout.getPluginManager().getPlugin(host);
		if (p == null) {
			return null;
		}
		return new JarFile(p.getFile());
	}

	@Override
	public boolean existsInPath(String host, String path) {
		JarFile f = null;
		boolean b = false;
		try {
			f = getJar(host);
			if (f == null) {
				Spout.info("Tried to get file " + path + " from plugin " + host + " but it isn't loaded!");
				return false; //If the plugin doesn't exist, we don't have the file
			}
			b = f.getJarEntry(path.substring(1)) != null;
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
		return b;
	}

	@Override
	public boolean existsInPath(URI uri) {
		return existsInPath(uri.getHost(), uri.getPath());
	}

	@Override
	public InputStream getStream(String host, String path) {
		try {
			JarFile f = getJar(host);
			if (f == null) {
				throw new IllegalArgumentException("Specified JarFile does not exist.");
			}
			JarEntry entry = f.getJarEntry(path.substring(1));
			if (entry == null) {
				throw new IllegalArgumentException("Specified JarEntry does not exist.");
			}
			return f.getInputStream(entry);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public InputStream getStream(URI uri) {
		return getStream(uri.getHost(), uri.getPath());
	}

	@Override
	public String[] list(String host, String path) {
		JarFile jar = null;
		try {
			jar = getJar(host);
			if (jar == null) {
				throw new IllegalArgumentException("Specified JarFile does not exist.");
			}
			// iterate through the JarEntries
			Enumeration<JarEntry> entries = jar.entries();
			List<String> list = new ArrayList<String>();
			path = path.substring(1);
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				// we can't load directories, no point in returning them
				// verify that the entry is in the given path
				if (!entry.isDirectory() && name.startsWith(path)) {
					list.add(name.replaceFirst(path, ""));
				}
			}
			return list.toArray(new String[list.size()]);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public String[] list(URI uri) {
		return list(uri.getHost(), uri.getPath());
	}
}
