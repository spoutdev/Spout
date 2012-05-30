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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import org.spout.api.resource.ResourcePathResolver;

public class FilepathResolver implements ResourcePathResolver {
	protected final String directory;

	public FilepathResolver(String path) {
		this.directory = path;
	}

	@Override
	public boolean existsInPath(String file, String path) {
		File f = new File(path + File.separator + file);
		return f.exists();
	}

	@Override
	public boolean existsInPath(URI path) {
		return this.existsInPath(path.getPath(), directory + File.separator + (path.getHost() == null ? "/" : path.getHost()));
	}

	@Override
	public InputStream getStream(String file, String path) {
		try {
			return new FileInputStream(new File(path + File.separator + file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public InputStream getStream(URI path) {
		return this.getStream(path.getPath(), directory + File.separatorChar + (path.getHost() == null ? "/" : path.getHost()));
	}
}
