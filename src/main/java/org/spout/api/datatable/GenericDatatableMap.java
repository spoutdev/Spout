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
package org.spout.api.datatable;

import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.spout.api.datatable.procedures.GDMCompressProcedure;
import org.spout.api.datatable.value.DatatableNil;
import org.spout.api.datatable.value.DatatableObject;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.StringMap;
import org.spout.api.util.VarInt;

public class GenericDatatableMap implements DatatableMap {
	private static final StringMap ROOT_STRING_MAP = new StringMap(null, new MemoryStore<Integer>(), 0, Short.MAX_VALUE, GenericDatatableMap.class.getName());
	private final StringMap stringmap;
	TSynchronizedIntObjectMap<DatatableObject> map = new TSynchronizedIntObjectMap<DatatableObject>(new TIntObjectHashMap<DatatableObject>());

	private final DatatableNil niltype = new DatatableNil();

	public static StringMap getStringMap() {
		return ROOT_STRING_MAP;
	}

	public GenericDatatableMap() {
		stringmap = ROOT_STRING_MAP;
	}

	@Override
	public void set(DatatableObject value) {
		set(value.hashCode(), value);
	}

	@Override
	public void set(String key, DatatableObject value) {
		setRaw(stringmap.register(key), value);
	}

	@Override
	public void set(int key, DatatableObject value) {
		if (stringmap.getString(key) == null) {
			throw new IllegalArgumentException("Key " + key + " does not have a matching string");
		}

		setRaw(key, value);
	}

	private void setRaw(int key, DatatableObject value) {
		value.setKey(key);
		map.put(key, value);
	}

	@Override
	public DatatableObject get(String key) {
		int intKey = getIntKey(key);
		DatatableObject value = map.get(intKey);
		if (value == null) {
			return niltype;
		}

		return value;
	}

	@Override
	public byte[] compress() {
		final ByteArrayOutputStream stringOutput = new ByteArrayOutputStream();
		final ByteArrayOutputStream objectOutput = new ByteArrayOutputStream();

		GDMCompressProcedure procedure = new GDMCompressProcedure(this, stringOutput, objectOutput);

		boolean success = map.forEachEntry(procedure);

		if (!success) {
			throw new IllegalStateException("Unable to compress GenericDatatableMap");
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			VarInt.writeInt(out, procedure.strings);
			VarInt.writeInt(out, procedure.objects);
			out.write(stringOutput.toByteArray());
			out.write(objectOutput.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return out.toByteArray();
	}

	public void decompress(byte[] compressedData, boolean wipe) {
		if (wipe) {
			map.clear();
		}
		InputStream in = new ByteArrayInputStream(compressedData);
		try {
			TIntIntHashMap keyReplacement = new TIntIntHashMap();
			int strings = VarInt.readInt(in);
			int objects = VarInt.readInt(in);
			for (int i = 0; i < strings; i++) {
				int key = VarInt.readInt(in);
				String string = VarInt.readString(in);
				int newKey = getIntKey(string);
				keyReplacement.put(key, newKey);
			}
			for (int i = 0; i < objects; i++) {
				DatatableObject obj = DatatableObject.input(in);
				int key = obj.hashCode() + 0;
				if (!keyReplacement.contains(key)) {
					throw new IOException("Unknown key when decompressing GenericDatatableMap");
				}

				int newKey = keyReplacement.get(key);
				obj.setKey(newKey);
				setRaw(newKey, obj);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void decompress(byte[] compressedData) {
		decompress(compressedData, true);
	}

	public void output(OutputStream out) throws IOException {
		VarInt.writeInt(out, -1);
		byte[] compressed = compress();
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
		decompress(compressed, wipe);
	}

	public static DatatableMap readMap(InputStream in) throws IOException {
		GenericDatatableMap map = new GenericDatatableMap();
		map.input(in);
		return map;
	}

	@Override
	public boolean contains(String key) {
		int intKey = getIntKey(key);
		return map.containsKey(intKey);
	}

	@Override
	public boolean contains(int key) {
		return map.containsKey(key);
	}

	@Override
	public int getIntKey(String key) {
		return stringmap.register(key);
	}

	@Override
	public String getStringKey(int key) {
		return stringmap.getString(key);
	}

	@Override
	public DatatableObject get(int key) {
		DatatableObject o = map.get(key);
		return o != null ? o : niltype;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public DatatableObject remove(String key) {
		return remove(getIntKey(key));
	}

	@Override
	public DatatableObject remove(int key) {
		DatatableObject o = map.remove(key);
		return o != null ? o : niltype;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		Collection<String> keys = stringmap.getKeys();
		HashSet<String> keyset = new HashSet<String>();
		//Not all of the string map values are necessarily registered to a value
		for (String key : keys) {
			if (contains(key)) {
				keyset.add(key);
			}
		}
		return keyset;
	}

	@Override
	public Collection<DatatableObject> values() {
		return map.valueCollection();
	}

}
