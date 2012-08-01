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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.spout.api.io.bytearrayarray.ByteArrayArray;

public class SimpleRegionFile implements ByteArrayArray {
	
	private static final int VERSION = 1;
	private static final int TIMEOUT = 120000; // timeout delay
	public static final int FILE_CLOSED = -1;

	private final File filePath;
	private final CachedRandomAccessFile file;
	@SuppressWarnings("unused")
	private final int version;
	private final int timeout;
	
	private final AtomicInteger[] blockSegmentStart;
	private final AtomicInteger[] blockSegmentLength;
	private final AtomicInteger[] blockActualLength;
	private final SRFReentrantReadWriteLock[] blockLock;
	private final AtomicInteger numberBlocksLocked;
	
	private final AtomicBoolean closed;
	private final AtomicLong lastAccess;
	
	private final AtomicReference<AtomicBoolean[]> inuse;
	private final int segmentSize;
	private final int segmentMask;
	private final int entries;
	
	/**
	 * Creates a SimpleRegionFile
	 * 
	 * @param filePath the path to the file
	 * @param desiredSegmentSize log2(the desired segment size) 
	 * @param entries the number of blocks (sub-files) in the RegionFile
	 * @throws IOException on error
	 */
	public SimpleRegionFile(File filePath, int desiredSegmentSize, int entries) throws IOException {
		this(filePath, desiredSegmentSize, entries, TIMEOUT);
	}
	
	/**
	 * Creates a SimpleRegionFile
	 * 
	 * @param filePath the path to the file
	 * @param desiredSegmentSize log2(the desired segment size) 
	 * @param entries the number of blocks (sub-files) in the RegionFile
	 * @param timeout the time in ms until the file times out for auto-closing
	 * @throws IOException on error
	 */
	public SimpleRegionFile(File filePath, int desiredSegmentSize, int entries, int timeout) throws IOException {
		
		this.filePath = filePath;
		this.closed = new AtomicBoolean(false);
		
		this.timeout = timeout;
		this.lastAccess = new AtomicLong(0);
		refreshAccess();
		
		try {
			this.file = new CachedRandomAccessFile(this.filePath, "rw");
		} catch (FileNotFoundException e) {
			this.closed.set(true);
			throw new SRFException("Unable to open region file " + this.filePath, e);
		}
		
		int headerSize = getHeaderSize(entries);
		
		if (file.length() <= headerSize) {
			file.seek(0);
			file.writeInt(VERSION);
			file.writeInt(desiredSegmentSize);
			file.writeInt(entries);
			for (int i = 0; i < entries << 1; i++) {
				file.writeInt(0);
			}
		}
		
		file.seek(0);
		this.version = file.readInt();
		this.segmentSize = file.readInt();
		this.segmentMask = (1 << this.segmentSize) - 1;
		this.entries = file.readInt();
		
		if (entries != this.entries) {
			file.close();
			this.closed.set(true);
			throw new SRFException("Number of entries mismatch for file " + this.filePath + ", expected " + entries + " got " + this.entries);
		}
		
		inuse = new AtomicReference<AtomicBoolean[]>(new AtomicBoolean[0]);
		
		int headerSegments = sizeToSegments(headerSize);
		
		int segmentsLocked = reserveSegments(0, headerSegments);
		if (segmentsLocked != headerSegments) {
			throw new SRFException("Unabled to lock header segments");
		}
		
		blockSegmentStart = new AtomicInteger[entries];
		blockSegmentLength = new AtomicInteger[entries];
		blockActualLength = new AtomicInteger[entries];
		blockLock = new SRFReentrantReadWriteLock[entries];
		numberBlocksLocked = new AtomicInteger(0);
		
		for (int i = 0; i < entries; i++) {
			blockSegmentStart[i] = new AtomicInteger(file.readInt());
			blockActualLength[i] = new AtomicInteger(file.readInt());
			blockSegmentLength[i] = new AtomicInteger(sizeToSegments(blockActualLength[i].get()));
			blockLock[i] = new SRFReentrantReadWriteLock(numberBlocksLocked);
			int length = reserveSegments(blockSegmentStart[i].get(), blockSegmentLength[i].get());
			if (length != blockSegmentLength[i].get()) {
				throw new SRFException("Reserved segments for Block " + i + " overlap with another block");
			}
		}
		
	}


	@Override
	public boolean exists(int i) throws IOException {
		if (i < 0 || i > entries) {
			throw new SRFException("Read block index out of range");
		}
		refreshAccess();
		Lock lock = blockLock[i].readLock();
		lock.lock();
		try {
			if (this.isClosed()) {
				throw new SRFClosedException("File closed");
			}
			return blockActualLength[i].get() != 0;

		} finally {
			lock.unlock();
		}
	}

	@Override
	public InputStream getInputStream(int i) throws IOException {
		if (i < 0 || i > entries) {
			throw new SRFException("Read block index out of range");
		}
		refreshAccess();
		Lock lock = blockLock[i].readLock();
		lock.lock();
		try {
			if (this.isClosed()) {
				throw new SRFClosedException("File closed");
			}
			if (blockActualLength[i].get() == 0) {
				//This block is of 0 length, and will cause EOF errors if you attempt to make a stream with it.
				return null;
			}

			int start = blockSegmentStart[i].get() << segmentSize;
			int actualLength = blockActualLength[i].get();
			byte[] result = new byte[actualLength];
			synchronized (file) {
				file.seek(start);
				file.readFully(result);
			}
			return new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(result)));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public OutputStream getOutputStream(int i) throws IOException {
		if (i < 0 || i > entries) {
			throw new SRFException("Read block index out of range");
		}
		refreshAccess();
		Lock lock = blockLock[i].writeLock();
		lock.lock();
		if (this.isClosed()) {
			throw new SRFClosedException("File closed");
		}
		return new BufferedOutputStream(new DeflaterOutputStream(new SRFOutputStream(this, i, this.segmentMask + 1, lock)));
	}
	
	/**
	 * Writes a byte array to a block.  This is for internal use only. <br>
	 * <br>
	 * Note: It is assumed that the block is locked when making these changes<br>
	 * 
	 * @param i the block index
	 * @param buf the buffer
	 * @param length the actual block length
	 * @throws IOException
	 */
	void write(int i, byte[] buf, int length) throws IOException {
		refreshAccess();
		int start = reserveBlockSegments(i, length);
		synchronized(file) {
			this.writeFAT(i, start, length);
			file.seek(start << segmentSize);
			file.write(buf, 0, length);
		}
	}
	
	@Override
	public boolean isTimedOut() {
		return this.lastAccess.get() + this.timeout < System.currentTimeMillis();
	}
	
	@Override
	public void closeIfTimedOut() throws IOException {
		if (isTimedOut()) {
			attemptClose();
		}
	}
	
	@Override
	public boolean isClosed() {
		return this.numberBlocksLocked.get() == FILE_CLOSED;
	}
	
	@Override
	public boolean attemptClose() throws IOException {
		refreshAccess();
		if (!this.numberBlocksLocked.compareAndSet(0, FILE_CLOSED)) {
			// Cannot close: either the file is already closed or there are still blocks locked.
			return false;
		}

		synchronized(file) {
			file.close();
		}
		return true;
	}
	
	/**
	 * Gets the size of the header in bytes
	 * 
	 * @param entries the number of entries
	 * @return the header size
	 */
	private static int getHeaderSize(int entries) {
		int headerSize = getFATOffset();
		headerSize += 4 * entries;  // start array (int[entries])
		headerSize += 4 * entries;  // size array (int[entries])
		return headerSize;
	}
	
	/**
	 * Gets the FAT base position
	 * 
	 * @return the base position
	 */
	private static int getFATOffset() {
		int headerSize = 0;
		headerSize += 4;            // Version (int)
		headerSize += 4;            // Segment size (int)
		headerSize += 4;            // entries (int)
		return headerSize;
	}
	
	/**
	 * Updates the last access time.  This can be used to determine if the file should be closed.
	 */
	private void refreshAccess() {
		this.lastAccess.set(System.currentTimeMillis());
	}
	
	/**
	 * Gets the number of segments required to store data of a given length.
	 * 
	 * @param size the size in bytes
	 * @return the number of segments
	 */
	private int sizeToSegments(int size) {
		if (size <= 0) {
			return 0;
		}

		return ((size - 1) >> segmentSize) + 1;
	}
	
	/**
	 * Releases a segment.
	 * 
	 * @param i the segment index
	 * @return true on success
	 */
	private boolean releaseSegment(int i) {
		boolean oldUsed = setInUse(i, false);
		return oldUsed;
	}
	
	/**
	 * Reserves a segment.
	 * 
	 * @param i the segment index
	 * @return true on success
	 */
	private boolean reserveSegment(int i) {
		boolean oldUsed = setInUse(i, true);
		return !oldUsed;
	}
	
	/**
	 * Reserves a group of segments.  If all segments can't be locked, any locked segments are immediately released.
	 * 
	 * @param start the index of the first segment
	 * @param length the number of segments to lock
	 * @return the number of segments locked (this is equal to length on success)
	 * @throws IOException
	 */
	private int reserveSegments(int start, int length) throws IOException {
		int end = start + length;
		for (int i = start; i < end; i++) {
			if (!reserveSegment(i)) {
				for (int j = i - 1; j >= start; j--) {
					if (!releaseSegment(j)) {
						throw new SRFException("Release error when releasing a group of segments that had just been locked");
					}
				}
				return i - start;
			}
		}
		return length;
	}
	
	/**
	 * Reserves a contiguous group of segments for a block.<br>
	 * <br>
	 * If the new length is less than or equal to the old length, then the current allocation is resized down.<br>
	 * <br>
	 * If there is space after the current allocation so that it can be expanded to the new size, then it is expanded.<br>
	 * <br>
	 * Otherwise, it scans from the start until it finds a large enough group of segments.<br>
	 * <br>
	 * This may result in the file length needing to be increased.
	 * 
	 * @param i the block index
	 * @param length the actual length of the new block
	 * @return the start segment that was allocated
	 * @throws IOException
	 */
	private int reserveBlockSegments(int i, int length) throws IOException {
		AtomicInteger blockStart = this.blockSegmentStart[i];
		AtomicInteger blockLength = this.blockSegmentLength[i];
		AtomicInteger blockBytes = this.blockActualLength[i];
		
		int oldStart = blockStart.get();
		int oldLength = blockLength.get();
		int oldEnd = oldStart + oldLength;
		
		int newLength = sizeToSegments(length);
		int newEnd = oldStart + newLength;
		
		if (newLength <= oldLength) { // file has shrunk
			for (int j = newEnd; j < oldEnd; j++) {
				if (!this.releaseSegment(j)) {
					throw new SRFException("Unable to unlock blocks due to file shrinking");
				}
			}
			blockLength.set(newLength);
			blockBytes.set(length);
			return oldStart;
		}
		
		int extraLength = newLength - oldLength;
		int lockedSegments = this.reserveSegments(oldEnd, extraLength);
		
		if (lockedSegments == extraLength) {
			blockLength.set(newLength);
			blockBytes.set(length);
			return oldStart;
		}
		
		int newStart = 0;
		lockedSegments = 0;
		
		while (lockedSegments != newLength) {
			lockedSegments = this.reserveSegments(newStart, newLength);
			if (lockedSegments != newLength) {
				newStart = newStart + lockedSegments + 1;
			}
		}
		
		for (int j = oldStart; j < oldEnd; j++) {
			releaseSegment(j);
		}
		
		blockStart.set(newStart);
		blockLength.set(newLength);
		blockBytes.set(length);
		return newStart;
	}
	
	private void writeFAT(int i, int start, int actualLength) throws IOException {
		int FATEntryPosition = getFATOffset() + (i << 3);
		synchronized(file) {
			file.seek(FATEntryPosition);
			file.writeInt(start);
			file.writeInt(actualLength);
		}
	}
	
	/**
	 * Sets an element in the in use array and returns the old value.
	 * 
	 * @param i the segment index
	 * @param used true if the segment should be in use
	 * @return the old value
	 */
	private boolean setInUse(int i, boolean used) {
		AtomicBoolean[] localArray = inuse.get();
		
		if (localArray.length <= i) {
			expandInUseArray(Math.max(i+1, localArray.length * 3 / 2));
		}
		
		localArray = inuse.get();
		
		return localArray[i].getAndSet(used);
	}
	
	/**
	 * Expands the in use array.  When this method returns, the in use array will be at least newSize elements long
	 * 
	 * @param newSize the desired new size
	 */
	private void expandInUseArray(int newSize) {
		
		boolean success = false;
		
		while (!success) {
			AtomicBoolean[] oldArray = inuse.get();

			if (newSize <= oldArray.length) {
				return;
			}
			
			AtomicBoolean[] newArray = new AtomicBoolean[newSize];
			
			for (int i = 0; i < oldArray.length; i++) {
				newArray[i] = oldArray[i];
			}
			
			for (int i = oldArray.length; i < newSize; i++) {
				newArray[i] = new AtomicBoolean(false);
			}
			
			success = inuse.compareAndSet(oldArray, newArray);
		}
		
	}
}
