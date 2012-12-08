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
package org.spout.engine.filesystem.path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.spout.engine.filesystem.SharedFileSystem;

public class ZipFilePathResolver extends FilePathResolver {
	public ZipFilePathResolver() {
		super(SharedFileSystem.RESOURCE_FOLDER.getPath());
	}

	public ZipFile getZip(String host) throws IOException {
		return new ZipFile(directory + File.separatorChar + host);
	}

	@Override
	public boolean existsInPath(String host, String path) {
		ZipFile f = null;
		boolean b = false;
		try {
			f = getZip(host);
			b = f.getEntry(path.substring(1)) != null;
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
	public InputStream getStream(String host, String path) {
		try {
			ZipFile f = getZip(host);
			ZipEntry entry = f.getEntry(path);
			if (entry == null) {
				return null;
			}
			return f.getInputStream(entry);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] list(String host, String path) {
		ZipFile zip = null;
		try {
			zip = getZip(host);
			// iterate through the zip's entries
			Enumeration<? extends ZipEntry> entries = zip.entries();
			List<String> list = new ArrayList<String>();
			path = path.substring(1);
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();
				// we can't load directories, no point in returning them
				// verify the entry is within the given path
				if (!entry.isDirectory() && name.startsWith(path)) {
					list.add(name.replaceFirst(path, ""));
				}
			}
			return list.toArray(new String[list.size()]);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
