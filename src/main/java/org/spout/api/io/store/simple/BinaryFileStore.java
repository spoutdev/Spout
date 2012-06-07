/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.io.store.simple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This implements a SimpleStore that is stored in memory. The save and load
 * methods can be used to write the map to a binary file.
 */
public class BinaryFileStore extends MemoryStore<Integer> implements SimpleStore<Integer> {

	private File file;
	private boolean dirty = false;

	public BinaryFileStore(File file) {
		super();
		this.file = file;
	}
	
	public BinaryFileStore() {
		this(null);
	}
	
	public synchronized void setFile(File file) {
		this.file = file;
	}
	
	public synchronized File getFile() {
		return file;
	}

	@Override
	public synchronized boolean clear() {
		dirty = true;
		return super.clear();
	}

	@Override
	public synchronized boolean save() {
		if (!dirty) {
			return true;
		}

		boolean saved = true;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			Iterator<Entry<String, Integer>> itr = super.getEntrySet().iterator();

			while (itr.hasNext()) {
				Entry<String, Integer> next = itr.next();
				out.writeInt(next.getValue());
				out.writeUTF(next.getKey());
			}
		} catch (IOException ioe) {
			saved = false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				saved = false;
			}
			if (saved) {
				dirty = false;
			}
		}
		return saved;
	}

	@Override
	public synchronized boolean load() {
		boolean loaded = true;
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

			boolean eof = false;
			while (!eof) {
				try {
					Integer id = in.readInt();
					String key = in.readUTF();
					set(key, id);
				} catch (EOFException eofe) {
					eof = true;
				}
			}
		} catch (IOException ioe) {
			loaded = false;
		} finally {
			try {
				if (in != null) {
					in.close();
				} 
			} catch (IOException ioe) {
				loaded = false;
			}
		}
		if (loaded) {
			dirty = false;
		}
		return loaded;
	}

	@Override
	public synchronized Integer remove(String key) {
		Integer value = super.remove(key);
		if (value != null) {
			dirty = true;
		}
		return value;
	}

	@Override
	public synchronized Integer set(String key, Integer value) {
		dirty = true;
		return super.set(key, value);
	}

}
