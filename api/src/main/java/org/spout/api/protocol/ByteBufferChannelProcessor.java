/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.protocol;

/**
 * Represents a processor that acts as a pass-through, backed by a byte array
 */
public class ByteBufferChannelProcessor extends CommonChannelProcessor {
	private byte[] internalBuffer;
	private int writePointer;
	private int readPointer;
	private boolean full;

	public ByteBufferChannelProcessor(int capacity) {
		super(capacity);
	}

	@Override
	protected void write(byte[] buf, int length) {
		if (length > buf.length) {
			throw new ArrayIndexOutOfBoundsException(length + " exceeds the size of the byte array " + buf.length);
		}

		int toCopy = length;

		if (internalBuffer == null) {
			internalBuffer = new byte[capacity << 1];
			readPointer = 0;
			writePointer = 0;
			full = false;
		}
		if (freeSpace() < length) {
			throw new IllegalStateException("Internal buffer ran out of memory");
		}
		int toTransfer = Math.min(length, internalBuffer.length - writePointer);
		System.arraycopy(buf, 0, internalBuffer, writePointer, toTransfer);
		writePointer = (writePointer + toTransfer) % internalBuffer.length;

		length -= toTransfer;

		if (length > 0) {
			System.arraycopy(buf, toTransfer, internalBuffer, writePointer, length);
			writePointer = (writePointer + length) % internalBuffer.length;
		}

		if (writePointer == readPointer && toCopy > 0) {
			full = true;
		}
	}

	@Override
	protected int read(byte[] buf) {
		final int toCopy = Math.min(stored(), buf.length);
		final int toTransfer = Math.min(toCopy, internalBuffer.length - readPointer);
		int length = toCopy;

		System.arraycopy(internalBuffer, readPointer, buf, 0, toTransfer);
		readPointer = (readPointer + toTransfer) % internalBuffer.length;

		length -= toTransfer;

		if (length > 0) {
			System.arraycopy(internalBuffer, 0, buf, toTransfer, length);
			readPointer = (readPointer + length) % internalBuffer.length;
		}

		if (toCopy > 0) {
			full = false;
		}
		return toCopy;
	}

	private int stored() {
		if (full) {
			return internalBuffer.length;
		}

		if (writePointer >= readPointer) {
			return writePointer - readPointer;
		}

		return internalBuffer.length - (readPointer - writePointer);
	}

	private int freeSpace() {
		if (full) {
			return 0;
		}

		if (writePointer >= readPointer) {
			return internalBuffer.length - (writePointer - readPointer);
		}

		return readPointer - writePointer;
	}
}
