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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.datatable.DatatableTuple;
import org.spout.api.util.VarInt;

public abstract class DatatableObject implements DatatableTuple {
	public static final byte PERSIST = 0x1;
	public static final byte SYNC = 0x2;

	protected final AtomicInteger keyID;
	protected final AtomicInteger flags;
	protected final AtomicReference<Serializable> data;
	protected final AtomicBoolean dirty;
	protected final AtomicReference<byte[]> compressed;
	
	private static DatatableObject[] newInstanceArray = new DatatableObject[5];

	public DatatableObject() {
		this(0);
	}
	
	public DatatableObject(int key) {
		this(key, null);
	}

	public DatatableObject(int key, Serializable dat) {
		keyID = new AtomicInteger(key);
		data = new AtomicReference<Serializable>(dat);
		flags = new AtomicInteger((PERSIST | SYNC));
		dirty = new AtomicBoolean(false);
		compressed = new AtomicReference<byte[]>(null);
	}
	
	static {
		register(new DatatableNil(0));
		register(new DatatableBool(0));
		register(new DatatableInt(0));
		register(new DatatableFloat(0));
		register(new DatatableSerializable(0));
	}
	
	private static void register(DatatableObject o) {
		int id = o.getObjectTypeId();
		if (newInstanceArray[id] != null) {
			throw new IllegalStateException("Attempt made to register " + o.getClass().getSimpleName() + 
					" but the id is already in use by " + newInstanceArray[id].getClass().getSimpleName());
		}
		newInstanceArray[id] = o;
	}
	
	public static DatatableObject newInstance(int id, int key) {
		if (id < 0 || id > newInstanceArray.length || newInstanceArray[id] == null) {
			throw new IllegalArgumentException("Datatable object id of " + id + " has no corresponding type");
		}
		return newInstanceArray[id].newInstance(key);
	}

	@Override
	public void set(Object value) {
		if (value != null && !(value instanceof Serializable)) {
			throw new IllegalArgumentException("Unsupported Metadata type");
		}
		data.set((Serializable) value);
	}
	
	@Override
	public void setKey(int key) {
		keyID.set(key);
	}
	
	@Override
	public int getKey() {
		return keyID.get();
	}
	
	@Override
	public boolean compareAndSet(Object expected, Object newValue) {
		if (newValue != null && !(newValue instanceof Serializable)) {
			throw new IllegalArgumentException("Unsupported Metadata type");
		} else if (expected != null && !(expected instanceof Serializable)) {
			return false;
		}
		return data.compareAndSet((Serializable)expected, (Serializable)newValue);
	}

	@Override
	public int hashCode() {
		return keyID.get();
	}

	@Override
	public void setFlags(byte flags) {
		this.flags.set(flags);
	}

	@Override
	public void setPersistant(boolean value) {
		int oldValue;
		int newValue;
		
		do {
			oldValue = this.flags.get();
			if (value) {
				newValue = oldValue | DatatableObject.PERSIST;
			} else {
				newValue = oldValue & ~DatatableObject.PERSIST;
			}	
		} while (!this.flags.compareAndSet(oldValue, newValue));

	}

	@Override
	public void setSynced(boolean value) {
		int oldValue;
		int newValue;
		
		do {
			oldValue = this.flags.get();
			if (value) {
				newValue = oldValue | DatatableObject.SYNC;
			} else {
				newValue = oldValue & ~DatatableObject.SYNC;
			}	
		} while (!this.flags.compareAndSet(oldValue, newValue));
	}

	@Override
	public Serializable get() {
		return data.get();
	}

	@Override
	public int asInt() {
		Object data = this.data.get();
		if (data instanceof Number) {
			return ((Number) data).intValue();
		}
		return 0;
	}

	@Override
	public float asFloat() {
		Object data = this.data.get();
		if (data instanceof Number) {
			return ((Number) data).floatValue();
		}
		return 0;
	}

	@Override
	public boolean asBool() {
		Object data = this.data.get();
		if (data instanceof Boolean) {
			return (Boolean) data;
		}
		return false;
	}
	
	public abstract int fixedLength();
	
	public abstract byte getObjectTypeId();
	
	public abstract DatatableObject newInstance(int key);
	
	public abstract byte[] compress();
	
	public abstract void decompress(byte[] compressed);

	public void output(OutputStream out) throws IOException {
		out.write(getObjectTypeId());
		VarInt.writeInt(out, hashCode());
		byte[] compressed = compress();
		int expectedLength = fixedLength();
		if (expectedLength == -1) {
			VarInt.writeInt(out, compressed != null ? compressed.length : 0);
		} else if (expectedLength != compressed.length) {
			throw new IllegalStateException("Fixed length DatatableObject did not match actual length");
		}
		if (compressed != null) {
			out.write(compressed);
		}
	}

	public static DatatableObject input(InputStream in) throws IOException {
		int typeId = in.read();
		if (typeId == -1) {
			throw new EOFException("InputStream did not contain a DatatableObject");
		}
		int key = VarInt.readInt(in);
		DatatableObject obj = newInstance(typeId, key);
		int expectedLength = obj.fixedLength();
		if (expectedLength == -1) {
			expectedLength = VarInt.readInt(in);
		}
		if (expectedLength > 0) {
			byte[] compressed = new byte[expectedLength];
			while (expectedLength > 0) {
				expectedLength -= in.read(compressed, compressed.length - expectedLength, expectedLength);
			}
			obj.decompress(compressed);
		}
		return obj;
	};
	
}
