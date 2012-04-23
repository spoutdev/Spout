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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.datatable.DatatableTuple;

public abstract class DatatableObject implements DatatableTuple {
	public static final byte PERSIST = 0x1;
	public static final byte SYNC = 0x2;

	protected final int keyID;
	protected final AtomicInteger flags;
	protected final AtomicReference<Serializable> data;
	protected final AtomicBoolean dirty;
	protected final AtomicReference<byte[]> compressed;

	public DatatableObject(int key) {
		this(key, null);
	}

	public DatatableObject(int key, Serializable dat) {
		keyID = key;
		data = new AtomicReference<Serializable>(dat);
		flags = new AtomicInteger(0);
		dirty = new AtomicBoolean(false);
		compressed = new AtomicReference<byte[]>(null);
	}

	@Override
	public void set(Object value) {
		if (!(value instanceof Serializable)) {
			throw new IllegalArgumentException("Unsupported Metadata type");
		}
		data.set((Serializable) value);
	}

	@Override
	public int hashCode() {
		return keyID;
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
		return data;
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
	
	public abstract byte[] compress();
	
	public abstract void decompress(byte[] compressed);
	
	public void output(OutputStream out) throws IOException {
	};

	public void input(InputStream in) throws IOException {
	};

}
