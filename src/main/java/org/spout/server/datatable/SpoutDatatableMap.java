/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.datatable;

import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.DatatableTuple;
import org.spout.api.util.StringMap;
import org.spout.server.datatable.value.SpoutDatatableObject;

public class SpoutDatatableMap implements DatatableMap {
	final StringMap stringmap;
	TSynchronizedIntObjectMap<DatatableTuple> map = new TSynchronizedIntObjectMap<DatatableTuple>(new TIntObjectHashMap<DatatableTuple>());

	public SpoutDatatableMap(StringMap stringmap) {
		this.stringmap = stringmap;
	}

	@Override
	public void set(DatatableTuple value) {
		map.put(value.hashCode(), value);

	}

	public int getKey(String key) {
		return stringmap.register(key);
	}

	@Override
	public DatatableTuple get(String key) {
		return map.get(getKey(key));
	}

	@Override
	public byte[] compress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decompress(byte[] compressedData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void output(OutputStream out) throws IOException {
		

	}

	@Override
	public void input(InputStream in) throws IOException {
		
	}

}
