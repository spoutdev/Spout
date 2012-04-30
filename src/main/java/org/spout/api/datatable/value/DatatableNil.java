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

public class DatatableNil extends DatatableObject {

	public DatatableNil() {
		this(0);
	}
	
	public DatatableNil(int key) {
		super(key);
	}
	
	@Override
	public void set(Object value) {
		throw new RuntimeException("This value doesn't exist!");
	}

	@Override
	public boolean compareAndSet(Object expected, Object value) {
		throw new RuntimeException("This value doesn't exist!");
	}
	
	@Override
	public void setFlags(byte flags) {
		throw new RuntimeException("This value doesn't exist!");
	}

	@Override
	public void setPersistant(boolean value) {
		throw new RuntimeException("This value doesn't exist!");
	}

	@Override
	public void setSynced(boolean value) {
		throw new RuntimeException("This value doesn't exist!");
	}

	@Override
	public Serializable get() {
		return null;
	}

	@Override
	public int asInt() {
		return 0;
	}

	@Override
	public float asFloat() {
		return 0;
	}

	@Override
	public boolean asBool() {
		return false;
	}

	@Override
	public byte[] compress() {
		return null;
	}

	@Override
	public void decompress(byte[] compressed) {
		if (compressed != null && compressed.length != 0) {
			throw new IllegalArgumentException("DatatableNil objects can only be represented by null or zero length arrays");
		}
	}

	@Override
	public byte getObjectTypeId() {
		return 0;
	}
	
	@Override
	public DatatableObject newInstance(int key) {
		return new DatatableNil(key);
	}
	
	@Override
	public int fixedLength() {
		return 0;
	}
}
