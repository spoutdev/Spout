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
package org.spout.api.util.list;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ByteCircularBufferFIFOTest {
	@Test
	public void test() {
		ByteCircularBufferFIFO buf = new ByteCircularBufferFIFO();

		writeSequence(buf, 10, 9);

		writeArraySequence(buf, 20, 11);

		readArraySequence(buf, 10, 9);

		writeArraySequence(buf, 30, 45, 13, 69);

		readSequence(buf, 20, 11);

		readArraySequence(buf, 30, 13);

		int b = buf.read();

		assertTrue("Empty FIFO did not readback -1", b == -1);

		writeArraySequence(buf, 40, 45, 17, 69);

		writeSequence(buf, 50, 19);

		buf.skip(17);

		readArraySequence(buf, 50, 77, 19, 111);

		writeSequence(buf, 60, 9);

		long s = buf.skip(11);

		assertTrue("Incorrect number of bytes skipped", s == 9);

		b = buf.read();

		assertTrue("Empty FIFO did not readback -1", b == -1);

		byte[] arr = new byte[30];

		writeSequence(buf, 60, 14);

		int numBytes = buf.read(arr);

		assertTrue("Wrong number of bytes read", numBytes == 14);

		writeSequence(buf, 70, 12);

		numBytes = buf.read(arr, 9, 21);

		assertTrue("Wrong number of bytes read", numBytes == 12);

		buf.trim();

		writeSequence(buf, 80, 94);

		buf.trim();

		writeSequence(buf, 90, 77);

		buf.trim();

		writeSequence(buf, 100, 107);

		buf.trim();

		writeSequence(buf, 110, 214);

		buf.trim();

		readSequence(buf, 80, 94);

		buf.trim();

		readSequence(buf, 90, 77);

		buf.trim();

		readSequence(buf, 100, 107);

		buf.trim();

		readSequence(buf, 110, 214);

		buf.trim();

		b = buf.read();

		assertTrue("Empty FIFO did not readback -1 after trim", b == -1);
	}

	private void writeSequence(ByteCircularBufferFIFO buf, int start, int arrayLength) {
		byte[] a = fillArray(start, arrayLength);
		for (byte b : a) {
			buf.write(b);
		}
	}

	private void readSequence(ByteCircularBufferFIFO buf, int start, int arrayLength) {
		byte[] a = fillArray(start, arrayLength);
		byte[] read = new byte[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			int b = buf.read();
			assertTrue("Empty buffer when reading sequence", b != -1);
			assertTrue("Negative return value when reading sequence", b >= 0);
			read[i] = (byte)b;
		}
		checkArray(a, read, 0, arrayLength, "readSequence");
	}

	private void writeArraySequence(ByteCircularBufferFIFO buf, int start, int arrayLength) {
		byte[] a = fillArray(start, arrayLength);
		buf.write(a);
	}

	private void readArraySequence(ByteCircularBufferFIFO buf, int start, int arrayLength) {
		byte[] a = fillArray(start, arrayLength);
		byte[] read = new byte[arrayLength];
		int numBytes = buf.read(read);
		assertTrue("Incorrect number of bytes read", numBytes == read.length);
		checkArray(a, read, 0, arrayLength, "readArraySequence without offset");
	}

	private void writeArraySequence(ByteCircularBufferFIFO buf, int start, int off, int length, int arrayLength) {
		byte[] a = fillArray(start, length);
		byte[] write = new byte[arrayLength];
		for (int i = off; i < off + length; i++) {
			write[i] = a[i - off];
		}
		buf.write(write, off, length);
	}

	private void readArraySequence(ByteCircularBufferFIFO buf, int start, int off, int length, int arrayLength) {
		byte[] a = fillArray(start, length);
		byte[] read = new byte[arrayLength];
		int numBytes = buf.read(read, off, length);
		assertTrue("Incorrect number of bytes read", numBytes == length);
		checkArray(a, read, off, length, "readArraySequence with offset");
	}

	private void checkArray(byte[] reference, byte[] read, int off, int length, String message) {
		for (int i = 0; i < length; i++) {
			assertTrue("Array mismatch in method " + message, reference[i] == read[off + i]);
		}
	}

	private byte[] fillArray(int start, int length) {
		byte[] a = new byte[length];
		for (int i = 0; i < length; i++) {
			a[i] = (byte)(start + i);
		}
		return a;
	}
}
