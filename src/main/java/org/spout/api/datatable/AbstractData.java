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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.util.VarInt;

abstract class AbstractData{
	public static final byte PERSIST = 0x1;
	public static final byte SYNC = 0x2;

	protected final AtomicInteger keyID;
	protected final AtomicInteger flags;
	protected final AtomicReference<Serializable> data;
	protected final AtomicBoolean dirty;
	protected final AtomicReference<byte[]> compressed;

	private static AbstractData[] newInstanceArray = new AbstractData[5];

	public AbstractData() {
		this(0);
	}

	public AbstractData(int key) {
		this(key, null);
	}

	public AbstractData(int key, Serializable dat) {
		keyID = new AtomicInteger(key);
		data = new AtomicReference<Serializable>(dat);
		flags = new AtomicInteger((PERSIST | SYNC));
		dirty = new AtomicBoolean(false);
		compressed = new AtomicReference<byte[]>(null);
	}

	static {
		register(new NullData(0));
		register(new BooleanData(0));
		register(new IntegerData(0));
		register(new FloatData(0));
		register(new SerializableData(0));
	}

	private static void register(AbstractData o) {
		int id = o.getObjectTypeId();
		if (newInstanceArray[id] != null) {
			throw new IllegalStateException("Attempt made to register " + o.getClass().getSimpleName() + " but the id is already in use by " + newInstanceArray[id].getClass().getSimpleName());
		}
		newInstanceArray[id] = o;
	}

	public static AbstractData newInstance(int id, int key) {
		if (id < 0 || id > newInstanceArray.length || newInstanceArray[id] == null) {
			throw new IllegalArgumentException("Datatable object id of " + id + " has no corresponding type");
		}
		return newInstanceArray[id].newInstance(key);
	}

	public void set(Object value) {
		if (value != null && !(value instanceof Serializable)) {
			throw new IllegalArgumentException("Unsupported Metadata type");
		}
		data.set((Serializable) value);
	}

	public void setKey(int key) {
		keyID.set(key);
	}

	public int getKey() {
		return keyID.get();
	}

	@Override
	public int hashCode() {
		return keyID.get();
	}

	public void setPersistant(boolean value) {
		int oldValue;
		int newValue;

		do {
			oldValue = this.flags.get();
			if (value) {
				newValue = oldValue | AbstractData.PERSIST;
			} else {
				newValue = oldValue & ~AbstractData.PERSIST;
			}
		} while (!this.flags.compareAndSet(oldValue, newValue));

	}

	public void setSynced(boolean value) {
		int oldValue;
		int newValue;

		do {
			oldValue = this.flags.get();
			if (value) {
				newValue = oldValue | AbstractData.SYNC;
			} else {
				newValue = oldValue & ~AbstractData.SYNC;
			}
		} while (!this.flags.compareAndSet(oldValue, newValue));
	}

	public Serializable get() {
		return data.get();
	}

	public abstract int fixedLength();

	public abstract byte getObjectTypeId();

	public abstract AbstractData newInstance(int key);

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

	public static AbstractData input(InputStream in) throws IOException {
		int typeId = in.read();
		if (typeId == -1) {
			throw new EOFException("InputStream did not contain a DatatableObject");
		}
		int key = VarInt.readInt(in);
		AbstractData obj = newInstance(typeId, key);
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
