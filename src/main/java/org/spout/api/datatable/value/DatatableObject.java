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

import org.spout.api.datatable.DatatableTuple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class DatatableObject implements DatatableTuple {
	public static final byte PERSIST = 0x1;
	public static final byte SYNC = 0x2;

	protected int keyID;
	protected byte flags;
	Serializable data;

	public DatatableObject(int key) {
		keyID = key;
	}

	public DatatableObject(int key, Serializable dat) {
		keyID = key;
		data = dat;
	}

	@Override
	public void set(int key, Object value) {
		keyID = key;
		if (!(value instanceof Serializable)) {
			throw new IllegalArgumentException("Unsupported Metadata type");
		}
		data = (Serializable) value;
	}

	@Override
	public int hashCode() {
		return keyID;
	}

	@Override
	public void setFlags(byte flags) {
		this.flags = flags;
	}

	@Override
	public void setPersistant(boolean value) {
		if (value) {
			flags |= DatatableObject.PERSIST;
		} else {
			flags &= ~DatatableObject.PERSIST;
		}
	}

	@Override
	public void setSynced(boolean value) {
		if (value) {
			flags |= DatatableObject.SYNC;
		} else {
			flags &= ~DatatableObject.SYNC;
		}
	}

	@Override
	public Serializable get() {
		return data;
	}

	@Override
	public int asInt() {
		if (data instanceof Number) {
			return ((Number) data).intValue();
		}
		return 0;
	}

	@Override
	public float asFloat() {
		if (data instanceof Number) {
			return ((Number) data).floatValue();
		}
		return 0;
	}

	@Override
	public boolean asBool() {
		if (data instanceof Boolean) {
			return (Boolean) data;
		}
		return false;
	}

	@Override
	public void output(OutputStream out) throws IOException {

	}

	@Override
	public void input(InputStream in) throws IOException {

	}
}
