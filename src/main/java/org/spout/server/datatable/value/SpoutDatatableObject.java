/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.server.datatable.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.spout.api.datatable.DatatableTuple;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;

public class SpoutDatatableObject implements DatatableTuple {
	public static final byte Persist = 0x1;
	public static final byte Sync = 0x2;
	protected int keyID;
	protected byte flags;
	Object data;

	public SpoutDatatableObject(int key) {
		keyID = key;
	}

	public SpoutDatatableObject(int key, Object dat) {
		keyID = key;
		data = dat;
	}

	@Override
	public void set(int key, Object value) {
		keyID = key;
		if (!(value instanceof Vector3) || !(value instanceof Vector2) || !(value instanceof Quaternion)) {
			throw new IllegalArgumentException("Unsuported Metadata type");
		}
		data = value;
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
			flags &= SpoutDatatableObject.Persist;
		} else {
			flags &= ~SpoutDatatableObject.Persist;
		}
	}

	@Override
	public void setSynced(boolean value) {
		if (value) {
			flags &= SpoutDatatableObject.Sync;
		} else {
			flags &= ~SpoutDatatableObject.Sync;
		}
	}

	@Override
	public Object get() {
		return data;
	}

	@Override
	public int asInt() {
		throw new NumberFormatException("Cannot represent Object as int");
	}

	@Override
	public float asFloat() {
		throw new NumberFormatException("Cannot represent Object as float");
	}

	@Override
	public boolean asBool() {
		throw new NumberFormatException("Cannot represent Object as boolean");
	}

	@Override
	public void output(OutputStream out) throws IOException {

	}

	@Override
	public void input(InputStream in) throws IOException {

	}
}
