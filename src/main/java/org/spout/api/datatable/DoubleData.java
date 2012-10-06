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

import com.google.common.util.concurrent.AtomicDouble;

class DoubleData extends AbstractData {
	private AtomicDouble data = new AtomicDouble(0);

	public DoubleData(int key) {
		super(key);
	}

	public DoubleData(int key, double value) {
		super(key);
		data.set(value);
	}

	@Override
	public void set(Object value) {
		throw new IllegalArgumentException("This is an double value, use set(double)");
	}

	public void set(double value) {
		data.set(value);
	}

	@Override
	public Serializable get() {
		return data.get();
	}

	@Override
	public byte[] compress() {
		return LongData.compressRaw(Double.doubleToRawLongBits(data.get()));
	}

	@Override
	public void decompress(byte[] compressed) {
		set(Double.longBitsToDouble(LongData.decompressRaw(compressed)));
	}

	@Override
	public byte getObjectTypeId() {
		return 7;
	}

	@Override
	public AbstractData newInstance(int key) {
		return new DoubleData(key);
	}

	@Override
	public int fixedLength() {
		return 8;
	}

}
