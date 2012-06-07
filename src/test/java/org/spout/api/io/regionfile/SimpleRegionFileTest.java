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
package org.spout.api.io.regionfile;

import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import org.junit.Test;

import org.spout.api.io.bytearrayarray.BAAClosedException;
import org.spout.api.io.bytearrayarray.ByteArrayArray;

public class SimpleRegionFileTest {
	private static int desiredEntries = 128; // Region.REGION_SIZE * Region.REGION_SIZE *Region.REGION_SIZE;
	private static int chunkBlocks = 128; // Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE;
	private static String filename = "regionfile.dat";

	private byte[][] dataCache = new byte[desiredEntries][];
	private ByteArrayArray srf;

	@Test
	public void test() throws IOException {
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}

		System.out.println("File: " + file.getAbsolutePath());

		srf = new SimpleRegionFile(file, 9, desiredEntries);

		Random r = new Random();

		System.out.println("Randomly reading and writing to the file");

		for (int i = 0; i < desiredEntries * 2; i++) {
			int entry = (r.nextInt() & 0x7FFFFFFF) % desiredEntries;
			assertTrue("Data read from store did not match written data", checkEntryMatch(entry));
			entry = (r.nextInt() & 0x7FFFFFFF) % desiredEntries;
			updateEntry(entry, createFakeChunk(chunkBlocks << 3, 0.15F * r.nextFloat()));
		}

		System.out.println("Closing file");

		assertTrue("Unable to close file after first open", srf.attemptClose());

		System.out.println("Opening file again to test that data was correctly saved to disk");

		srf = new SimpleRegionFile(file, 9, desiredEntries);

		System.out.println("Randomly reading and writing to the file");

		for (int i = 0; i < desiredEntries / 2; i++) {
			int entry = (r.nextInt() & 0x7FFFFFFF) % desiredEntries;
			assertTrue("Data read, after second open, from store did not match written data", checkEntryMatch(entry));
			assertTrue("Data read, after second open, from store did not match written data", checkEntryMatch(i * 2));
			entry = (r.nextInt() & 0x7FFFFFFF) % desiredEntries;
			updateEntry(entry, createFakeChunk(chunkBlocks << 3, 0.15F * r.nextFloat()));
			assertTrue("Data read, after second open, from store did not match written data", checkEntryMatch(1 + (i * 2)));
		}

		int entry = (r.nextInt() & 0x7FFFFFFF) % desiredEntries;
		OutputStream out = srf.getOutputStream(entry);

		System.out.println("Trying to close file with an output stream open");

		assertTrue("File closed even though output stream was open", !srf.attemptClose());

		out.close();

		System.out.println("Trying to close file with all streams closed");

		assertTrue("Unable to close file after second open", srf.attemptClose());

		System.out.println("Checking that exception thrown when writing to a closed file");

		boolean exceptionThrown = false;
		try {
			out = srf.getOutputStream(entry);
		} catch (BAAClosedException e) {
			exceptionThrown = true;
		}

		assertTrue("No exception thrown when trying to get an output stream from a closed file", exceptionThrown);

		System.out.println("Checking that exception thrown when reading from a closed file");

		exceptionThrown = false;
		try {
			srf.getInputStream(entry);
		} catch (BAAClosedException e) {
			exceptionThrown = true;
		}

		assertTrue("No exception thrown when trying to get an input stream from a closed file", exceptionThrown);

		System.out.println("Checking that file doesn't close if timeout hasn't expired (10ms)");

		boolean success = false;

		while (!success) {
			srf = new SimpleRegionFile(file, 9, desiredEntries, 10);
			long startTime = System.currentTimeMillis();
			srf.getInputStream(entry);
			srf.closeIfTimedOut();
			long endTime = System.currentTimeMillis();
			boolean possibleTimeout = (endTime - startTime) >= 5;
			boolean fileClosed = srf.isClosed();
			assertTrue("File was closed even though it should not have timed out", !(fileClosed && !possibleTimeout));
			success = !possibleTimeout;
			if (!srf.isClosed()) {
				assertTrue(srf.attemptClose());
			}
		}

		System.out.println("Checking that file closes if timeout has expired (10ms)");

		srf = new SimpleRegionFile(file, 9, desiredEntries, 10);
		srf.getInputStream(entry);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			assertTrue("Interrupted Exception thrown when waiting for timeout", false);
		}
		srf.closeIfTimedOut();
		assertTrue("File wasn't closed even though it should have timed out", srf.isClosed());
		if (!srf.isClosed()) {
			assertTrue(srf.attemptClose());
		}

		System.out.println("Deleting temp file");
		file.delete();
	}

	private boolean checkEntryMatch(int entry) throws IOException {
		byte[] expected = dataCache[entry];
		if (expected == null) {
			return true;
		}
		//System.out.println("Checking entry " + entry);
		DataInputStream in = new DataInputStream(srf.getInputStream(entry));
		for (int i = 0; ; ++i) {
			final byte b;
			try {
				b = in.readByte();
			} catch (EOFException e) {
				if (i != expected.length) {
					System.out.println("Failed due to to short data " + i + " != " + expected.length);
					return false;
				}

				return true;
			}

			if (i >= expected.length) {
				System.out.println("Failed due to wrong EOF");
				return false;
			}

			if (b != expected[i]) {
				System.out.println("Failed due to data mismatch at position " + i);
				return false;
			}
		}
	}

	private void updateEntry(int entry, byte[] data) throws IOException {
		//System.out.println("Writing " + data.length + " to entry " + entry);
		DataOutputStream out = new DataOutputStream(srf.getOutputStream(entry));
		out.write(data);
		out.close();
		dataCache[entry] = data;
	}

	private static byte[] createFakeChunk(int bufferSize, float nonZero) {
		byte[] buffer = new byte[bufferSize];

		int nonZeroBytes = (int)(nonZero * bufferSize);

		Random r = new Random();

		for (int i = 0; i < nonZeroBytes; i++) {
			buffer[(r.nextInt() & 0x7FFFFFFF) % bufferSize] = (byte)r.nextInt();
		}

		return buffer;
	}
}
