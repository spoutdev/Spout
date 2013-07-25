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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.spout.api.resource.ResourcePathResolver;

public class FilePathResolver implements ResourcePathResolver {
	protected final String directory;

	public FilePathResolver(String path) {
		this.directory = path;
	}

	public File getFile(String host, String path) {
		return new File(directory + File.separatorChar + host, path);
	}

	@Override
	public boolean existsInPath(String host, String path) {
		return getFile(host, path).exists();
	}

	@Override
	public boolean existsInPath(URI uri) {
		return this.existsInPath(uri.getHost(), uri.getPath());
	}

	@Override
	public InputStream getStream(String host, String path) {
		try {
			return new FileInputStream(getFile(host, path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public InputStream getStream(URI uri) {
		return this.getStream(uri.getHost(), uri.getPath());
	}

	@Override
	public String[] list(String host, String path) {
		List<String> list = new ArrayList<String>();
		for (File file : getFile(host, path).listFiles()) {
			// we can't load directories, no point in returning them
			if (file.isFile()) {
				list.add(file.getName());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public String[] list(URI uri) {
		return list(uri.getHost(), uri.getPath());
	}
}

