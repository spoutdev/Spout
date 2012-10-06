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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

class LongData extends AbstractData {

	private final AtomicLong data = new AtomicLong(0);

	public LongData(int key) {
		super(key);
	}

	public LongData(int key, long value) {
		super(key);
		data.set(value);
	}

	@Override
	public void set(Object value) {
		throw new IllegalArgumentException("This is an long value, use set(long)");
	}

	public void set(long value) {
		data.set(value);
	}

	@Override
	public Serializable get() {
		return data.get();
	}

	@Override
	public byte[] compress() {
		return compressRaw(data.get());
	}

	public static byte[] compressRaw(long x) {
		byte[] compressed = new byte[8];
		compressed[0] = (byte) (x >> 56);
		compressed[1] = (byte) (x >> 48);
		compressed[2] = (byte) (x >> 40);
		compressed[3] = (byte) (x >> 32);
		compressed[4] = (byte) (x >> 24);
		compressed[5] = (byte) (x >> 16);
		compressed[6] = (byte) (x >> 8);
		compressed[7] = (byte) (x >> 0);
		return compressed;
	}

	@Override
	public void decompress(byte[] compressed) {
		set(decompressRaw(compressed));
	}

	public static long decompressRaw(byte[] compressed) {
		long x = 0;
		x |= (compressed[0] & 0xFFL) << 56;
		x |= (compressed[1] & 0xFFL) << 48;
		x |= (compressed[2] & 0xFFL) << 40;
		x |= (compressed[3] & 0xFFL) << 32;
		x |= (compressed[4] & 0xFFL) << 24;
		x |= (compressed[5] & 0xFFL) << 16;
		x |= (compressed[6] & 0xFFL) << 8;
		x |= (compressed[7] & 0xFFL) << 0;
		return x;
	}

	@Override
	public byte getObjectTypeId() {
		return 5;
	}

	@Override
	public AbstractData newInstance(int key) {
		return new LongData(key);
	}

	@Override
	public int fixedLength() {
		return 8;
	}
}
