/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.datatable;

import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spout.api.util.StringToUniqueIntegerMap;

import org.spout.api.util.SyncedStringMap;
import org.spout.api.util.VarInt;

/**
 * Keys are actually stored across two maps.
 * One map, {@link SyncedStringMap}, stores the string -> int key conversion.
 * The second map, {@link TSynchronizedIntObjectMap}, stores the actual data.
 * 
 */
class GenericDatatableMap {
	private static final SyncedStringMap ROOT_STRING_MAP = SyncedStringMap.create(GenericDatatableMap.class.getName());
	private final StringToUniqueIntegerMap stringmap;
	private final Object mapMutex = new Object();
	private final TSynchronizedIntObjectMap<Serializable> map = new TSynchronizedIntObjectMap<Serializable>(new TIntObjectHashMap<Serializable>(), mapMutex);

	public GenericDatatableMap(StringToUniqueIntegerMap stringmap) {
		this.stringmap = stringmap;
	}

	public GenericDatatableMap() {
		this.stringmap = ROOT_STRING_MAP;
	}

	
	public void set(Serializable value) {
		set(value.hashCode(), value);
	}

	public void set(String key, Serializable value) {
		getAndSet(key, value);
	}

	public void set(int key, Serializable value) {
		if (stringmap.getString(key) == null) {
			throw new IllegalArgumentException("Key " + key + " does not have a matching string");
	}
	
		setRaw(key, value);
	}

	public Serializable setIfAbsent(Serializable value) {
		return setIfAbsentRaw(value.hashCode(), value);
	}
	
	
	public Serializable setIfAbsent(String key, Serializable value) {
		return setIfAbsentRaw(stringmap.register(key), value);
	}
	
	
	public Serializable setIfAbsent(int key, Serializable value) {
		if (stringmap.getString(key) == null) {
			throw new IllegalArgumentException("Key " + key + " does not have a matching string");
		}
		return setIfAbsentRaw(key, value);
	}
	
	public Serializable setIfAbsentRaw(int key, Serializable value) {
		return map.putIfAbsent(key, value);
	}

	public Serializable getAndSet(String key, Serializable value) {
		return setRaw(stringmap.register(key), value);
	}
	
	public Serializable getAndSet(int key, Serializable value) {
		if (stringmap.getString(key) == null) {
			throw new IllegalArgumentException("Key " + key + " does not have a matching string");
		}

		return setRaw(key, value);
	}

	private Serializable setRaw(int key, Serializable value) {
		return map.put(key, value);
	}
	
	public Serializable get(String key) {
		int intKey = getIntKey(key);
		Serializable value = map.get(intKey);
		return value;
	}

	public byte[] serialize() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(map);
			return out.toByteArray();
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to compress GenericDatatableMap");
		}
	}

	public void deserialize(byte[] compressedData, boolean wipe) throws IOException{
		if (wipe) {
			map.clear();
		}
		InputStream in = new ByteArrayInputStream(compressedData);
		ObjectInputStream ois = new ObjectInputStream(in);
		try {
			map.putAll((Map<? extends Integer, ? extends Serializable>) ois.readObject());
		} catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Unable to decompress GenericDatatableMap");
		}
	}

	public void decompress(byte[] compressedData) throws IOException{
		deserialize(compressedData, true);
	}

	public void output(OutputStream out) throws IOException {
		VarInt.writeInt(out, -1);
		byte[] compressed = serialize();
		VarInt.writeInt(out, compressed.length);
		out.write(compressed);
	}

	public void input(InputStream in) throws IOException {
		input(in, true);
	}

	public void input(InputStream in, boolean wipe) throws IOException {
		int id = VarInt.readInt(in);
		if (id != -1) {
			throw new IOException("Unable to parse GenericDatatableMap");
		}
		int length = VarInt.readInt(in);
		byte[] compressed = new byte[length];
		while (length > 0) {
			length -= in.read(compressed, compressed.length - length, length);
		}
		deserialize(compressed, wipe);
	}

	public boolean contains(String key) {
		int intKey = getIntKey(key);
		return map.containsKey(intKey);
	}

	public boolean contains(int key) {
		return map.containsKey(key);
	}

	public int getIntKey(String key) {
		return stringmap.register(key);
	}

	public String getStringKey(int key) {
		return stringmap.getString(key);
	}

	public Serializable get(int key) {
		Serializable o = map.get(key);
		return o;
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Serializable remove(String key) {
		return remove(getIntKey(key));
	}

	public Serializable remove(int key) {
		Serializable o = map.remove(key);
		return o;
	}

	public void clear() {
		map.clear();
	}

	public Set<String> keySet() {
		Collection<String> keys = stringmap.getKeys();
		HashSet<String> keyset = new HashSet<String>();
		// Not all of the string map values are necessarily registered to a value
		for (String key : keys) {
			if (contains(key)) {
				keyset.add(key);
			}
		}
		return keyset;
	}

	public Collection<Serializable> values() {
		return map.valueCollection();
	}

	public static GenericDatatableMap readMap(InputStream in) throws IOException {
		GenericDatatableMap map = new GenericDatatableMap();
		map.input(in);
		return map;
	}
}
