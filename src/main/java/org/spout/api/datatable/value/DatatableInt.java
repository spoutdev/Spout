/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.datatable.value;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class DatatableInt extends DatatableObject {
	
	private final AtomicInteger data = new AtomicInteger(0);

	public DatatableInt(int key) {
		super(key);
	}

	public DatatableInt(int key, int value) {
		super(key);
		data.set(value);
	}

	@Override
	public void set(Object value) {
		throw new IllegalArgumentException("This is an int value, use set(int)");
	}
	
	public void set(int value) {
		data.set(value);
	}

	@Override
	public Serializable get() {
		return data.get();
	}

	@Override
	public int asInt() {
		return data.get();
	}

	@Override
	public float asFloat() {
		return data.get();
	}

	@Override
	public boolean asBool() {
		return data.get() != 0;
	}

	@Override
	public byte[] compress() {
		return compressRaw(asInt());
	}
	
	public static byte[] compressRaw(int x) {
		byte[] compressed = new byte[4];
		compressed[0] = (byte)(x >> 24);
		compressed[1] = (byte)(x >> 16);
		compressed[2] = (byte)(x >> 8);
		compressed[3] = (byte)(x >> 0);
		return compressed;
	}

	@Override
	public void decompress(byte[] compressed) {
		set(decompressRaw(compressed));
	}
	
	public static int decompressRaw(byte[] compressed) {
		int x = 0;
		x |= (compressed[0] & 0xFF) << 24;
		x |= (compressed[1] & 0xFF) << 16;
		x |= (compressed[2] & 0xFF) << 8;
		x |= (compressed[3] & 0xFF) << 0;
		return x;
	}
	
	@Override
	public byte getObjectTypeId() {
		return 3;
	}
	
	@Override
	public DatatableObject newInstance(int key) {
		return new DatatableInt(key);
	}
	
	@Override
	public int fixedLength() {
		return 4;
	}

}
