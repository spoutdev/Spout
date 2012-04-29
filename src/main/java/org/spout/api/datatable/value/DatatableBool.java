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
import java.util.concurrent.atomic.AtomicBoolean;

public class DatatableBool extends DatatableObject {
	private static final byte[] one = new byte[] {(byte)1};
	private static final byte[] zero = new byte[] {(byte)0};
	
	private AtomicBoolean data = new AtomicBoolean(false);

	public DatatableBool(int key) {
		super(key);
	}

	public DatatableBool(int key, boolean value) {
		super(key);
		data.set(value);
	}

	@Override
	public void set(Object value) {
		throw new IllegalArgumentException("This is an boolean value, use set(string,bool)");
	}

	public void set(boolean value) {
		data.set(value);
	}

	@Override
	public Serializable get() {
		return data.get();
	}

	@Override
	public int asInt() {
		return data.get() ? 1 : 0;
	}

	@Override
	public float asFloat() {
		throw new NumberFormatException("this value cannot be expressed as an float");
	}

	@Override
	public boolean asBool() {
		return data.get();
	}

	@Override
	public byte[] compress() {
		return (asBool()) ? one : zero;
	}

	@Override
	public void decompress(byte[] compressed) {
		if (compressed.length != 1) {
			throw new IllegalArgumentException("DatatableBools should be represented by a byte array of length 1");
		}
		set(compressed[0] != 0);
	}
	
	@Override
	public byte getObjectTypeId() {
		return 1;
	}
	
	@Override
	public DatatableObject newInstance(int key) {
		return new DatatableBool(key);
	}

}
