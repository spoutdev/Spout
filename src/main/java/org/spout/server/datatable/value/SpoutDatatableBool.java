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

public class SpoutDatatableBool extends SpoutDatatableObject {
	boolean data;

	public SpoutDatatableBool(int key) {
		super(key);
	}

	public SpoutDatatableBool(int key, boolean value) {
		super(key);
		data = value;
	}

	@Override
	public void set(int key, Object value) {
		throw new IllegalArgumentException("This is an boolean value, use set(string,bool)");
	}

	public void set(String key, boolean value) {
		keyID = key.hashCode();
		data = value;
	}

	@Override
	public Object get() {
		throw new NumberFormatException("this value cannot be expressed as an object");
	}

	@Override
	public int asInt() {
		return data ? 1 : 0;
	}

	@Override
	public float asFloat() {
		throw new NumberFormatException("this value cannot be expressed as an float");
	}

	@Override
	public boolean asBool() {
		return data;
	}

	@Override
	public void output(OutputStream out) throws IOException {

	}

	@Override
	public void input(InputStream in) throws IOException {

	}
}
