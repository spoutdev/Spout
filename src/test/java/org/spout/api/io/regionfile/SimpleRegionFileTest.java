package org.spout.api.io.regionfile;

import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
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
		DataOutputStream out = srf.getOutputStream(entry);
		
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
		} else {
			//System.out.println("Checking entry " + entry);
			DataInputStream in = srf.getInputStream(entry);
			int i = 0;
			boolean eof = false;
			while (!eof) {
				byte b;
				try {
					b = in.readByte();
				} catch (EOFException e) {
					eof = true;
					continue;
				}
				if (i >= expected.length || b != expected[i++]) {
					if (i >= expected.length) {
						System.out.println("Failed due to wrong EOF");
					} else {
						System.out.println("Failed due to data mismatch at position " + i);
					}
					return false;
				}
			}
			if (i != expected.length) {
				System.out.println("Failed due to to short data " + i + " != " + expected.length);
			}
			return i == expected.length;
		}
	}
	
	private void updateEntry(int entry, byte[] data) throws IOException {
		//System.out.println("Writing " + data.length + " to entry " + entry);
		DataOutputStream out = srf.getOutputStream(entry);
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
