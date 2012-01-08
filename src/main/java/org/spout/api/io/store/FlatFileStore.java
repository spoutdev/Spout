/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.io.store;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.spout.api.io.FileUtil;

/**
 * This implements a SimpleStore that is stored in memory. The save and load
 * methods can be used to write the map to a File.
 */

public class FlatFileStore<T> extends MemoryStore<T> implements SimpleStore<T> {

	private final File file;
	private boolean dirty = false;
	private final Class<?> clazz; // preserve class, so parser knows what to do

	public FlatFileStore(File file, Class<?> clazz) {
		super();
		this.clazz = clazz;
		this.file = file;
		if (file != null) {
			if (!file.exists()) {
				if (!FileUtil.createFile(file)) {
					return;
				}
			}
		}
	}

	@Override
	public synchronized boolean clear() {
		dirty = true;
		return super.clear();
	}

	@Override
	public synchronized boolean save() {
		if (dirty) {
			Collection<String> strings = getStrings();
			boolean saved = FileUtil.stringToFile(strings, file);
			if (saved) {
				dirty = false;
			}
			return saved;
		} else {
			return true;
		}
	}

	@Override
	public synchronized boolean load() {
		Collection<String> strings = FileUtil.fileToString(file);
		if (strings == null) {
			return false;
		}
		boolean loaded = processStrings(strings);
		if (loaded) {
			dirty = false;
		}
		return loaded;
	}

	@Override
	public synchronized T remove(String key) {
		T value = super.remove(key);
		if (value != null) {
			dirty = true;
		}
		return value;
	}

	@Override
	public synchronized T set(String key, T value) {
		dirty = true;
		return super.set(key, value);
	}

	private synchronized Collection<String> getStrings() {
		Iterator<Entry<String, T>> itr = super.getEntrySet().iterator();
		ArrayList<String> strings = new ArrayList<String>(super.getSize());
		while (itr.hasNext()) {
			Entry<String, T> entry = itr.next();
			String encodedKey = encode(entry.getKey());
			T value = entry.getValue();
			strings.add(value + ":" + encodedKey);
		}
		return strings;
	}

	private boolean processStrings(Collection<String> strings) {
		super.clear();
		for (String string : strings) {
			String[] split = string.trim().split(":");
			if (split.length != 2) {
				return false;
			}
			T value;
			try {
				value = parse(split[0]);
			} catch (NumberFormatException nfe) {
				return false;
			}
			String key = decode(split[1]);
			set(key, value);
		}
		return true;
	}

	private static String encode(String key) {
		String encoded = key;
		encoded = encoded.replace("\\", "\\\\");
		encoded = encoded.replace("\n", "\\n");
		encoded = encoded.replace(":", "\\:");
		return encoded;
	}

	private static String decode(String encoded) {
		String key = encoded;
		key = key.replace("\\:", ":");
		key = key.replace("\\n", "\n");
		key = key.replace("\\\\", "\\");
		return encoded;
	}

	@SuppressWarnings("unchecked")
	private T parse(String string) {
		if (clazz.equals(Integer.class)) {
			return (T) (Object) Integer.parseInt(string);
		} else if (clazz.equals(String.class)) {
			return (T) string;
		} else {
			throw new IllegalArgumentException("Unable to parse clazzes of type " + clazz.getName());
		}
	}

}
