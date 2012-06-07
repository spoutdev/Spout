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
package org.spout.api.datatable.value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.spout.api.Spout;

public class DatatableSerializable extends DatatableObject {
	
	public DatatableSerializable(int key) {
		super(key);
	}
	
	public DatatableSerializable(int key, Serializable data) {
		super(key, data);
	}

	@Override
	public byte[] compress() {
		Serializable value = super.get();
		if (value instanceof ByteArrayWrapper) {
			return ((ByteArrayWrapper)value).getArray();
		}

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

		try {
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

			objOut.writeObject(super.get());
			objOut.flush();
			objOut.close();
		} catch (IOException e) {
			if (Spout.debugMode()) {
				e.printStackTrace();
			}
			return null;
		}

		return byteOut.toByteArray();
	}

	@Override
	public void decompress(byte[] compressed) {
		super.set(new ByteArrayWrapper(compressed));
	}
	
	@Override
	public Serializable get() {
		while (true) {
			Serializable s = super.get();
			if (!(s instanceof ByteArrayWrapper)) {
				return super.get();
			}
			try {
				ByteArrayWrapper w = (ByteArrayWrapper)s;
				ObjectInputStream inObj = new ObjectInputStream(new ByteArrayInputStream(w.getArray()));
				Object o;
				try {
					o = inObj.readObject();
				} finally {
					// Overkill
					inObj.close();
				}
				Serializable deserialized = (Serializable)o;
				if (super.compareAndSet(s, deserialized)) {
					return deserialized;
				}
			} catch (IOException e) {
				return null;
			} catch (ClassNotFoundException e) {
				return null;
			} catch (ClassCastException e) {
				return null;
			}
		}
	}
	
	@Override
	public byte getObjectTypeId() {
		return 4;
	}
	
	@Override
	public DatatableObject newInstance(int key) {
		return new DatatableSerializable(key);
	}
	
	public boolean isUnknownClass() {
		Serializable s = super.get();
		if (s == null) {
			return false;
		}

		return s instanceof ByteArrayWrapper;
	}
	
	private static class ByteArrayWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private final byte[] array;
		
		public ByteArrayWrapper(byte[] array) {
			this.array = array;
		}
		
		public byte[] getArray() {
			return array;
		}
		
	}

	@Override
	public int fixedLength() {
		return -1;
	}

}
