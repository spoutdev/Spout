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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

class ByteData extends AbstractData {

	private final AtomicInteger data = new AtomicInteger(0);

	public ByteData(int key) {
		super(key);
	}

	public ByteData(int key, byte value) {
		super(key);
		data.set(value);
	}

	@Override
	public void set(Serializable value) {
		throw new IllegalArgumentException("This is an byte value, use set(byte)");
	}

	public void set(byte value) {
		data.set(value);
	}

	@Override
	public Byte get() {
		return (byte) data.get();
	}

	@Override
	public byte[] compress() {
		return compressRaw((byte) data.get());
	}

	public static byte[] compressRaw(byte x) {
		byte[] compressed = new byte[1];
		compressed[0] = (byte) (x >> 0);
		return compressed;
	}

	@Override
	public void decompress(byte[] compressed) {
		set(decompressRaw(compressed));
	}

	public static byte decompressRaw(byte[] compressed) {
		byte x = 0;
		x |= (compressed[0] & 0xFF) << 0;
		return x;
	}

	@Override
	public int fixedLength() {
		return 1;
	}
}
