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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.spout.engine.filesystem.SharedFilesystem;

public class ZipfileResolver extends FilepathResolver {
	public ZipfileResolver() {
		super(SharedFilesystem.RESOURCE_FOLDER.getPath());
	}

	@Override
	public boolean existsInPath(String file, String path) {
		boolean has = false;
		ZipFile f = null;
		try {
			System.out.println(path);
			f = new ZipFile(path);
			ZipEntry entry = f.getEntry(file);
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
		ZipFile f = null;
		FileInputStream stream = null;
		try {
			f = new ZipFile(directory + path);
			ZipEntry entry = f.getEntry(file);
			InputStream s = f.getInputStream(entry);
			return s; //TODO close the jar.
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
		return stream;
	}
}
