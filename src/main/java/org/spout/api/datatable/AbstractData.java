/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
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

public abstract class AbstractData {
	public static final byte PERSIST = 0x1;
	public static final byte SYNC = 0x2;

	protected final AtomicInteger keyID;
	protected final AtomicInteger flags;
	protected final AtomicBoolean dirty;
	protected final AtomicReference<byte[]> compressed;

	public AbstractData(int key) {
		keyID = new AtomicInteger(key);
		flags = new AtomicInteger((PERSIST | SYNC));
		dirty = new AtomicBoolean(false);
		compressed = new AtomicReference<byte[]>(null);
	}

	public abstract void set(Serializable value);

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

	public abstract Serializable get();

	public abstract int fixedLength();

	public abstract byte[] compress();

	public abstract void decompress(byte[] compressed);

	public void output(OutputStream out) throws IOException {
		out.write(DataRegistry.getId(this));
		VarInt.writeInt(out, hashCode());
		byte[] compressed = compress();
		int expectedLength = fixedLength();
		if (expectedLength == -1) {
			VarInt.writeInt(out, compressed != null ? compressed.length : 0);
		} else if (expectedLength != (compressed == null ? 0 : compressed.length)) {
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
		
		AbstractData obj = DataRegistry.getData(typeId).newInstance(key);
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
