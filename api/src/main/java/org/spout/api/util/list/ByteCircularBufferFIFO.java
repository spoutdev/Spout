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
package org.spout.api.util.list;

public class ByteCircularBufferFIFO {
	private static final byte[] emptyArray = new byte[0];
	private byte[] buf;
	private int start = 0;
	private int end = 0;
	private int mask = 0;

	public ByteCircularBufferFIFO() {
		this(16);
	}

	public ByteCircularBufferFIFO(int initialSize) {
		buf = emptyArray;
		resizeBuffer(initialSize);
	}

	/**
	 * Writes a byte to the FIFO
	 *
	 * @param b the byte to add
	 */
	public void write(byte b) {
		if (end - start > mask) {
			resizeBuffer(buf.length << 1);
		}
		buf[(end++) & mask] = b;
	}

	/**
	 * Writes a byte array to the FIFO
	 *
	 * @param array the byte array to add
	 */
	public void write(byte[] array) {
		write(array, 0, array.length);
	}

	/**
	 * Writes a portion of a byte array to the FIFO
	 *
	 * @param array the byte array to add
	 * @param off the index of the first byte to write
	 * @param length the number of bytes to write
	 */
	public void write(byte[] array, int off, int length) {
		if (end - start + length > mask) {
			resizeBuffer(end - start + length);
		}
		int arrayEnd = off + length;
		for (int i = off; i < arrayEnd; i++) {
			buf[(end++) & mask] = array[i];
		}
	}

	/**
	 * Reads a byte from the FIFO
	 *
	 * @return the byte or -1 if the FIFO is empty
	 */
	public int read() {
		if (end <= start) {
			return -1;
		}

		return buf[(start++) & mask] & 0xFF;
	}

	/**
	 * Fills a byte array by reading the FIFO
	 *
	 * @param array the byte array to add
	 */
	public int read(byte[] array) {
		return read(array, 0, array.length);
	}

	/**
	 * Fills a portion of a byte array by reading the FIFO
	 *
	 * @param array the byte array to add
	 * @param off the index of the first byte to write
	 * @param length the maximum number of bytes to write
	 * @return the number of bytes read
	 */
	public int read(byte[] array, int off, int length) {
		int lengthPlusOffset = length + off;
		for (int i = off; i < lengthPlusOffset; i++) {
			if (end <= start) {
				return i - off;
			}
			array[i] = buf[(start++) & mask];
		}
		return length;
	}

	/**
	 * Skips the given number of bytes
	 */
	public long skip(long n) {
		int length = end - start;
		if (length < n) {
			n = length;
		}
		start += n;
		return n;
	}

	/**
	 * Converts the data stored in the FIFO into a byte array.  The next byte to be read is placed at position 0.
	 *
	 * @return a byte array representation of the array
	 */
	public byte[] toByteArray() {
		if (end < start) {
			return emptyArray;
		}

		byte[] a = new byte[end - start];
		int j = 0;
		for (int i = start; i < end; i++) {
			a[j++] = buf[i & mask];
		}
		return a;
	}

	/**
	 * Reduces the internal array to the minimum size required to hold the FIFO data
	 */
	public void trim() {
		resizeBuffer(0);
	}

	private void resizeBuffer(int newSize) {
		if (end - start > newSize) {
			newSize = end - start;
		}
		newSize = nextPow2(newSize);
		if (newSize != buf.length) {
			byte[] newBuf = new byte[newSize];
			int j = 0;
			for (int i = start; i < end; i++) {
				newBuf[j++] = buf[i & mask];
			}
			mask = newSize - 1;
			buf = newBuf;
			end -= start;
			start = 0;
		}
	}

	/**
	 * Returns the lowest power of 2 that is greater than the given integer
	 */
	private int nextPow2(int x) {
		if (x <= 0) {
			return 1;
		}

		x -= 1;
		x |= (x >> 1);
		x |= (x >> 2);
		x |= (x >> 4);
		x |= (x >> 8);
		x |= (x >> 16);
		return x + 1;
	}
}
